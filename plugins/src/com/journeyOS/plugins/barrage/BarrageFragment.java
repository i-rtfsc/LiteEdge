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

package com.journeyOS.plugins.barrage;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import com.journeyOS.core.api.barrage.IBarrage;
import com.journeyOS.core.api.ui.IContainer;
import com.journeyOS.core.base.BaseFragment;
import com.journeyOS.core.permission.IPermission;
import com.journeyOS.plugins.R;
import com.journeyOS.plugins.R2;

import butterknife.BindView;
import butterknife.OnClick;

public class BarrageFragment extends BaseFragment {
    private static final String TAG = BarrageFragment.class.getSimpleName();

    static Activity mContext;

    @BindView(R2.id.barrage)
    SettingSwitch mBarrage;

    @BindView(R2.id.barrage_selector)
    SettingView mBarrageSelector;

    @BindView(R2.id.barrage_speed)
    SettingView mBarrageSpeed;

    @BindView(R2.id.barrage_click)
    SettingView mBarrageClick;

    @BindView(R2.id.barrage_title)
    SettingView mBarrageTitle;

    @BindView(R2.id.barrage_summary)
    SettingView mBarrageSummary;

    @BindView(R2.id.barrage_background)
    SettingView mBarrageBackground;

    @BindView(R2.id.barrage_test)
    SettingView mBarrageTest;


    public static Fragment newInstance(Activity activity) {
        BarrageFragment fragment = new BarrageFragment();
        mContext = activity;
        return fragment;
    }

    @Override
    public int attachLayoutRes() {
        return R.layout.fragment_barrage;
    }

    @Override
    public void initBeforeView() {
        super.initBeforeView();
    }

    @Override
    public void initViews() {
        boolean barrage = SpUtils.getInstant().getBoolean(Constant.BARRAGE, Constant.BARRAGE_DEFAULT);
        mBarrage.setCheckedImmediately(barrage);

        if (!barrage) {
            mBarrageSelector.setEnabled(false);
        }

        mBarrageClick.setRightSummary(mContext.getResources().getStringArray(R.array.barrage_click_feedback_array)[SpUtils.getInstant().getInt(Constant.BARRAGE_CLICK, Constant.BARRAGE_CLICK_DEFAULT)]);

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

        int count = SpUtils.getInstant().getInt(Constant.BARRAGE_SPEED, Constant.BARRAGE_SPEED_DEFAULT) - 1;
        mBarrageSpeed.setRightSummary(mContext.getResources().getStringArray(R.array.barrage_speed_array)[count]);
    }

    @OnClick({R2.id.barrage})
    public void listenerBarrage() {
        if (!CoreManager.getDefault().getImpl(IPermission.class).hasListenerNotification(mContext)) {
            CoreManager.getDefault().getImpl(IPermission.class).listenerNotification(mContext, true);
        }
        boolean barrage = SpUtils.getInstant().getBoolean(Constant.BARRAGE, Constant.BARRAGE_DEFAULT);
        mBarrage.setCheck(!barrage);
        SpUtils.getInstant().put(Constant.BARRAGE, !barrage);

        mBarrageSelector.setEnabled(!barrage);
        mBarrageSelector.setAlpha(!barrage ? 1.0f : 0.5f);
    }

    @OnClick({R2.id.barrage_selector})
    public void listenerBarrageSelector() {
        CoreManager.getDefault().getImpl(IContainer.class).subActivity(mContext, BarrageSelectorFragment.newInstance(mContext), mContext.getString(R.string.barrage_whitelist));
    }

    @OnClick({R2.id.barrage_speed})
    public void listenerSpeed() {
        CoreManager.getDefault().getImpl(IBarrage.class).removeBarrage();

        final String[] items = mContext.getResources().getStringArray(R.array.barrage_speed_array);
        int item = SpUtils.getInstant().getInt(Constant.BARRAGE_SPEED, Constant.BARRAGE_SPEED_DEFAULT) - 1;

        final AlertDialog dialog = new AlertDialog.Builder(mContext, R.style.CornersAlertDialog)
                .setTitle(R.string.barrage_speed_title)
                .setSingleChoiceItems(items, item, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        SpUtils.getInstant().put(Constant.BARRAGE_SPEED, which + 1);
                        mBarrageSpeed.setRightSummary(mContext.getResources().getStringArray(R.array.barrage_speed_array)[which]);
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
                        mBarrageClick.setRightSummary(mContext.getResources().getStringArray(R.array.barrage_click_feedback_array)[which]);
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
                .showLightnessSlider(true)
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
                .showLightnessSlider(true)
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
                .showLightnessSlider(true)
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

    @OnClick({R2.id.barrage_test})
    public void listenerBarrageTest() {
        if (!CoreManager.getDefault().getImpl(IPermission.class).canDrawOverlays(mContext)) {
            CoreManager.getDefault().getImpl(IPermission.class).drawOverlays(mContext, true);
            return;
        }

        if (!CoreManager.getDefault().getImpl(IPermission.class).hasListenerNotification(mContext)) {
            CoreManager.getDefault().getImpl(IPermission.class).listenerNotification(mContext, true);
            return;
        }

        CoreManager.getDefault().getImpl(IBarrage.class).sendBarrage();
    }

}
