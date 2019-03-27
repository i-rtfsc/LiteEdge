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
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.journeyOS.base.Constant;
import com.journeyOS.base.persistence.SpUtils;
import com.journeyOS.base.utils.AnimationUtil;
import com.journeyOS.base.utils.AppUtils;
import com.journeyOS.base.utils.BaseUtils;
import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.base.widget.LocusLayoutManager;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.StateMachine;
import com.journeyOS.core.api.thread.ICoreExecutors;
import com.journeyOS.core.database.edge.Edge;
import com.journeyOS.core.database.edgelab.EdgeLab;
import com.journeyOS.core.type.EdgeDirection;
import com.journeyOS.core.weather.Air;
import com.journeyOS.core.weather.Weather;
import com.journeyOS.edge.R;
import com.journeyOS.edge.view.adapter.EdgeAdapter;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class EdgeView extends RelativeLayout implements View.OnClickListener, View.OnLongClickListener, View.OnAttachStateChangeListener {
    private static final String TAG = EdgeView.class.getSimpleName();

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

    View mLayout1;
    View mLayout2;
    View mLayout3;
    View mLayout4;
    View mLayout5;
    View mLayout6;

    CircleImageView mIcon1;
    CircleImageView mIcon2;
    CircleImageView mIcon3;
    CircleImageView mIcon4;
    CircleImageView mIcon5;
    CircleImageView mIcon6;

    TextView mText1;
    TextView mText2;
    TextView mText3;
    TextView mText4;
    TextView mText5;
    TextView mText6;

    //lab
    LocusLayoutManager mLayoutManager;
    EdgeAdapter mAdapter;
    RecyclerView mListView;

    View mDebug;
    SeekBar mRadius;
    TextView mRadiusText;
    SeekBar mPeek;
    TextView mPeekText;


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
        int count = SpUtils.getInstant().getInt(Constant.EDGE_CONUT, Constant.EDGE_CONUT_DEFAULT);
        final boolean edgeLab = (count != Constant.EDGE_STYLE_DINFINE);
        initCommonView(edgeLab);

        mEd = StateMachine.getEdgeDirection();
        LogUtils.d(TAG, "on view inflate, edge direction = " + mEd);
        switch (mEd) {
            case UP:
                initUpView(edgeLab);
                break;
            case LEFT:
                initLeftView(edgeLab);
                break;
            case RIGHT:
                initRightView(edgeLab);
                break;
        }
    }

    private void initCommonView(boolean edgeLab) {
        mRootView = findViewById(R.id.root_view);
        mMask = findViewById(R.id.mask_bg);
        mLayoutStatus = findViewById(R.id.layout_statusbar);
        mLayoutStatus.setOnLongClickListener(this);

        mLayoutGroups = findViewById(R.id.layout_groups);

        if (edgeLab) {
            mLayoutGroups.setVisibility(GONE);
            mLayoutGroups = findViewById(R.id.layout_groups_lab);

            mListView = (RecyclerView) findViewById(R.id.recycler_view);

            mDebug = findViewById(R.id.control_panel);
            mRadius = (SeekBar) findViewById(R.id.seek_radius);
            mRadiusText = (TextView) findViewById(R.id.radius_text);
            mPeek = (SeekBar) findViewById(R.id.seek_peek);
            mPeekText = (TextView) findViewById(R.id.peek_text);
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
        } else {
            findViewById(R.id.layout_groups_lab).setVisibility(GONE);
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
        }

        mStatusBarText1 = (TextView) findViewById(R.id.statusbar_text1);
        mStatusBarText2 = (TextView) findViewById(R.id.statusbar_text2);
        mStatusBarText3 = (TextView) findViewById(R.id.statusbar_text3);

        addOnAttachStateChangeListener(this);
    }

    private void initLeftView(boolean edgeLab) {
        mLayoutStatus.setPivotX(480f);
        mLayoutStatus.setPivotY(48f);
        mLayoutStatus.setRotation(-90f);

        if (!edgeLab) {
            mLayout1.setRotation(-22f);
            mLayout2.setRotation(-13.2f);
            mLayout3.setRotation(-4.4f);
            mLayout4.setRotation(4.4f);
            mLayout5.setRotation(13.2f);
            mLayout6.setRotation(22f);
        }

        mStatusBarWidth = 96f;
        mIconGroupWidth = 339f;
    }

    private void initRightView(boolean edgeLab) {
        mLayoutStatus.setPivotX(480f);
        mLayoutStatus.setPivotY(48f);
        mLayoutStatus.setRotation(90f);

        if (!edgeLab) {
            mLayout1.setRotation(22f);
            mLayout2.setRotation(13.2f);
            mLayout3.setRotation(4.4f);
            mLayout4.setRotation(-4.4f);
            mLayout5.setRotation(-13.2f);
            mLayout6.setRotation(-22f);
        }

        mStatusBarWidth = 96f;
        mIconGroupWidth = 339f;
    }

    private void initUpView(boolean edgeLab) {
        if (!edgeLab) {
            mLayout1.setRotation(24f);
            mLayout2.setRotation(14.4f);
            mLayout3.setRotation(4.8f);
            mLayout4.setRotation(-4.8f);
            mLayout5.setRotation(-14.4f);
            mLayout6.setRotation(-24f);
        }

        mStatusBarHeight = 144f;
        mIconGroupHeight = 384f;
    }

    public void initDatas() {
        int count = SpUtils.getInstant().getInt(Constant.EDGE_CONUT, Constant.EDGE_CONUT_DEFAULT);
        final boolean edgeLabEnale = (count != Constant.EDGE_STYLE_DINFINE);

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
                                if (edgeLabEnale) {
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

                                    mRadius.setOnSeekBarChangeListener(mRadiusListener);
                                    mPeek.setOnSeekBarChangeListener(mPeekListener);
                                    mRadius.setProgress(edgeLab.radius);
                                    mPeek.setProgress(edgeLab.peek);

                                } else {
                                    for (Edge config : configs) {
                                        CircleImageView icon = null;
                                        TextView title = null;

                                        int postion = -1;
                                        String[] items = config.item.split(Constant.SEPARATOR);
                                        if (items != null) {
                                            postion = Integer.parseInt(items[1]);
                                        }
                                        if (postion == 1) {
                                            icon = mIcon1;
                                            title = mText1;
                                        } else if (postion == 2) {
                                            icon = mIcon2;
                                            title = mText2;
                                        } else if (postion == 3) {
                                            icon = mIcon3;
                                            title = mText3;
                                        } else if (postion == 4) {
                                            icon = mIcon4;
                                            title = mText4;
                                        } else if (postion == 5) {
                                            icon = mIcon5;
                                            title = mText5;
                                        } else if (postion == 6) {
                                            icon = mIcon6;
                                            title = mText6;
                                        }

                                        Drawable drawable = null;
                                        String name = null;

                                        boolean isAppExisted = AppUtils.isPackageExisted(getContext(), config.packageName);
                                        if (isAppExisted) {
                                            drawable = AppUtils.getAppIcon(getContext(), config.packageName);
                                            name = AppUtils.getAppName(getContext(), config.packageName, Constant.LENGTH);
                                        }

                                        if (icon != null && drawable != null) {
                                            icon.setImageDrawable(drawable);
                                        }
                                        if (title != null && name != null) {
                                            title.setText(name);
                                        }
                                    }

                                }
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

    final SeekBar.OnSeekBarChangeListener mRadiusListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (mRadiusText != null) {
                mRadiusText.setText(getResources().getString(R.string.radius_format, progress));
            }
            if (fromUser) {
                if (mLayoutManager != null) {
                    mLayoutManager.setRadius(progress);
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // do nothing
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (mListener != null) {
                mListener.saveRadius(mEd, seekBar.getProgress());
            }
        }
    };

    final SeekBar.OnSeekBarChangeListener mPeekListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (mPeekText != null) {
                mPeekText.setText(getResources().getString(R.string.peek_format, progress));
            }
            if (fromUser) {
                if (mLayoutManager != null) {
                    mLayoutManager.setPeekDistance(progress);
                }
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // do nothing
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (mListener != null) {
                mListener.savePeek(mEd, seekBar.getProgress());
            }
        }
    };
}
