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
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.journeyOS.base.Constant;
import com.journeyOS.base.persistence.SpUtils;
import com.journeyOS.base.utils.AnimationUtil;
import com.journeyOS.base.utils.BaseUtils;
import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.base.utils.UIUtils;
import com.journeyOS.base.widget.LocusLayoutManager;
import com.journeyOS.base.widget.textview.RainbowTextView;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.StateMachine;
import com.journeyOS.core.api.edgeprovider.IEdgeLabProvider;
import com.journeyOS.core.api.thread.ICoreExecutors;
import com.journeyOS.core.database.edge.Edge;
import com.journeyOS.core.database.edgelab.EdgeLab;
import com.journeyOS.core.type.EdgeDirection;
import com.journeyOS.core.weather.Air;
import com.journeyOS.core.weather.Weather;
import com.journeyOS.edge.R;
import com.journeyOS.edge.view.adapter.EdgeAdapter;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import java.util.List;

public class EdgeView extends RelativeLayout implements View.OnClickListener, View.OnLongClickListener, View.OnAttachStateChangeListener {
    private static final String TAG = EdgeView.class.getSimpleName();

    boolean isUpdate = false;

    //temperature
    private static final String TEMPERATURE = "℃";
    private static final String REGION = " ~ ";

    EdgeDirection mEd;

    View mBackground;
    View mRootView;
    View mLayoutGroups;
    View mLayoutStatus;

    RainbowTextView mStatusBarText;

    //lab
    LocusLayoutManager mLayoutManager;
    EdgeAdapter mAdapter;
    RecyclerView mListView;

    View mDebug;
    View mDebugClose;
    IndicatorSeekBar mRadius;
    TextView mRadiusText;
    IndicatorSeekBar mPeek;
    TextView mPeekText;

    IndicatorSeekBar mWidth;
    TextView mWidthText;
    IndicatorSeekBar mHeight;
    TextView mHeightText;

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
        mEd = StateMachine.getEdgeDirection();
        LogUtils.d(TAG, "on view inflate, edge direction = " + mEd);

