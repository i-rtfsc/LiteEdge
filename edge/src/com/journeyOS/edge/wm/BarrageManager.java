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

package com.journeyOS.edge.wm;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Toast;

import com.journeyOS.barrage.BarrageParentView;
import com.journeyOS.barrage.BarrageView;
import com.journeyOS.base.Constant;
import com.journeyOS.base.persistence.SpUtils;
import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.base.utils.Singleton;
import com.journeyOS.base.utils.UIUtils;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.StateMachine;
import com.journeyOS.core.permission.IPermission;
import com.journeyOS.core.type.BarrageState;
import com.journeyOS.edge.R;
import com.journeyOS.edge.barrage.BarrageEntity;
import com.journeyOS.edge.barrage.BarrageHelper;
import com.journeyOS.edge.music.MusicManager;
import com.journeyOS.i007Service.core.notification.Notification;

import es.dmoral.toasty.Toasty;

public class BarrageManager {
    private static final String TAG = BarrageManager.class.getSimpleName();

    private Context mContext;
    private WindowManager mWm;

    private BarrageParentView mRootView;
    private BarrageView mBarrageView;
    private BarrageHelper mBarrageHelper;

    private boolean isAttachedToWindow = false;

    private String mOngingPackageName = null;

    private BarrageManager() {
        mContext = CoreManager.getDefault().getContext();
        mWm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
    }

    private static final Singleton<BarrageManager> gDefault = new Singleton<BarrageManager>() {
        @Override
        protected BarrageManager create() {
            return new BarrageManager();
        }
    };

    public static BarrageManager getDefault() {
        return gDefault.get();
    }

    public void initBarrage() {
        LogUtils.d(TAG, "wanna showing barrage");
        if (!CoreManager.getDefault().getImpl(IPermission.class).canDrawOverlays(mContext)) {
            String message = mContext.getString(R.string.hasnot_permission) + mContext.getString(R.string.overflow);
            Toasty.warning(mContext, message, Toast.LENGTH_SHORT).show();
            SpUtils.getInstant().put(Constant.BALL, false);
            return;
        }
        if (mRootView == null) {
            mRootView = (BarrageParentView) View.inflate(mContext, R.layout.barrage_layout, null);
            mBarrageView = (BarrageView) mRootView.findViewById(R.id.barrage);
            mBarrageView.prepare();
//            if (!mRootView.isAttachedToWindow()) {
//                WindowManager.LayoutParams layoutParams = getLayoutParams();
//                mWm.addView(mRootView, layoutParams);
//            }
        }
        if (mBarrageHelper == null) {
            mBarrageHelper = new BarrageHelper(mContext);
        }

        if (mBarrageView != null && mBarrageHelper != null) {
            mBarrageHelper.add(mBarrageView);
        }

        mBarrageView.setOnBarrageAttachStateChangeListenerListener(new BarrageView.OnBarrageAttachStateChangeListenerListener() {
            @Override
            public void onShowing() {
                LogUtils.d(TAG, "barrage view show");
            }

            @Override
            public void onHiding() {
                StateMachine.setBarrageState(BarrageState.HIDE);
                LogUtils.d(TAG, "wann remove barrage view!");
                if (mRootView != null && mRootView.isAttachedToWindow()) {
                    mWm.removeView(mRootView);
                    mBarrageHelper.release();
                    mBarrageHelper = null;
                    mRootView = null;
                    isAttachedToWindow = false;
                }
            }
        });
    }

    public void sendBarrage(Notification notification) {
        StateMachine.setBarrageState(BarrageState.SHOW);
        setPackageName(notification.getPackageName());
        BarrageEntity barrageEntity = new BarrageEntity();
        barrageEntity.packageName = notification.getPackageName();
        barrageEntity.level = 100;
        barrageEntity.type = BarrageEntity.BARRAGE_TYPE_USERCHAT;
        String title = notification.getTitle();
        if (title != null && title.length() > 10) {
            title = title.substring(0, 10) + "...";
        }
        barrageEntity.name = title;
        barrageEntity.text = notification.getText();

        if (SpUtils.getInstant().getBoolean(Constant.BARRAGE_ICONO, Constant.BARRAGE_ICONO_DEFAULT)) {
            int iconId = notification.extras.getInt(Notification.EXTRA_SMALL_ICON);
            LogUtils.d(TAG, "get icon from notification, icon id = " + iconId);
            if (iconId > 0) {
                //https://stackoverflow.com/questions/40325307/how-to-get-an-image-from-another-apps-notification
                //Resources resources = mContext.getPackageManager().getResourcesForApplication(notification.getPackageName());
                try {
                    Resources resources = mContext.getPackageManager().getResourcesForApplication(MusicManager.MUSIC_QQ);
                    Drawable icon = resources.getDrawable(iconId);
                    if (icon != null) {
                        barrageEntity.avatar = UIUtils.getCircularBitmap(UIUtils.drawableToBitmap(icon));
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        sendBarrage(barrageEntity);
    }

    public void sendBarrage(BarrageEntity barrageEntity) {
        if (mRootView == null) {
            initBarrage();
        }

        if (mRootView != null) {
            if (!isAttachedToWindow) {
                WindowManager.LayoutParams layoutParams = getLayoutParams();
                mWm.addView(mRootView, layoutParams);
                isAttachedToWindow = true;
            }

            if (mBarrageHelper != null) {
                mBarrageHelper.addBarrage(barrageEntity, true);
            }
        }
    }

    public void sendBarrageTest() {
        if (mRootView == null) {
            initBarrage();
        }
        Bitmap bitmap = UIUtils.drawableToBitmap(mContext.getResources().getDrawable(R.mipmap.user));
        Bitmap circleBitmap = UIUtils.getCircularBitmap(bitmap);
        sendBarrage(circleBitmap, "用户名", "弹幕消息测试~");
    }

    public void sendBarrage(Bitmap bitmap, String name, String text) {
        if (mRootView != null) {
            if (!isAttachedToWindow) {
                WindowManager.LayoutParams layoutParams = getLayoutParams();
                mWm.addView(mRootView, layoutParams);
                isAttachedToWindow = true;
            }

            BarrageEntity barrageEntity = new BarrageEntity();
            barrageEntity.type = BarrageEntity.BARRAGE_TYPE_USERCHAT;
            barrageEntity.name = name;
            barrageEntity.avatar = bitmap;
            barrageEntity.level = 100;
            barrageEntity.text = text;
            if (mBarrageHelper != null) {
                mBarrageHelper.addBarrage(barrageEntity, true);
            }
        }
    }

    private LayoutParams getLayoutParams() {
        LayoutParams params = new LayoutParams();
        if (Build.VERSION.SDK_INT >= 26) {
            params.type = LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            params.type = LayoutParams.TYPE_TOAST;
        }
        params.format = PixelFormat.TRANSPARENT;
        params.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
                | LayoutParams.FLAG_NOT_FOCUSABLE
                | LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | LayoutParams.FLAG_SPLIT_TOUCH;
        params.gravity = Gravity.TOP;
        params.y = UIUtils.getStatusBarHeight(mContext) / 4;
        params.width = LayoutParams.MATCH_PARENT;
        params.height = 110;
        return params;
    }

    public String getPackageName() {
        return mOngingPackageName;
    }

    public void setPackageName(String packageName) {
        this.mOngingPackageName = packageName;
    }
}
