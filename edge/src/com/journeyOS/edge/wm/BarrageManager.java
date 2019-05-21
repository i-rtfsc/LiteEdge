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
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.journeyOS.base.Constant;
import com.journeyOS.base.barrage.BarrageController;
import com.journeyOS.base.barrage.BarrageDispatcher;
import com.journeyOS.base.barrage.BarrageModel;
import com.journeyOS.base.barrage.OnBarrageStateChangeListener;
import com.journeyOS.base.persistence.SpUtils;
import com.journeyOS.base.utils.AppUtils;
import com.journeyOS.base.utils.Base64Utils;
import com.journeyOS.base.utils.JsonHelper;
import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.base.utils.Singleton;
import com.journeyOS.base.utils.UIUtils;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.StateMachine;
import com.journeyOS.core.permission.IPermission;
import com.journeyOS.core.type.BarrageState;
import com.journeyOS.edge.R;
import com.journeyOS.i007Service.core.notification.Notification;

import java.util.List;

import es.dmoral.toasty.Toasty;

public class BarrageManager implements OnBarrageStateChangeListener {
    private static final String TAG = BarrageManager.class.getSimpleName();

    private Context mContext;
    private WindowManager mWm;
    private RelativeLayout mRootView;

    private BarrageDispatcher mBarrageDispatcher;

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
            mRootView = new RelativeLayout(mContext);
            mRootView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT));
        }

        initBarrageDispatcher();

        LogUtils.d(TAG, "barrage view has been attached to window = [" + isAttachedToWindow + "]");
        if (!isAttachedToWindow) {
            isAttachedToWindow = true;
            mWm.addView(mRootView, getLayoutParams());
        }
    }

    public void initBarrageDispatcher() {
        if (mBarrageDispatcher == null) {
            mBarrageDispatcher = new BarrageDispatcher(mContext, mRootView);
            mBarrageDispatcher.registerChangedListener(this);
        }
    }

    public void sendBarrageTest() {
        initBarrage();

        Bitmap bitmap = UIUtils.drawableToBitmap(mContext.getResources().getDrawable(R.mipmap.user));
        Bitmap circleBitmap = UIUtils.getCircularBitmap(bitmap);
        sendBarrage(circleBitmap, "用户名", "弹幕消息超长测试11111222223333344444555556666677777888889999900000111111~", true);
    }

    public void sendBarrage(Notification notification) {
        initBarrage();

        Bitmap circleBitmap = null;
        Drawable drawable = AppUtils.getAppIcon(mContext, notification.getPackageName());
        if (drawable != null) {
            Bitmap bitmap = UIUtils.drawableToBitmap(drawable);
            circleBitmap = UIUtils.getCircularBitmap(bitmap);
        }
        sendBarrage(circleBitmap, notification.getTitle(), notification.getText(), true);
        setPackageName(notification.getPackageName());
    }

    public void sendBarrage(Bitmap bitmap, String name, String text, boolean checkSkip) {
        if (checkSkip) {
            boolean isSkip = skipBarrage(name, text);
            if (isSkip) {
                LogUtils.i(TAG, "skip this barrage!");
                return;
            }
        }

        if (mRootView == null) {
            initBarrage();
        }

        if (mBarrageDispatcher != null) {
            BarrageModel barrageModel = getBarrageModel(bitmap, name, text);
            mBarrageDispatcher.send(barrageModel);
        }
    }

    public boolean skipBarrage(String name, String text) {
        boolean isSkip = false;
        String json = SpUtils.getInstant().getString(Constant.BARRAGE_FLITER, Constant.BARRAGE_FLITER_DEFAULT);
        if (json != null) {
            List<String> fliters = JsonHelper.fromJson(Base64Utils.fromBase64(json), List.class);
            for (String fliter : fliters) {
                if (name != null && name.toLowerCase().contains(fliter.toLowerCase())) {
                    isSkip = true;
                    break;
                }

                if (text != null && text.toLowerCase().contains(fliter.toLowerCase())) {
                    isSkip = true;
                    break;
                }
            }
        }
        return isSkip;
    }

    public void hideBarrage() {
        StateMachine.setBarrageState(BarrageState.HIDE);
    }

    private LayoutParams getLayoutParams() {
        LayoutParams params = WindowUitls.getBaseLayoutParams();
        params.flags = params.flags | LayoutParams.FLAG_SPLIT_TOUCH | LayoutParams.FLAG_NOT_TOUCHABLE;
        params.width = LayoutParams.MATCH_PARENT;
        params.height = LayoutParams.MATCH_PARENT;
        return params;
    }

    private BarrageModel getBarrageModel(Bitmap bitmap, String title, String content) {
        BarrageModel barrageModel = new BarrageModel();
        barrageModel.avatar = bitmap;
        if (title != null && title.length() > 10) {
            title = title.substring(0, 9);
        }
        barrageModel.title = title;
        barrageModel.content = content;
        BarrageController controller = new BarrageController();
        controller.setPostion(SpUtils.getInstant().getInt(Constant.BARRAGE_POSTION, Constant.BARRAGE_POSTION_DEFAULT));
        controller.setDirection(SpUtils.getInstant().getInt(Constant.BARRAGE_DIRECTION, Constant.BARRAGE_DIRECTION_DEFAULT));
        controller.setSpeed(SpUtils.getInstant().getInt(Constant.BARRAGE_SPEED, Constant.BARRAGE_SPEED_DEFAULT));
        controller.setAvatarSize(SpUtils.getInstant().getInt(Constant.BARRAGE_AVATAR_SIZE, Constant.BARRAGE_AVATAR_SIZE_DEFAULT));
        controller.setTextSize(SpUtils.getInstant().getInt(Constant.BARRAGE_TEXT_SIZE, Constant.BARRAGE_TEXT_SIZE_DEFAULT));
        controller.setTextTitleColor(SpUtils.getInstant().getInt(Constant.BARRAGE_TITLE_COLOR, Constant.BARRAGE_TITLE_COLOR_DEFAULT));
        controller.setTextContentColor(SpUtils.getInstant().getInt(Constant.BARRAGE_SUMMARY_COLOR, Constant.BARRAGE_SUMMARY_COLOR_DEFAULT));
        controller.setBackgroundColor(SpUtils.getInstant().getInt(Constant.BARRAGE_BACKGROUND_COLOR, Constant.BARRAGE_BACKGROUND_COLOR_DEFAULT));

        float[] radii = {
                (float) SpUtils.getInstant().getInt(Constant.BARRAGE_BACKGROUND_TOP_LEFT, Constant.BARRAGE_BACKGROUND_TOP_LEFT_DEFAULT),
                (float) SpUtils.getInstant().getInt(Constant.BARRAGE_BACKGROUND_TOP_LEFT, Constant.BARRAGE_BACKGROUND_TOP_LEFT_DEFAULT),
                (float) SpUtils.getInstant().getInt(Constant.BARRAGE_BACKGROUND_TOP_RIGHT, Constant.BARRAGE_BACKGROUND_TOP_RIGHT_DEFAULT),
                (float) SpUtils.getInstant().getInt(Constant.BARRAGE_BACKGROUND_TOP_RIGHT, Constant.BARRAGE_BACKGROUND_TOP_RIGHT_DEFAULT),
                (float) SpUtils.getInstant().getInt(Constant.BARRAGE_BACKGROUND_BOTTOM_RIGHT, Constant.BARRAGE_BACKGROUND_BOTTOM_RIGHT_DEFAULT),
                (float) SpUtils.getInstant().getInt(Constant.BARRAGE_BACKGROUND_BOTTOM_RIGHT, Constant.BARRAGE_BACKGROUND_BOTTOM_RIGHT_DEFAULT),
                (float) SpUtils.getInstant().getInt(Constant.BARRAGE_BACKGROUND_BOTTOM_LEFT, Constant.BARRAGE_BACKGROUND_BOTTOM_LEFT_DEFAULT),
                (float) SpUtils.getInstant().getInt(Constant.BARRAGE_BACKGROUND_BOTTOM_LEFT, Constant.BARRAGE_BACKGROUND_BOTTOM_LEFT_DEFAULT)
        };
        controller.setBackgroundRadius(radii);

        controller.setStrokeWidth(SpUtils.getInstant().getInt(Constant.BARRAGE_BACKGROUND_STROKE_WIDTH, Constant.BARRAGE_BACKGROUND_STROKE_WIDTH_DEFAULT));
        controller.setStrokeColor(SpUtils.getInstant().getInt(Constant.BARRAGE_BACKGROUND_STROKE_COLOR, Constant.BARRAGE_BACKGROUND_STROKE_COLOR_DEFAULT));

        barrageModel.controller = controller;
        return barrageModel;
    }

    public String getPackageName() {
        return mOngingPackageName;
    }

    public void setPackageName(String packageName) {
        this.mOngingPackageName = packageName;
    }

    @Override
    public void onBarrageAttachedToWindow() {
        LogUtils.d(TAG, "barrage attached to window");
        StateMachine.setBarrageState(BarrageState.SHOW);
    }

    @Override
    public void onBarrageDetachedFromWindow() {
        LogUtils.d(TAG, "barrage detached to window");
        hideBarrage();
        setPackageName(null);
        if (mRootView != null) {
            if (isAttachedToWindow) {
                LogUtils.d(TAG, "remove barrage view from window");
                mWm.removeView(mRootView);
                isAttachedToWindow = false;
            }
        }
    }
}