        initCommonView();
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

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        if (!isUpdate) {
            isUpdate = true;
            if (mEd != null) {
                EdgeLab edgeLab = CoreManager.getDefault().getImpl(IEdgeLabProvider.class).getCacheConfig(mEd.name().toLowerCase());
                if (edgeLab != null) {
                    initEdgeSeekBar(edgeLab);
                    updateEdgeLayout(edgeLab.width, edgeLab.height);
                }
            }
        }
    }

    private void initCommonView() {
        mRootView = findViewById(R.id.root_view);
        mBackground = findViewById(R.id.mask_bg);
        mLayoutStatus = findViewById(R.id.layout_statusbar);
        mLayoutStatus.setOnLongClickListener(this);

        mLayoutGroups = findViewById(R.id.layout_groups_lab);
        mListView = (RecyclerView) findViewById(R.id.recycler_view);

        mDebug = findViewById(R.id.control_panel);
        mDebugClose = findViewById(R.id.control_close);
//        mDebug.setVisibility(VISIBLE);
        mRadius = (IndicatorSeekBar) findViewById(R.id.seek_radius);
        mRadiusText = (TextView) findViewById(R.id.radius_text);
        mPeek = (IndicatorSeekBar) findViewById(R.id.seek_peek);
        mPeekText = (TextView) findViewById(R.id.peek_text);

        LogUtils.d(TAG, " EdgeDirection = " + mEd + " , is up = " + (EdgeDirection.UP != mEd));
        LogUtils.d(TAG, " getScreenWidth = " + UIUtils.getScreenWidth(getContext()) + " , getScreenHeight = " + UIUtils.getScreenHeight(getContext()));

        mWidth = (IndicatorSeekBar) findViewById(R.id.seek_edge_width);
        int widthMin = EdgeDirection.UP != mEd ? 100 : UIUtils.getScreenWidth(getContext()) / 2;
        int widthMax = EdgeDirection.UP != mEd ? UIUtils.getScreenWidth(getContext()) / 3 : UIUtils.getScreenWidth(getContext());
        LogUtils.d(TAG, " widthMin = " + widthMin + " , widthMax = " + widthMax);
        mWidth.setMin(widthMin);
        mWidth.setMax(widthMax);
        mWidthText = (TextView) findViewById(R.id.edge_width_text);

        mHeight = (IndicatorSeekBar) findViewById(R.id.seek_edge_height);
        int heightMin = EdgeDirection.UP != mEd ? UIUtils.getScreenHeight(getContext()) / 2 : 100;
        int heightMax = EdgeDirection.UP != mEd ? UIUtils.getScreenHeight(getContext()) : UIUtils.getScreenHeight(getContext()) / 3;
        LogUtils.d(TAG, " heightMin = " + heightMin + " , heightMax = " + heightMax);
        mHeight.setMin(heightMin);
        mHeight.setMax(heightMax);
        mHeightText = (TextView) findViewById(R.id.edge_height_text);

        boolean edgeLabDebug = SpUtils.getInstant().getBoolean(Constant.EDGE_LAB_DEBUG, Constant.EDGE_LAB_DEBUG_DEFAULT);
        if (edgeLabDebug) {
            mLayoutStatus.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    int visibility = mDebug.getVisibility();
                    if (VISIBLE == visibility) {
                        mDebug.setVisibility(GONE);
                    } else {
                        mDebug.setVisibility(VISIBLE);
                    }
                }
            });

            mDebugClose.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    mDebug.setVisibility(GONE);
                }
            });
        }
        mStatusBarText = (RainbowTextView) findViewById(R.id.statusbar_text);
        addOnAttachStateChangeListener(this);
    }

    private void initLeftView() {
        mLayoutStatus.setPivotX(480f);
        mLayoutStatus.setPivotY(48f);
        mLayoutStatus.setRotation(-90f);

        mStatusBarWidth = 96f;
        mIconGroupWidth = 339f;
    }

    private void initRightView() {
        mLayoutStatus.setPivotX(480f);
        mLayoutStatus.setPivotY(48f);
        mLayoutStatus.setRotation(90f);

        mStatusBarWidth = 96f;
        mIconGroupWidth = 339f;
    }

    private void initUpView() {
        mStatusBarHeight = 144f;
        mIconGroupHeight = 384f;
    }

    private void initEdgeSeekBar(EdgeLab edgeLab) {
        int edgeWidth = edgeLab.width;
        int edgeHeight = edgeLab.height;
        mWidth.setProgress(edgeWidth);
        if (mWidthText != null) {
            mWidthText.setText(getResources().getString(R.string.edge_width_format, edgeWidth));
        }
        mHeight.setProgress(edgeHeight);
        if (mHeightText != null) {
            mHeightText.setText(getResources().getString(R.string.edge_height_format, edgeHeight));
        }
    }

    private void updateEdgeLayout(int width, int height) {
        ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) mLayoutGroups.getLayoutParams();
        if (width != -1) params.width = width;
        if (height != -1) params.height = height;
        LogUtils.d(TAG, " on layout width = [" + width + "], height = [" + height + "]");
        mLayoutGroups.setLayoutParams(params);
    }

    public void initDatas() {
        if (mListener != null) {
            CoreManager.getDefault().getImpl(ICoreExecutors.class).diskIOThread().execute(new Runnable() {
                @Override
                public void run() {
                    final EdgeLab edgeLab = mListener.getLabConfig(mEd);

                    final List<Edge> configs = mListener.getConfigs(mEd);
                    if (!BaseUtils.isNull(configs)) {
                        CoreManager.getDefault().getImpl(ICoreExecutors.class).mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                initEdgeSeekBar(edgeLab);
                                if (edgeLab != null) {
                                    updateEdgeLayout(edgeLab.width, edgeLab.height);
                                }

                                mAdapter = new EdgeAdapter(getContext(), mEd, configs);
                                mAdapter.setOnEdgeAdapterListener(mAdapterListener);
                                mLayoutManager = new LocusLayoutManager(getContext(),
                                        edgeLab.gravity,
                                        edgeLab.orientation,
                                        edgeLab.radius,
                                        edgeLab.peek,
                                        edgeLab.rotate == 1);

                                mListView.setLayoutManager(mLayoutManager);
                                mListView.setAdapter(mAdapter);
                                mAdapter.notifyDataSetChanged();

                                mListView.setVisibility(View.VISIBLE);

                                mRadius.setOnSeekChangeListener(mRadiusListener);
                                mPeek.setOnSeekChangeListener(mPeekListener);
                                mRadius.setProgress(edgeLab.radius);
                                mPeek.setProgress(edgeLab.peek);

                                mWidth.setOnSeekChangeListener(mWidthListener);
                                mHeight.setOnSeekChangeListener(mHeightListener);


                            }
                        });
                    }
                }
            });

            CoreManager.getDefault().getImpl(ICoreExecutors.class).networkIOThread().execute(new Runnable() {
                @Override
                public void run() {
                    Weather weather = mListener.getWeather();
                    final StringBuilder sb = new StringBuilder();
                    if (weather != null && weather.HeWeather6 != null) {
                        final Weather.HeWeather6Bean heWeather = weather.HeWeather6.get(0);
                        if (heWeather != null) {
                            if (heWeather.daily_forecast != null) {
                                Weather.HeWeather6Bean.DailyForecastBean daily = heWeather.daily_forecast.get(0);
                                Weather.HeWeather6Bean.BasicBean basic = heWeather.basic;
                                if (daily != null && basic != null) {
                                    sb.append(basic.location)
                                            .append("：")
                                            .append(daily.cond_txt_d)
                                            .append("    ")
                                            .append(daily.tmp_min)
                                            .append(REGION)
                                            .append(daily.tmp_max)
                                            .append(TEMPERATURE)
                                            .append("    ");
                                }
                            }
                        }
                    }

                    Air air = mListener.getAir();
                    if (air != null && air.HeWeather6 != null) {
                        final Air.HeWeather6Bean.AirNowCityBean airNowCity = air.HeWeather6.get(0).air_now_city;
                        if (airNowCity != null) {
                            sb.append(getContext().getString(R.string.weather_air_alty))
                                    .append(airNowCity.qlty);
                        }
                    }

                    CoreManager.getDefault().getImpl(ICoreExecutors.class).mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            mStatusBarText.setText(sb.toString());
                        }
                    });
                }
            });
        }
    }

    public void showEdgeView() {
        if (mEd != null) {
            final EdgeLab edgeLab = CoreManager.getDefault().getImpl(IEdgeLabProvider.class).getCacheConfig(mEd.name().toLowerCase());
            if (edgeLab != null) {
                CoreManager.getDefault().getImpl(ICoreExecutors.class).mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        updateEdgeLayout(edgeLab.width, edgeLab.height);
                    }
                });
            }
        }
        mRootView.setAlpha(1f);
        mRootView.setTranslationY(0f);
        mRootView.setTranslationX(0f);
        initDatas();
        showBackgroundAnimate(mBackground);
        showEdgeAnimate(mLayoutGroups, EdgeDirection.UP == mEd);
        showEdgeStatusAnimate(mLayoutStatus, EdgeDirection.UP == mEd);
    }

    public void hideEdgeView() {
        if (mDebug != null && mDebug.getVisibility() == VISIBLE) {
            LogUtils.d(TAG, "user adjust view, can't be hide edge view,");
            return;
        }

        boolean isLandscape = getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        hideBackgroundAnimate(mBackground);
        hideEdgeAnimate(mLayoutGroups, isLandscape);
        hideEdgeStatusAnimate(mLayoutStatus, isLandscape);
    }

    private void showBackgroundAnimate(View view) {
        view.setAlpha(0f);
        view.animate()
                .alpha(1f)
                .setDuration(360)
                .setInterpolator(AnimationUtil.getEaseInterpolator())
                .setListener(null)
                .start();
    }

    private void hideBackgroundAnimate(View view) {
        view.setAlpha(1f);
        view.animate()
                .alpha(0f)
                .setDuration(360)
                .setStartDelay(45)
                .setInterpolator(AnimationUtil.getEaseOutInterpolator()).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                EdgeView.this.setVisibility(View.GONE);
            }
        }).start();
    }

    private void showEdgeAnimate(View view, boolean isLandscape) {
        view.setAlpha(0f);
        view.setScaleX(0.6f);
        view.setScaleY(0.6f);
        view.animate()
                .alpha(1f)
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(360)
                .setInterpolator(AnimationUtil.getShowCurveResistanceInterpolator());
        if (isLandscape) {
            view.setTranslationY(-mIconGroupHeight);
            view.animate()
                    .translationY(0f).start();
        } else if (EdgeDirection.LEFT == mEd) {
            view.setTranslationX(-mIconGroupWidth);
            view.animate()
                    .translationX(0f)
                    .start();
        } else {
            view.setTranslationX(mIconGroupWidth);
            view.animate()
                    .translationX(0f)
                    .start();
        }
    }

    private void hideEdgeAnimate(View view, boolean isLandscape) {
        view.setAlpha(1f);
        view.setScaleX(1f);
        view.setScaleY(1f);
        view.animate()
                .alpha(0f)
                .scaleX(0.6f)
                .scaleY(0.6f)
                .setDuration(360)
                .setInterpolator(AnimationUtil.getHideCurveInterpolator());
        if (isLandscape) {
            view.setTranslationY(0f);
            view.animate()
                    .translationY(-mIconGroupHeight)
                    .start();
        } else if (EdgeDirection.LEFT == mEd) {
            view.setTranslationX(0f);
            view.animate()
                    .translationX(-mIconGroupWidth)
                    .start();
        } else {
            view.setTranslationX(0f);
            view.animate()
                    .translationX(mIconGroupWidth)
                    .start();
        }

        view.animate().setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                notifyViewDetached();
            }
        });
    }

    private void showEdgeStatusAnimate(View view, boolean isLandscape) {
        view.setScaleX(0.6f);
        view.setScaleY(0.6f);
        view.setAlpha(1f);
        view.animate()
                .scaleY(1f)
                .scaleX(1f)
                .setDuration(360)
                .setStartDelay(45)
                .setInterpolator(AnimationUtil.getEaseInterpolator())
                .start();
        if (isLandscape) {
            view.setTranslationY(-mStatusBarHeight);
            view.animate()
                    .translationY(0f)
                    .start();
        } else if (EdgeDirection.LEFT == mEd) {
            view.setTranslationX(-mStatusBarWidth);
            view.animate()
                    .translationX(0f)
                    .start();
        } else {
            view.setTranslationX(mStatusBarWidth);
            view.animate()
                    .translationX(0f)
                    .start();
        }
    }

    private void hideEdgeStatusAnimate(View view, boolean isLandscape) {
        view.setScaleX(1f);
        view.setScaleY(1f);
        view.animate()
                .scaleY(0.6f)
                .scaleX(0.6f)
                .translationY(-mStatusBarHeight)
                .alpha(0.1f)
                .setDuration(240)
                .setInterpolator(AnimationUtil.getHideCurveInterpolator())
                .start();
        if (isLandscape) {
            view.setTranslationY(0f);
            view.animate()
                    .translationY(-mStatusBarHeight)
                    .start();
        } else if (EdgeDirection.LEFT == mEd) {
            view.setTranslationX(0f);
            view.animate()
                    .translationX(-mStatusBarWidth)
                    .start();
        } else {
            view.setTranslationX(0f);
            view.animate()
                    .translationX(mStatusBarWidth)
                    .start();
        }
    }

    //用 WindowManager.addView 的时候千万别设置FLAG_NOT_FOCUSABLE
    //不然dispatchKeyEvent死活接收不到事件...
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && EdgeDirection.NONE != StateMachine.getEdgeDirection()) {
                    hideEdgeView();
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && EdgeDirection.NONE != StateMachine.getEdgeDirection()) {
                    hideEdgeView();
                }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // If we've received a touch notification that the user has touched
        // outside the app, hide the dock view.
        if (MotionEvent.ACTION_OUTSIDE == event.getAction()
                || MotionEvent.ACTION_DOWN == event.getAction()) {
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

        switch (view.getId()) {
            case R.id.layout_statusbar:
                if (mListener != null) {
                    mListener.onLongClickStatusbar();
                }
                break;
        }
        return true;
    }


    void notifyViewDetached() {
        //onViewDetachedFromWindow not be called!
        if (mListener != null) {
            mListener.onViewDetachedFromWindow();
        }
    }

    private OnEdgeViewListener mListener;

    public void setOnEdgeViewListener(OnEdgeViewListener listener) {
        mListener = listener;
    }

    public interface OnEdgeViewListener {
        void onViewAttachedToWindow();

        void onViewDetachedFromWindow();

        List<Edge> getConfigs(EdgeDirection direction);

        EdgeLab getLabConfig(EdgeDirection direction);

        void onItemClick(int postion);

        void onItemLongClick(int postion);

        void onLongClickStatusbar();

        Weather getWeather();

        Air getAir();

        void saveRadius(EdgeDirection direction, int radius);

        void savePeek(EdgeDirection direction, int peek);

        void saveEdgeWidth(EdgeDirection direction, int width);

        void saveEgdeHeight(EdgeDirection direction, int height);
    }

    final EdgeAdapter.OnEdgeAdapterListener mAdapterListener = new EdgeAdapter.OnEdgeAdapterListener() {
        @Override
        public void onItemClick(int postion) {
            if (mListener != null) {
                mListener.onItemClick(postion);
            }
        }

        @Override
        public void onItemLongClick(int postion) {
            if (mListener != null) {
                mListener.onItemLongClick(postion);
            }
        }
    };

    final OnSeekChangeListener mRadiusListener = new OnSeekChangeListener() {
        @Override
        public void onSeeking(SeekParams seekParams) {
            int progress = seekParams.progress;
            if (mRadiusText != null) {
                mRadiusText.setText(getResources().getString(R.string.radius_format, progress));
            }
            if (mLayoutManager != null) {
                mLayoutManager.setRadius(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
            if (mListener != null) {
                mListener.saveRadius(mEd, seekBar.getProgress());
            }
        }
    };


    final OnSeekChangeListener mPeekListener = new OnSeekChangeListener() {
        @Override
        public void onSeeking(SeekParams seekParams) {
            int progress = seekParams.progress;
            if (mPeekText != null) {
                mPeekText.setText(getResources().getString(R.string.peek_format, progress));
            }
            if (mLayoutManager != null) {
                mLayoutManager.setPeekDistance(progress);
            }
        }

        @Override
        public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
            if (mListener != null) {
                mListener.savePeek(mEd, seekBar.getProgress());
            }
        }
    };


    final OnSeekChangeListener mWidthListener = new OnSeekChangeListener() {
        @Override
        public void onSeeking(SeekParams seekParams) {
            int progress = seekParams.progress;
            if (mWidthText != null) {
                mWidthText.setText(getResources().getString(R.string.edge_width_format, progress));
            }
            ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) mLayoutGroups.getLayoutParams();
            params.width = progress;
            mLayoutGroups.setLayoutParams(params);
        }

        @Override
        public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
            if (mListener != null) {
                mListener.saveEdgeWidth(mEd, seekBar.getProgress());
            }
        }
    };

    final OnSeekChangeListener mHeightListener = new OnSeekChangeListener() {
        @Override
        public void onSeeking(SeekParams seekParams) {
            int progress = seekParams.progress;
            if (mHeightText != null) {
                mHeightText.setText(getResources().getString(R.string.edge_height_format, progress));
            }
            ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) mLayoutGroups.getLayoutParams();
            params.height = progress;
            mLayoutGroups.setLayoutParams(params);

        }

        @Override
        public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
            if (mListener != null) {
                mListener.saveEgdeHeight(mEd, seekBar.getProgress());
            }
        }
    };

}
