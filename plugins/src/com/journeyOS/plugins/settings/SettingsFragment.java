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

package com.journeyOS.plugins.settings;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.journeyOS.base.Constant;
import com.journeyOS.base.persistence.SpUtils;
import com.journeyOS.base.widget.SettingSwitch;
import com.journeyOS.base.widget.SettingView;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.api.edge.IEdge;
import com.journeyOS.core.api.plugins.IPlugins;
import com.journeyOS.core.api.ui.IContainer;
import com.journeyOS.core.base.BaseFragment;
import com.journeyOS.core.permission.IPermission;
import com.journeyOS.plugins.R;
import com.journeyOS.plugins.R2;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class SettingsFragment extends BaseFragment {

    @BindView(R2.id.daemon)
    SettingSwitch mDaemon;

    @BindView(R2.id.exclude)
    SettingSwitch mExclude;

    @BindView(R2.id.ball)
    SettingSwitch mBall;

    @BindView(R2.id.status_bar)
    SettingSwitch mStatusBar;

    @BindView(R2.id.edge_item_txt)
    SettingSwitch mItemText;

    @BindView(R2.id.edge_count)
    SettingView mEdgeCount;

    @BindView(R2.id.innerBall)
    SettingView mInnerBall;

    @BindView(R2.id.ball_size)
    IndicatorSeekBar mBallSize;

    static Activity mContext;

    public static Fragment newInstance(Activity activity) {
        SettingsFragment fragment = new SettingsFragment();
        mContext = activity;
        return fragment;
    }

    @Override
    public int attachLayoutRes() {
        return R.layout.fragment_settings;
    }

    @Override
    public void initBeforeView() {
        super.initBeforeView();
    }

    @Override
    public void initViews() {
        boolean daemon = SpUtils.getInstant().getBoolean(Constant.DAEMON, Constant.DAEMON_DEFAULT);
        mDaemon.setCheckedImmediately(daemon);

        boolean exclude = SpUtils.getInstant().getBoolean(Constant.EXCLUDE, Constant.EXCLUDE_DEFAULT);
        mExclude.setCheckedImmediately(exclude);

        boolean ball = SpUtils.getInstant().getBoolean(Constant.BALL, Constant.BALL_DEFAULT);
        mBall.setCheckedImmediately(ball);
        if (ball && CoreManager.getDefault().getImpl(IPermission.class).canDrawOverlays(mContext)) {
            CoreManager.getDefault().getImpl(IEdge.class).showingOrHidingBall(true);
        }

        boolean statusBar = SpUtils.getInstant().getBoolean(Constant.EDGE_LAB_DEBUG, Constant.EDGE_LAB_DEBUG_DEFAULT);
        mStatusBar.setCheckedImmediately(statusBar);

        boolean itemText = SpUtils.getInstant().getBoolean(Constant.EDGE_ITEM_TEXT, Constant.EDGE_ITEM_TEXT_DEFAULT);
        mItemText.setCheckedImmediately(itemText);

        int count = SpUtils.getInstant().getInt(Constant.EDGE_CONUT, Constant.EDGE_CONUT_DEFAULT);
        mEdgeCount.setRightSummary(mContext.getString(Constant.sEdgeCountMap.get(count)));

        mBallSize.setProgress(SpUtils.getInstant().getInt(Constant.BALL_SIZE, Constant.BALL_SIZE_DEFAULT));
        mBallSize.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                CoreManager.getDefault().getImpl(IEdge.class).updateBallSize(seekParams.progress);
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
                SpUtils.getInstant().put(Constant.BALL_SIZE, seekBar.getProgress());
            }
        });
    }

    @OnClick({R2.id.daemon})
    public void listenerAutoStart() {
        boolean daemon = SpUtils.getInstant().getBoolean(Constant.DAEMON, Constant.DAEMON_DEFAULT);
        mDaemon.setCheck(!daemon);
        SpUtils.getInstant().put(Constant.DAEMON, !daemon);
    }

    @OnClick({R2.id.exclude})
    public void listenerExclude() {
        boolean exclude = SpUtils.getInstant().getBoolean(Constant.EXCLUDE, Constant.EXCLUDE_DEFAULT);
        mExclude.setCheck(!exclude);
        SpUtils.getInstant().put(Constant.EXCLUDE, !exclude);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityManager am = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
            if (am != null) {
                List<ActivityManager.AppTask> tasks = am.getAppTasks();
                if (tasks != null && tasks.size() > 0) {
                    ActivityManager.AppTask task = tasks.get(0);
                    if (task != null) {
                        task.setExcludeFromRecents(!exclude);
                    }
                }
            }
        }
    }

    @OnClick({R2.id.ball})
    public void listenerBall() {
        if (!CoreManager.getDefault().getImpl(IPermission.class).canDrawOverlays(mContext)) {
            CoreManager.getDefault().getImpl(IPermission.class).drawOverlays(mContext, true);
            return;
        }

        boolean ball = SpUtils.getInstant().getBoolean(Constant.BALL, Constant.BALL_DEFAULT);
        mBall.setCheck(!ball);
        SpUtils.getInstant().put(Constant.BALL, !ball);
        CoreManager.getDefault().getImpl(IEdge.class).showingOrHidingBall(!ball);
    }

    @OnClick({R2.id.portrait})
    public void listenerPortrait() {
        Fragment fragment = CoreManager.getDefault().getImpl(IPlugins.class).provideGestureFragment(mContext, Configuration.ORIENTATION_PORTRAIT);
        CoreManager.getDefault().getImpl(IContainer.class).subActivity(mContext, fragment, mContext.getString(R.string.gesture_portrait));
    }

    @OnClick({R2.id.landscape})
    public void listenerLandscape() {
        Fragment fragment = CoreManager.getDefault().getImpl(IPlugins.class).provideGestureFragment(mContext, Configuration.ORIENTATION_LANDSCAPE);
        CoreManager.getDefault().getImpl(IContainer.class).subActivity(mContext, fragment, mContext.getString(R.string.gesture_landscape));
    }

    @OnClick({R2.id.edge_item_txt})
    public void listenerItemText() {
        boolean itemText = SpUtils.getInstant().getBoolean(Constant.EDGE_ITEM_TEXT, Constant.EDGE_ITEM_TEXT_DEFAULT);
        mItemText.setCheck(!itemText);
        SpUtils.getInstant().put(Constant.EDGE_ITEM_TEXT, !itemText);
    }

    @OnClick({R2.id.status_bar})
    public void listenerStatusBar() {
        boolean debug = SpUtils.getInstant().getBoolean(Constant.EDGE_LAB_DEBUG, Constant.EDGE_LAB_DEBUG_DEFAULT);
        mStatusBar.setCheck(!debug);
        SpUtils.getInstant().put(Constant.EDGE_LAB_DEBUG, !debug);
    }

    @OnClick({R2.id.edge_count})
    public void listenerCount() {
        final String[] items = mContext.getResources().getStringArray(R.array.edge_count_array);
        int item = SpUtils.getInstant().getInt(Constant.EDGE_CONUT, Constant.EDGE_CONUT_DEFAULT) - 6;

        final AlertDialog dialog = new AlertDialog.Builder(mContext, R.style.CornersAlertDialog)
                .setTitle(mContext.getString(R.string.count))
                .setSingleChoiceItems(items, item, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        SpUtils.getInstant().put(Constant.EDGE_CONUT, which + 6);
                        mEdgeCount.setRightSummary(mContext.getString(Constant.sEdgeCountMap.get(which + 6)));
//                        setViewsEnabled((which + 6) != Constant.EDGE_STYLE_DINFINE);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }

    @OnClick({R2.id.innerBall})
    public void listenerInnerBall() {
        ColorPickerDialogBuilder
                .with(mContext, R.style.CornersAlertDialog)
                .setTitle(mContext.getString(R.string.inner_ball_color_title))
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .density(9)
                .noSliders()
                .showLightnessSlider(true)
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
                        CoreManager.getDefault().getImpl(IEdge.class).updateInnerBall(selectedColor);
                    }
                })
                .setPositiveButton(android.R.string.ok, new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        SpUtils.getInstant().put(Constant.INNER_BALL_COLOR, selectedColor);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int color = SpUtils.getInstant().getInt(Constant.INNER_BALL_COLOR, Constant.INNER_BALL_COLOR_DEFAULT);
                        CoreManager.getDefault().getImpl(IEdge.class).updateInnerBall(color);
                    }
                })
                .build()
                .show();
    }

}
