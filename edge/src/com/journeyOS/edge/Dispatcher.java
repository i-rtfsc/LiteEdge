/*
 * Copyright (c) 2018 anqi.huang@outlook.com
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

import android.content.Context;
import android.content.Intent;

import com.journeyOS.base.utils.AppUtils;
import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.base.utils.Singleton;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.GlobalType;
import com.journeyOS.core.api.edge.IEdge;
import com.journeyOS.core.api.edgeprovider.IGestureProvider;
import com.journeyOS.core.api.thread.ICoreExecutors;
import com.journeyOS.core.database.gesture.Gesture;
import com.journeyOS.core.type.FingerDirection;
import com.journeyOS.edge.music.MusicManager;
import com.journeyOS.i007Service.core.accessibility.AccessibilityManager;
import com.journeyOS.plugins.pay.PayModel;

import es.dmoral.toasty.Toasty;

public class Dispatcher {
    private static final String TAG = Dispatcher.class.getSimpleName();
    private Context mContext;

    private Dispatcher() {
        mContext = CoreManager.getDefault().getContext();
    }

    private static final Singleton<Dispatcher> gDefault = new Singleton<Dispatcher>() {
        @Override
        protected Dispatcher create() {
            return new Dispatcher();
        }
    };

    public static Dispatcher getDefault() {
        return gDefault.get();
    }

    public void handleGestureDirection(final FingerDirection fingerDirection) {
        LogUtils.d(TAG, "gesture fingerDirection = " + fingerDirection);
        final int orientation = mContext.getResources().getConfiguration().orientation;
        CoreManager.getDefault().getImpl(ICoreExecutors.class).diskIOThread().execute(new Runnable() {
            @Override
            public void run() {
                String direction = CoreManager.getDefault().getImpl(IGestureProvider.class).encodeItem(fingerDirection, orientation);
                LogUtils.d(TAG, "direction = " + direction);
                final Gesture gesture = CoreManager.getDefault().getImpl(IGestureProvider.class).getConfig(direction);
                if (gesture != null) {
                    LogUtils.d(TAG, "gesture = " + gesture.toString());
                    CoreManager.getDefault().getImpl(ICoreExecutors.class).mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            LogUtils.d(TAG, "gesture type = " + gesture.type);
                            switch (gesture.type) {
                                case GlobalType.EDGE:
                                    int edgeDirection = Integer.parseInt(gesture.action);
                                    if (edgeDirection != -1) {
                                        CoreManager.getDefault().getImpl(IEdge.class).showingEdge(edgeDirection);
                                    }
                                    break;
                                case GlobalType.KEY:
                                    if (AppUtils.isServiceEnabled(mContext)) {
                                        int key = Integer.parseInt(gesture.action);
                                        AccessibilityManager.getDefault().performGlobalAction(key);
                                    } else {
                                        Toasty.warning(mContext, mContext.getString(R.string.hasnot_permission) + mContext.getString(R.string.accessibility)).show();
                                    }
                                    break;
                                case GlobalType.MUSIC:
                                    switch (gesture.action) {
                                        case MusicManager.MUSIC_LAST:
                                            MusicManager.getDefault().last();
                                            break;
                                        case MusicManager.MUSIC_PLAY:
                                            MusicManager.getDefault().play();
                                            break;
                                        case MusicManager.MUSIC_NEXT:
                                            MusicManager.getDefault().next();
                                            break;
                                    }
                                    break;
                                case GlobalType.PAY:
                                    Intent intent = null;
                                    switch (gesture.action) {
                                        case PayModel.ALIPAY_SCAN:
                                            intent = PayModel.alipayScan();
                                            break;
                                        case PayModel.ALIPAY_QRCODE:
                                            intent = PayModel.alipayBarcode();
                                            break;
                                        case PayModel.ALIPAY_CAR_CODE:
                                            intent = PayModel.alipayCarcode();
                                            break;
                                        case PayModel.TENCENT_MM_SCAN:
                                            intent = PayModel.weChatScan();
                                            break;
                                    }
                                    if (intent != null) {
                                        String packageName = intent.getPackage();
                                        if (AppUtils.isPackageExisted(mContext, packageName)) {
                                            AppUtils.startApp(mContext, intent);
                                        } else {
                                            Toasty.warning(mContext, mContext.getString(R.string.app_not_existed)).show();
                                        }
                                    }
                                    break;
                                case GlobalType.APP:
                                    AppUtils.startApp(mContext, gesture.action);
                                    break;
                                default:
                                    break;
                            }
                        }
                    });
                }
            }
        });
    }
}
