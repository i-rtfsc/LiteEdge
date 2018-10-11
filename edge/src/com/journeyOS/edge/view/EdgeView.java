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

package com.journeyOS.edge.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.journeyOS.base.Constant;
import com.journeyOS.base.utils.AnimationUtil;
import com.journeyOS.base.utils.AppUtils;
import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.api.edgeprovider.EdgeConfig;
import com.journeyOS.core.api.thread.ICoreExecutorsApi;
import com.journeyOS.core.type.EdgeDirection;
import com.journeyOS.edge.EdgeService;
import com.journeyOS.edge.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class EdgeView extends RelativeLayout implements View.OnClickListener, View.OnLongClickListener, View.OnAttachStateChangeListener {
    private static final String TAG = EdgeView.class.getSimpleName();

    private EdgeDirection mEd;

    private View mMask;
    private View mRootView;
    private View mLayoutGroups;
    private View mLayoutStatus;

    private TextView mStatusBarText1;
    private TextView mStatusBarText2;
    private TextView mStatusBarText3;

    private View mLayout1;
    private View mLayout2;
    private View mLayout3;
    private View mLayout4;
    private View mLayout5;
    private View mLayout6;

    private CircleImageView mIcon1;
    private CircleImageView mIcon2;
    private CircleImageView mIcon3;
    private CircleImageView mIcon4;
    private CircleImageView mIcon5;
    private CircleImageView mIcon6;

    private TextView mText1;
    private TextView mText2;
    private TextView mText3;
    private TextView mText4;
    private TextView mText5;
    private TextView mText6;

    private float mStatusBarHeight, mIconGroupHeight;
    private float mStatusBarWidth, mIconGroupWidth;

    public EdgeView(Context context) {
        super(context);
    }

    public EdgeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public EdgeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        initCommonView();

        mEd = EdgeService.getEdgeDirection();
        LogUtils.d(TAG, "on view inflate, edge direction = " + mEd);
        switch (mEd) {
            case UP:
                initUpView();
                break;
            case LEFT:
                initLeftView();
                break;
            case RIGHT:
                initRightView();
                break;
        }
    }

    private void initCommonView() {
        mRootView = findViewById(R.id.root_view);
        mMask = findViewById(R.id.mask_bg);
        mLayoutStatus = findViewById(R.id.layout_statusbar);
        mLayoutGroups = findViewById(R.id.layout_groups);

        mLayout1 = findViewById(R.id.layout1);
        mLayout1.setOnClickListener(this);
        mLayout1.setOnLongClickListener(this);
        mIcon1 = (CircleImageView) findViewById(R.id.icon1);
        mText1 = (TextView) findViewById(R.id.text1);

        mLayout2 = findViewById(R.id.layout2);
        mLayout2.setOnClickListener(this);
        mLayout2.setOnLongClickListener(this);
        mIcon2 = (CircleImageView) findViewById(R.id.icon2);
        mText2 = (TextView) findViewById(R.id.text2);

        mLayout3 = findViewById(R.id.layout3);
        mLayout3.setOnClickListener(this);
        mLayout3.setOnLongClickListener(this);
        mIcon3 = (CircleImageView) findViewById(R.id.icon3);
        mText3 = (TextView) findViewById(R.id.text3);

        mLayout4 = findViewById(R.id.layout4);
        mLayout4.setOnClickListener(this);
        mLayout4.setOnLongClickListener(this);
        mIcon4 = (CircleImageView) findViewById(R.id.icon4);
        mText4 = (TextView) findViewById(R.id.text4);

        mLayout5 = findViewById(R.id.layout5);
        mLayout5.setOnClickListener(this);
        mLayout5.setOnLongClickListener(this);
        mIcon5 = (CircleImageView) findViewById(R.id.icon5);
        mText5 = (TextView) findViewById(R.id.text5);

        mLayout6 = findViewById(R.id.layout6);
        mLayout6.setOnClickListener(this);
        mLayout6.setOnLongClickListener(this);
        mIcon6 = (CircleImageView) findViewById(R.id.icon6);
        mText6 = (TextView) findViewById(R.id.text6);

        mStatusBarText1 = (TextView) findViewById(R.id.statusbar_text1);
        mStatusBarText2 = (TextView) findViewById(R.id.statusbar_text2);
        mStatusBarText3 = (TextView) findViewById(R.id.statusbar_text3);

        mStatusBarText1.setText("晴转多云");
        mStatusBarText2.setText("16℃");
        mStatusBarText3.setText("空气质量：优");

        addOnAttachStateChangeListener(this);
    }

    private void initLeftView() {
        mLayoutStatus.setPivotX(480f);
        mLayoutStatus.setPivotY(48f);
        mLayoutStatus.setRotation(-90f);

        mLayout1.setRotation(-22f);
        mLayout2.setRotation(-13.2f);
        mLayout3.setRotation(-4.4f);
        mLayout4.setRotation(4.4f);
        mLayout5.setRotation(13.2f);
        mLayout6.setRotation(22f);

        mStatusBarWidth = 96f;
        mIconGroupWidth = 339f;
    }

    private void initRightView() {
        mLayoutStatus.setPivotX(480f);
        mLayoutStatus.setPivotY(48f);
        mLayoutStatus.setRotation(90f);

        mLayout1.setRotation(22f);
        mLayout2.setRotation(13.2f);
        mLayout3.setRotation(4.4f);
        mLayout4.setRotation(-4.4f);
        mLayout5.setRotation(-13.2f);
        mLayout6.setRotation(-22f);

        mStatusBarWidth = 96f;
        mIconGroupWidth = 339f;
    }

    private void initUpView() {
        mLayout1.setRotation(24f);
        mLayout2.setRotation(14.4f);
        mLayout3.setRotation(4.8f);
        mLayout4.setRotation(-4.8f);
        mLayout5.setRotation(-14.4f);
        mLayout6.setRotation(-24f);

        mStatusBarHeight = 144f;
        mIconGroupHeight = 384f;
    }

    public void initDatas() {
        if (mListener != null) {
            CoreManager.getDefault().getImpl(ICoreExecutorsApi.class).diskIOThread().execute(new Runnable() {
                @Override
                public void run() {
                    final List<EdgeConfig> configs = mListener.getConfigs(mEd);
                    CoreManager.getDefault().getImpl(ICoreExecutorsApi.class).mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            if (configs != null) {
                                for (EdgeConfig config : configs) {
                                    int postion = -1;
                                    String[] items = config.item.split(Constant.SEPARATOR);
                                    if (items != null) {
                                        postion = Integer.parseInt(items[1]);
                                    }

                                    Drawable drawable = AppUtils.getAppIcon(getContext(), config.packageName);
                                    String name = AppUtils.getAppName(getContext(), config.packageName, Constant.LENGTH);
                                    if (postion == 1) {
                                        if (mIcon1 != null || drawable != null) {
                                            mIcon1.setImageDrawable(drawable);
                                        }
                                        if (mText1 != null || name != null) {
                                            mText1.setText(name);
                                        }
                                    } else if (postion == 2) {
                                        if (mIcon2 != null || drawable != null) {
                                            mIcon2.setImageDrawable(drawable);
                                        }
                                        if (mText2 != null || name != null) {
                                            mText2.setText(name);
                                        }
                                    } else if (postion == 3) {
                                        if (mIcon3 != null || drawable != null) {
                                            mIcon3.setImageDrawable(drawable);
                                        }
                                        if (mText3 != null || name != null) {
                                            mText3.setText(name);
                                        }
                                    } else if (postion == 4) {
                                        if (mIcon4 != null || drawable != null) {
                                            mIcon4.setImageDrawable(drawable);
                                        }
                                        if (mText4 != null || name != null) {
                                            mText4.setText(name);
                                        }
                                    } else if (postion == 5) {
                                        if (mIcon5 != null || drawable != null) {
                                            mIcon5.setImageDrawable(drawable);
                                        }
                                        if (mText5 != null || name != null) {
                                            mText5.setText(name);
                                        }
                                    } else if (postion == 6) {
                                        if (mIcon6 != null || drawable != null) {
                                            mIcon6.setImageDrawable(drawable);
                                        }
                                        if (mText6 != null || name != null) {
                                            mText6.setText(name);
                                        }
                                    }
                                }
                            }
                        }
                    });
                }
            });
        }
    }

    public void showEdgeView() {
        mRootView.setAlpha(1f);
        mRootView.setTranslationY(0f);
        mRootView.setTranslationX(0f);
        initDatas();
        maskShow();
        mainIconGroupShow(EdgeDirection.UP == mEd);
        statusBarShow(EdgeDirection.UP == mEd);
    }

    public void hideEdgeView() {
        boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        maskDismiss();
        mainIconGroupDismiss(isLandscape);
        statusBarDismiss(isLandscape);
    }

    private void maskShow() {
        mMask.setAlpha(0f);
        mMask.animate().alpha(1f).setDuration(360).setInterpolator(AnimationUtil.getEaseInterpolator()).setListener(null).start();
    }

    private void maskDismiss() {
        mMask.setAlpha(1f);
        mMask.animate().alpha(0f).setDuration(360).setStartDelay(45).setInterpolator(AnimationUtil.getEaseOutInterpolator()).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                EdgeView.this.setVisibility(View.GONE);
            }
        }).start();
    }

    private void mainIconGroupShow(boolean isLandscape) {
        mLayoutGroups.setAlpha(0f);
        mLayoutGroups.setScaleX(0.6f);
        mLayoutGroups.setScaleY(0.6f);
        mLayoutGroups.animate().alpha(1f).scaleX(1f).scaleY(1f).setDuration(360).setInterpolator(AnimationUtil.getShowCurveResistanceInterpolator());
        if (isLandscape) {
            mLayoutGroups.setTranslationY(-mIconGroupHeight);
            mLayoutGroups.animate().translationY(0f).start();
        } else if (EdgeDirection.LEFT == mEd) {
            mLayoutGroups.setTranslationX(-mIconGroupWidth);
            mLayoutGroups.animate().translationX(0f).start();
        } else {
            mLayoutGroups.setTranslationX(mIconGroupWidth);
            mLayoutGroups.animate().translationX(0f).start();
        }
    }

    private void mainIconGroupDismiss(boolean isLandscape) {
        mLayoutGroups.setAlpha(1f);
        mLayoutGroups.setScaleX(1f);
        mLayoutGroups.setScaleY(1f);
        mLayoutGroups.animate().alpha(0f).scaleX(0.6f).scaleY(0.6f).setDuration(360).setInterpolator(AnimationUtil.getHideCurveInterpolator());
        if (isLandscape) {
            mLayoutGroups.setTranslationY(0f);
            mLayoutGroups.animate().translationY(-mIconGroupHeight).start();
        } else if (EdgeDirection.LEFT == mEd) {
            mLayoutGroups.setTranslationX(0f);
            mLayoutGroups.animate().translationX(-mIconGroupWidth).start();
        } else {
            mLayoutGroups.setTranslationX(0f);
            mLayoutGroups.animate().translationX(mIconGroupWidth).start();
        }

        mLayoutGroups.animate().setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                notifyViewDetached();
            }
        });
    }

    private void statusBarShow(boolean isLandscape) {
        mLayoutStatus.setScaleX(0.6f);
        mLayoutStatus.setScaleY(0.6f);
        mLayoutStatus.setAlpha(1f);
        mLayoutStatus.animate().scaleY(1f).scaleX(1f).setDuration(360).setStartDelay(45).setInterpolator(AnimationUtil.getEaseInterpolator());
        if (isLandscape) {
            mLayoutStatus.setTranslationY(-mStatusBarHeight);
            mLayoutStatus.animate().translationY(0f).start();
        } else if (EdgeDirection.LEFT == mEd) {
            mLayoutStatus.setTranslationX(-mStatusBarWidth);
            mLayoutStatus.animate().translationX(0f).start();
        } else {
            mLayoutStatus.setTranslationX(mStatusBarWidth);
            mLayoutStatus.animate().translationX(0f).start();
        }
    }

    private void statusBarDismiss(boolean isLandscape) {
        mLayoutStatus.setScaleX(1f);
        mLayoutStatus.setScaleY(1f);
        mLayoutStatus.animate().scaleY(0.6f).scaleX(0.6f).translationY(-mStatusBarHeight).alpha(0.1f).setDuration(240).setInterpolator(AnimationUtil.getHideCurveInterpolator()).start();
        if (isLandscape) {
            mLayoutStatus.setTranslationY(0f);
            mLayoutStatus.animate().translationY(-mStatusBarHeight).start();
        } else if (EdgeDirection.LEFT == mEd) {
            mLayoutStatus.setTranslationX(0f);
            mLayoutStatus.animate().translationX(-mStatusBarWidth).start();
        } else {
            mLayoutStatus.setTranslationX(0f);
            mLayoutStatus.animate().translationX(mStatusBarWidth).start();
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    hideEdgeView();
                }
                return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // If we've received a touch notification that the user has touched
        // outside the app, hide the dock view.
        if (MotionEvent.ACTION_OUTSIDE == event.getAction()
                || MotionEvent.ACTION_UP == event.getAction()) {
            hideEdgeView();
            return true;
        }

        // Delegate everything else to Manager.
        return super.onTouchEvent(event);
    }

    @Override
    public void onViewAttachedToWindow(View view) {

    }

    @Override
    public void onViewDetachedFromWindow(View view) {
        LogUtils.d(TAG, "edge view detached");
        notifyViewDetached();
    }

    @Override
    public void onClick(View view) {
        int postion = -1;
        switch (view.getId()) {
            case R.id.layout1:
                postion = 1;
                break;
            case R.id.layout2:
                postion = 2;
                break;
            case R.id.layout3:
                postion = 3;
                break;
            case R.id.layout4:
                postion = 4;
                break;
            case R.id.layout5:
                postion = 5;
                break;
            case R.id.layout6:
                postion = 6;
                break;
        }
        if (mListener != null) {
            mListener.onItemClick(postion);
        }
    }

    @Override
    public boolean onLongClick(View view) {
        int postion = -1;
        switch (view.getId()) {
            case R.id.layout1:
                postion = 1;
                break;
            case R.id.layout2:
                postion = 2;
                break;
            case R.id.layout3:
                postion = 3;
                break;
            case R.id.layout4:
                postion = 4;
                break;
            case R.id.layout5:
                postion = 5;
                break;
            case R.id.layout6:
                postion = 6;
                break;
        }
        if (mListener != null) {
            mListener.onItemLongClick(postion);
        }
        return true;
    }


    void notifyViewDetached() {
        //onViewDetachedFromWindow not be called!
        if (mListener != null) {
            CoreManager.getDefault().getImpl(ICoreExecutorsApi.class).handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mListener.onViewDetachedFromWindow();
                }
            }, 0);
        }
    }

    private OnEdgeViewListener mListener;

    public void setOnEdgeViewListener(OnEdgeViewListener listener) {
        mListener = listener;
    }

    public interface OnEdgeViewListener {
        void onViewAttachedToWindow();

        void onViewDetachedFromWindow();

        List<EdgeConfig> getConfigs(EdgeDirection direction);

        void onItemClick(int postion);

        void onItemLongClick(int postion);
    }
}
