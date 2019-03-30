/*
 * Copyright (c) 2019 anqi.huang@outlook.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.journeyOS.edge;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.service.notification.NotificationListenerService.RankingMap;
import android.service.notification.StatusBarNotification;

import com.journeyOS.base.Constant;
import com.journeyOS.base.persistence.SpUtils;
import com.journeyOS.base.utils.AppUtils;
import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.base.utils.Singleton;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.api.edgeprovider.IAppProvider;
import com.journeyOS.core.api.thread.ICoreExecutors;
import com.journeyOS.core.database.app.App;
import com.journeyOS.core.permission.IPermission;
import com.journeyOS.edge.music.MusicManager;
import com.journeyOS.edge.wm.BarrageManager;
import com.journeyOS.i007Service.core.ServiceLifecycleListener;
import com.journeyOS.i007Service.core.notification.Notification;
import com.journeyOS.i007Service.core.notification.NotificationListener;
import com.journeyOS.i007Service.core.notification.NotificationListenerService;

import java.util.List;

public class NotificationManager implements ServiceLifecycleListener, NotificationListener {
    private static final String TAG = NotificationManager.class.getSimpleName();

    private static final String I007_PACKAGE = "com.journeyOS.i007Service";
    private static final String I007_CLASS_NOTIFICATION = "com.journeyOS.i007Service.core.notification.NotificationListenerService";

    private Context mContext;

    final H mHandler = H.getDefault().getHandler();

    private NotificationManager() {
        mContext = CoreManager.getDefault().getContext();
        startNotificationService();
    }

    private static final Singleton<NotificationManager> gDefault = new Singleton<NotificationManager>() {
        @Override
        protected NotificationManager create() {
            return new NotificationManager();
        }
    };

    public static NotificationManager getDefault() {
        return gDefault.get();
    }


    public void startNotificationService() {
        boolean isRunning = (NotificationListenerService.getInstance() != null);
        LogUtils.d(TAG, "start notification service, is service running = " + isRunning);
        if (!isRunning) {
            Intent intent = new Intent(mContext, NotificationListenerService.class);
            mContext.startService(intent);
        }
        toggleNotificationListenerService();

        if (mHandler.hasMessages(H.MSG_BARRAGE_NOTIFICATION)) {
            mHandler.removeMessages(H.MSG_BARRAGE_NOTIFICATION);
        }
        mHandler.sendEmptyMessageDelayed(H.MSG_BARRAGE_NOTIFICATION, H.DELAY_TIME);
    }


    void toggleNotificationListenerService() {
        //https://blog.csdn.net/qq_40788686/article/details/82898936
        ComponentName cn = new ComponentName(mContext.getPackageName(), I007_CLASS_NOTIFICATION);
        PackageManager pm = mContext.getPackageManager();
        pm.setComponentEnabledSetting(cn, PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(cn, PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
    }

    void handleNotification() {
        boolean isRunning = (NotificationListenerService.getInstance() != null);
        LogUtils.d(TAG, "handle notification, is service running = " + isRunning);
        if (!isRunning) {
            if (mHandler.hasMessages(H.MSG_BARRAGE_START_SERVICE)) {
                mHandler.removeMessages(H.MSG_BARRAGE_START_SERVICE);
            }
            mHandler.sendEmptyMessageDelayed(H.MSG_BARRAGE_START_SERVICE, 0);
            return;
        }
        LogUtils.d(TAG, "handle notification...");

        NotificationListenerService.getInstance().removeLifecycleListener(this);
        NotificationListenerService.getInstance().removeListener(this);

        NotificationListenerService.getInstance().addLifecycleListener(this);
        NotificationListenerService.getInstance().addListener(this);
    }

    @Override
    public void onRunning() {
        LogUtils.d(TAG, "notification service running");
    }

    @Override
    public void onStoping() {
        LogUtils.d(TAG, "notification service stoping, wanna start it again!");
        mHandler.sendEmptyMessageDelayed(H.MSG_BARRAGE_START_SERVICE, 0);
    }

    @Override
    public void onNotification(StatusBarNotification sbn, final Notification notification) {
        if (sbn != null) {
            if (MusicManager.MUSIC_NETEASE.equals(notification.getPackageName())
                    || MusicManager.MUSIC_QQ.equals(notification.getPackageName())
                    || MusicManager.MUSIC_XIAMI.equals(notification.getPackageName())) {
                MusicManager.getDefault().onNotification(sbn);
            }
        }

        if (!SpUtils.getInstant().getBoolean(Constant.BARRAGE, Constant.BARRAGE_DEFAULT)) {
            LogUtils.d(TAG, "barrage toggle was false");
            return;
        }

        LogUtils.d(TAG, "notification = [" + notification.toString() + "]");

        if (!CoreManager.getDefault().getImpl(IPermission.class).canDrawOverlays(mContext)
                && !CoreManager.getDefault().getImpl(IPermission.class).hasListenerNotification(mContext)) {
            return;
        }

        CoreManager.getDefault().getImpl(ICoreExecutors.class).diskIOThread().execute(new Runnable() {
            @Override
            public void run() {
                String packageName = notification.getPackageName();
                if (packageName != null) {
                    App app = CoreManager.getDefault().getImpl(IAppProvider.class).getApp(packageName);
                    if (app == null) {
                        LogUtils.d(TAG, "app was null");
                        PackageManager pm = CoreManager.getDefault().getContext().getPackageManager();
                        final Intent intent = new Intent(Intent.ACTION_MAIN, null);
                        //it should appear in the Launcher as a top-level application
                        intent.addCategory(Intent.CATEGORY_LAUNCHER);
                        intent.setPackage(packageName);
                        List<ResolveInfo> resolveInfoList = pm.queryIntentActivities(intent, 0);
                        for (ResolveInfo ri : resolveInfoList) {
                            if (packageName.equals(ri.activityInfo.packageName)) {
                                app = new App();
                                app.appName = AppUtils.getAppName(CoreManager.getDefault().getContext(), packageName);
                                app.packageName = packageName;
                                app.barrage = 1;
                                CoreManager.getDefault().getImpl(ICoreExecutors.class).mainThread().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        BarrageManager.getDefault().sendBarrage(notification);
                                    }
                                });
                            } else {
                                LogUtils.d(TAG, "don't need send barrage");
                            }
                        }

                        return;
                    }
                    if (app.barrage == 1) {
                        CoreManager.getDefault().getImpl(ICoreExecutors.class).mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                BarrageManager.getDefault().sendBarrage(notification);
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn, RankingMap rankingMap) {
        if (sbn != null) {
            if (MusicManager.MUSIC_NETEASE.equals(sbn.getPackageName())
                    || MusicManager.MUSIC_QQ.equals(sbn.getPackageName())) {
                MusicManager.getDefault().onNotificationRemoved(sbn);
            }
        }
    }
}
