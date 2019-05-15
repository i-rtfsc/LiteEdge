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

package com.journeyOS.core.push;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Bitmap;

import com.journeyOS.base.utils.JsonHelper;
import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.base.utils.Singleton;
import com.journeyOS.base.utils.UIUtils;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.R;
import com.journeyOS.core.Version;
import com.journeyOS.core.api.barrage.IBarrage;
import com.journeyOS.core.api.thread.ICoreExecutors;

import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.PushListener;

public class PushManager {
    private static final String TAG = PushManager.class.getSimpleName();
    private static final String APP_WEBSITE = "https://www.coolapk.com/apk/com.journeyOS.edge";
    private static final String DEVICE_TYPE = "deviceType";
    private static final String DEVICE = "android";

    Context mContext;

    private PushManager() {
        mContext = CoreManager.getDefault().getContext();
    }

    private static final Singleton<PushManager> gDefault = new Singleton<PushManager>() {
        @Override
        protected PushManager create() {
            return new PushManager();
        }
    };

    public static PushManager getDefault() {
        return gDefault.get();
    }

    public void notifyAllUpdate() {
        BmobPushManager bmobPushManager = new BmobPushManager();
        BmobQuery<BmobInstallation> query = BmobInstallation.getQuery();
        query.addWhereEqualTo(DEVICE_TYPE, DEVICE);
        //给指定设备推送，这个是我的三星手机
        //query.addWhereEqualTo("installationId", "F5D67F11B04236B432A2CE98FFAFB3A3");
        //query.addWhereEqualTo("installationId", BmobInstallationManager.getInstallationId());
        bmobPushManager.setQuery(query);
        PushMessage message = new PushMessage();
        message.what = PushMessage.MSG_CHECK_UPDATE;
        message.versionCode = Version.getVersionCode(mContext);
        message.title = "Edge新版本";
        message.msg = "打开酷安或者浏览器并粘贴即可下载！";
        String json = JsonHelper.toJson(message);
        LogUtils.d(TAG, "notify all devices update edge = " + json);
        bmobPushManager.pushMessage(json, new PushListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    LogUtils.d(TAG, "push success");
                } else {
                    LogUtils.d(TAG, "push fail = " + e.getMessage());
                }
            }
        });
    }

    public void handleMessage(String pushMsg) {
        if (pushMsg == null) {
            LogUtils.w(TAG, "push message was null");
        }

        if (pushMsg.contains("alert")) {
            Alert alert = JsonHelper.fromJson(pushMsg, Alert.class);
            pushMsg = alert.alert;
        }

        PushMessage message = JsonHelper.fromJson(pushMsg, PushMessage.class);

        if (message == null) {
            LogUtils.w(TAG, "push real message was null");
            return;
        }

        switch (message.what) {
            case PushMessage.MSG_CHECK_UPDATE:
                dispatchUpdate(message);
                break;
        }
    }

    private void dispatchUpdate(final PushMessage message) {
        LogUtils.w(TAG, "current = " + Version.getVersionCode(mContext));
        boolean needUpdate = (message.versionCode > Version.getVersionCode(mContext));
        if (!needUpdate) {
            LogUtils.d(TAG, "don't need update!");
            return;
        }

        CoreManager.getDefault().getImpl(ICoreExecutors.class).mainThread().execute(new Runnable() {
            @Override
            public void run() {
                ClipboardManager cm = (ClipboardManager) mContext.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(null, APP_WEBSITE);
                cm.setPrimaryClip(clip);

                Bitmap bitmap = UIUtils.drawableToBitmap(mContext.getResources().getDrawable(R.drawable.svg_core_ball));
                Bitmap circleBitmap = UIUtils.getCircularBitmap(bitmap);
                CoreManager.getDefault().getImpl(IBarrage.class).sendBarrage(circleBitmap, message.title, message.msg, false);
            }
        });
    }
}
