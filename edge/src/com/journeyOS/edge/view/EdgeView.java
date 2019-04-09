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

    View mMask;
    View mRootView;
    View mLayoutGroups;
    View mLayoutStatus;

    TextView mStatusBarText1;
    TextView mStatusBarText2;
    TextView mStatusBarText3;

    //lab
    LocusLayoutManager mLayoutManager;
    EdgeAdapter mAdapter;
    RecyclerView mListView;

    View mDebug;
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
        mMask = findViewById(R.id.mask_bg);
        mLayoutStatus = findViewById(R.id.layout_statusbar);
        mLayoutStatus.setOnLongClickListener(this);

        mLayoutGroups = findViewById(R.id.layout_groups_lab);
        mListView = (RecyclerView) findViewById(R.id.recycler_view);

        mDebug = findViewById(R.id.control_panel);
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
        }

        mStatusBarText1 = (TextView) findViewById(R.id.statusbar_text1);
        mStatusBarText2 = (TextView) findViewById(R.id.statusbar_text2);
        mStatusBarText3 = (TextView) findViewById(R.id.statusbar_text3);

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
                    if (edgeLab != null) {
                        updateEdgeLayout(edgeLab.width, edgeLab.height);
                    }

                    final List<Edge> configs = mListener.getConfigs(mEd);
                    if (!BaseUtils.isNull(configs)) {
                        CoreManager.getDefault().getImpl(ICoreExecutors.class).mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                initEdgeSeekBar(edgeLab);
                                //updateEdgeLayout(edgeLab.width, edgeLab.height);

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
                    if (weather != null && weather.HeWeather6 != null) {
                        final Weather.HeWeather6Bean heWeather = weather.HeWeather6.get(0);
                        if (heWeather != null) {
                            CoreManager.getDefault().getImpl(ICoreExecutors.class).mainThread().execute(new Runnable() {
                                @Override
                                public void run() {
                                    if (heWeather.daily_forecast != null) {
                                        Weather.HeWeather6Bean.DailyForecastBean daily = heWeather.daily_forecast.get(0);
                                        Weather.HeWeather6Bean.BasicBean basic = heWeather.basic;
                                        if (daily != null && basic != null) {
                                            if (mStatusBarText1 != null) {
                                                mStatusBarText1.setText(basic.location + "：" + daily.cond_txt_d);
                                            }
                                            if (mStatusBarText2 != null) {
                                                mStatusBarText2.setText(daily.tmp_min + REGION + daily.tmp_max + TEMPERATURE);
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    }

                    Air air = mListener.getAir();
                    if (air != null && air.HeWeather6 != null) {
                        final Air.HeWeather6Bean.AirNowCityBean airNowCity = air.HeWeather6.get(0).air_now_city;
                        if (airNowCity != null) {
                            CoreManager.getDefault().getImpl(ICoreExecutors.class).mainThread().execute(new Runnable() {
                                @Override
                                public void run() {
                                    if (mStatusBarText3 != null) {
                                        mStatusBarText3.setText(getContext().getString(R.string.weather_air_alty) + airNowCity.qlty);
                                    }
                                }
                            });
                        }
                    }
                }
            });
        }
    }

    public void showEdgeView() {
        if (mEd != null) {
            EdgeLab edgeLab = CoreManager.getDefault().getImpl(IEdgeLabProvider.class).getCacheConfig(mEd.name().toLowerCase());
            if (edgeLab != null) {
                updateEdgeLayout(edgeLab.width, edgeLab.height);
            }
        }
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
