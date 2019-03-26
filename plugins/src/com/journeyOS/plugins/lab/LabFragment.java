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

package com.journeyOS.plugins.lab;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.journeyOS.base.Constant;
import com.journeyOS.base.persistence.SpUtils;
import com.journeyOS.base.widget.SettingSwitch;
import com.journeyOS.base.widget.SettingView;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.api.barrage.IBarrage;
import com.journeyOS.core.api.edge.IEdge;
import com.journeyOS.core.base.BaseFragment;
import com.journeyOS.core.permission.IPermission;
import com.journeyOS.plugins.R;
import com.journeyOS.plugins.R2;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class LabFragment extends BaseFragment {

    @BindView(R2.id.status_bar)
    SettingSwitch mStatusBar;

    @BindView(R2.id.edge_item_txt)
    SettingSwitch mItemText;

    @BindView(R2.id.edge_count)
    SettingView mEdgeCount;

    @BindView(R2.id.innerBall)
    SettingView mInnerBall;

    @BindView(R2.id.barrage_click)
    SettingView mBarrageClick;

    @BindView(R2.id.barrage_title)
    SettingView mBarrageTitle;

    @BindView(R2.id.barrage_summary)
    SettingView mBarrageSummary;

    @BindView(R2.id.barrage_background)
    SettingView mBarrageBackground;

    static Activity mContext;

    public static Fragment newInstance(Activity activity) {
        LabFragment fragment = new LabFragment();
        mContext = activity;
        return fragment;
    }

    @Override
    public int attachLayoutRes() {
        return R.layout.fragment_lab;
    }

    @Override
    public void initBeforeView() {
        super.initBeforeView();
    }

    @Override
    public void initViews() {
//        boolean toggle = SpUtils.getInstant().getBoolean(Constant.EDGE_LAB, Constant.EDGE_LAB_DEFAULT);
//        mLab.setCheck(toggle);

        boolean statusBar = SpUtils.getInstant().getBoolean(Constant.EDGE_LAB_DEBUG, Constant.EDGE_LAB_DEBUG_DEFAULT);
        mStatusBar.setCheck(statusBar);

        boolean itemText = SpUtils.getInstant().getBoolean(Constant.EDGE_ITEM_TEXT, Constant.EDGE_ITEM_TEXT_DEFAULT);
        mItemText.setCheck(itemText);

        int count = SpUtils.getInstant().getInt(Constant.EDGE_CONUT, Constant.EDGE_CONUT_DEFAULT);
        mEdgeCount.setRightSummary(mContext.getString(Constant.sEdgeCountMap.get(count)));
        setViewsEnabled(count != Constant.EDGE_STYLE_DINFINE);

        mBarrageClick.setRightSummary(mContext.getString(Constant.sBarrageClickMap.get(SpUtils.getInstant().getInt(Constant.BARRAGE_CLICK, Constant.BARRAGE_CLICK_DEFAULT))));

        int titleColor = SpUtils.getInstant().getInt(Constant.BARRAGE_TITLE_COLOR, Constant.BARRAGE_TITLE_COLOR_DEFAULT);
        if (titleColor == 0) {
            titleColor = ContextCompat.getColor(mContext, R.color.hotpink);
        }
        mBarrageTitle.setRightSummaryColor(titleColor);

        int summaryColor = SpUtils.getInstant().getInt(Constant.BARRAGE_SUMMARY_COLOR, Constant.BARRAGE_SUMMARY_COLOR_DEFAULT);
        if (summaryColor == 0) {
            summaryColor = ContextCompat.getColor(mContext, R.color.lavender);
        }
        mBarrageSummary.setRightSummaryColor(summaryColor);

        int backgroundColor = SpUtils.getInstant().getInt(Constant.BARRAGE_BACKGROUND_COLOR, Constant.BARRAGE_BACKGROUND_COLOR_DEFAULT);
        if (backgroundColor == 0) {
            backgroundColor = ContextCompat.getColor(mContext, R.color.divider_dark);
        }
        mBarrageBackground.setRightSummaryColor(backgroundColor);
    }

    private void setViewsEnabled(boolean enabled) {
        List<View> views = new ArrayList<>();
        views.add(mItemText);
        views.add(mStatusBar);
        for (View view : views) {
            setViewEnabled(view, enabled);
        }
    }

    private void setViewEnabled(View view, boolean enabled) {
        view.setEnabled(enabled);
        view.setAlpha(enabled ? 1.0f : 0.5f);
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
                        setViewsEnabled((which + 6) != Constant.EDGE_STYLE_DINFINE);
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

    @OnClick({R2.id.barrage_click})
    public void listenerClick() {
        final String[] items = mContext.getResources().getStringArray(R.array.barrage_click_feedback_array);
        int item = SpUtils.getInstant().getInt(Constant.BARRAGE_CLICK, Constant.BARRAGE_CLICK_DEFAULT);

        final AlertDialog dialog = new AlertDialog.Builder(mContext, R.style.CornersAlertDialog)
                .setTitle(mContext.getString(R.string.barrage_click_dialog_title))
                .setSingleChoiceItems(items, item, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        SpUtils.getInstant().put(Constant.BARRAGE_CLICK, which);
                        mBarrageClick.setRightSummary(mContext.getString(Constant.sBarrageClickMap.get(which)));
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

    @OnClick({R2.id.barrage_title})
    public void listenerBarrageTitle() {
        ColorPickerDialogBuilder
                .with(mContext, R.style.CornersAlertDialog)
                .setTitle(mContext.getString(R.string.barrage_title_color))
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .density(9)
                .noSliders()
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
                    }
                })
                .setPositiveButton(android.R.string.ok, new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        SpUtils.getInstant().put(Constant.BARRAGE_TITLE_COLOR, selectedColor);
                        mBarrageTitle.setRightSummaryColor(selectedColor);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
    }

    @OnClick({R2.id.barrage_summary})
    public void listenerBarrageSummary() {
        ColorPickerDialogBuilder
                .with(mContext, R.style.CornersAlertDialog)
                .setTitle(mContext.getString(R.string.barrage_summary_color))
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .density(9)
                .noSliders()
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
                    }
                })
                .setPositiveButton(android.R.string.ok, new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        SpUtils.getInstant().put(Constant.BARRAGE_SUMMARY_COLOR, selectedColor);
                        mBarrageSummary.setRightSummaryColor(selectedColor);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .build()
                .show();
    }

    @OnClick({R2.id.barrage_background})
    public void listenerBarrageBackground() {
        ColorPickerDialogBuilder
                .with(mContext, R.style.CornersAlertDialog)
                .setTitle(mContext.getString(R.string.barrage_background_color))
                .wheelType(ColorPickerView.WHEEL_TYPE.CIRCLE)
                .density(9)
                .noSliders()
                .setOnColorSelectedListener(new OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int selectedColor) {
                    }
                })
                .setPositiveButton(android.R.string.ok, new ColorPickerClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int selectedColor, Integer[] allColors) {
                        SpUtils.getInstant().put(Constant.BARRAGE_BACKGROUND_COLOR, selectedColor);
                        mBarrageBackground.setRightSummaryColor(selectedColor);
                    }
                })
                .setNegativeButton(R.string.barrage_background_color_default, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int backgroundColor = ContextCompat.getColor(mContext, R.color.divider_dark);
                        SpUtils.getInstant().put(Constant.BARRAGE_BACKGROUND_COLOR, backgroundColor);
                        mBarrageBackground.setRightSummaryColor(backgroundColor);
                    }
                })
                .build()
                .show();
    }

    @OnClick({R2.id.barrage_color_test})
    public void listenerBarrageTest() {
        if (!CoreManager.getDefault().getImpl(IPermission.class).canDrawOverlays(mContext)) {
            CoreManager.getDefault().getImpl(IPermission.class).drawOverlays(mContext);
            return;
        }

        if (!CoreManager.getDefault().getImpl(IPermission.class).hasListenerNotification(mContext)) {
            CoreManager.getDefault().getImpl(IPermission.class).listenerNotification(mContext);
            return;
        }

        CoreManager.getDefault().getImpl(IBarrage.class).sendBarrage();
    }
}
