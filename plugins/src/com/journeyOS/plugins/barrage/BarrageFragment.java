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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

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
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.IndicatorStayLayout;
import com.warkiz.widget.IndicatorType;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;
import com.warkiz.widget.TickMarkType;

import butterknife.BindView;
import butterknife.OnClick;

public class BarrageFragment extends BaseFragment {
    private static final String TAG = BarrageFragment.class.getSimpleName();

    static Activity mContext;

    @BindView(R2.id.barrage)
    SettingSwitch mBarrage;

    @BindView(R2.id.barrage_selector)
    SettingView mBarrageSelector;

    @BindView(R2.id.barrage_direction)
    SettingView mBarrageDirection;

    @BindView(R2.id.barrage_postion)
    SettingView mBarragePostion;

    @BindView(R2.id.barrage_speed)
    SettingView mBarrageSpeed;

    @BindView(R2.id.barrage_avatar_size)
    SettingView mBarrageAvatarSize;

    @BindView(R2.id.barrage_text_size)
    SettingView mBarrageTextSize;

    @BindView(R2.id.barrage_click)
    SettingSwitch mBarrageClick;

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

        mBarrageDirection.setRightSummary(mContext.getResources().getStringArray(R.array.barrage_direction_array)[SpUtils.getInstant().getInt(Constant.BARRAGE_DIRECTION, Constant.BARRAGE_DIRECTION_DEFAULT) - 1]);

        mBarrageClick.setCheckedImmediately(SpUtils.getInstant().getBoolean(Constant.BARRAGE_CLICK, Constant.BARRAGE_CLICK_DEFAULT));

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

        int postion = SpUtils.getInstant().getInt(Constant.BARRAGE_POSTION, Constant.BARRAGE_POSTION_DEFAULT);
        final String[] postionItems = mContext.getResources().getStringArray(R.array.barrage_postion_array);
        mBarragePostion.setRightSummary(postionItems[postion - 1]);

        int count = SpUtils.getInstant().getInt(Constant.BARRAGE_SPEED, Constant.BARRAGE_SPEED_DEFAULT);
        mBarrageSpeed.setRightSummary(String.valueOf(count));

        int avatarSize = SpUtils.getInstant().getInt(Constant.BARRAGE_AVATAR_SIZE, Constant.BARRAGE_AVATAR_SIZE_DEFAULT);
        mBarrageAvatarSize.setRightSummary(String.valueOf(avatarSize));

        int textSize = SpUtils.getInstant().getInt(Constant.BARRAGE_TEXT_SIZE, Constant.BARRAGE_TEXT_SIZE_DEFAULT);
        mBarrageTextSize.setRightSummary(String.valueOf(textSize));
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

    @OnClick({R2.id.barrage_filter})
    public void listenerBarrageFilter() {
        CoreManager.getDefault().getImpl(IContainer.class).subActivity(mContext, BarrageFliterFragment.newInstance(mContext), mContext.getString(R.string.barrage_filter));
    }

    @OnClick({R2.id.barrage_direction})
    public void listenerDirection() {
        final String[] items = mContext.getResources().getStringArray(R.array.barrage_direction_array);
        int item = SpUtils.getInstant().getInt(Constant.BARRAGE_DIRECTION, Constant.BARRAGE_DIRECTION_DEFAULT) - 1;

        final AlertDialog dialog = new AlertDialog.Builder(mContext, R.style.CornersAlertDialog)
                .setTitle(mContext.getString(R.string.barrage_direction_title))
                .setSingleChoiceItems(items, item, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        SpUtils.getInstant().put(Constant.BARRAGE_DIRECTION, which + 1);
                        mBarrageDirection.setRightSummary(mContext.getResources().getStringArray(R.array.barrage_direction_array)[which]);
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

    @OnClick({R2.id.barrage_postion})
    public void listenerPostion() {
        final String[] items = mContext.getResources().getStringArray(R.array.barrage_postion_array);
        AlertDialog.Builder buider = new AlertDialog.Builder(mContext, R.style.CornersAlertDialog);
        buider.setTitle(R.string.barrage_postion_title);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_defined, null);
        buider.setView(dialogView);
        buider.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        LinearLayout linearLayout = dialogView.findViewById(R.id.rootLayout);

        int progress = SpUtils.getInstant().getInt(Constant.BARRAGE_POSTION, Constant.BARRAGE_POSTION_DEFAULT);

        IndicatorSeekBar seekBar = IndicatorSeekBar.with(getContext())
                .max(4)
                .min(1)
                .progress(progress)
                .tickCount(4)
                .showTickMarksType(TickMarkType.DIVIDER)
                .tickMarksColor(mContext.getResources().getColor(R.color.red))
                .tickTextsArray(items)
                .showTickTexts(true)
                .showIndicatorType(IndicatorType.CIRCULAR_BUBBLE)
                .indicatorColor(mContext.getResources().getColor(R.color.colorPrimary))
                .thumbColor(mContext.getResources().getColor(R.color.colorPrimary))
                .trackProgressColor(mContext.getResources().getColor(R.color.colorPrimary))
                .build();

        IndicatorStayLayout stayLayout = new IndicatorStayLayout(getContext());
        stayLayout.attachTo(seekBar);
        linearLayout.addView(stayLayout);

        seekBar.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {

            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
                int progress = seekBar.getProgress();
                SpUtils.getInstant().put(Constant.BARRAGE_POSTION, progress);
                mBarragePostion.setRightSummary(items[progress - 1]);
            }
        });
        buider.create().show();

    }

    @OnClick({R2.id.barrage_speed})
    public void listenerSpeed() {
        AlertDialog.Builder buider = new AlertDialog.Builder(mContext, R.style.CornersAlertDialog);
        buider.setTitle(R.string.barrage_speed_title);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_defined, null);
        buider.setView(dialogView);
        buider.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        LinearLayout linearLayout = dialogView.findViewById(R.id.rootLayout);

        int progress = SpUtils.getInstant().getInt(Constant.BARRAGE_SPEED, Constant.BARRAGE_SPEED_DEFAULT);
        IndicatorSeekBar seekBar = IndicatorSeekBar.with(getContext())
                .max(150)
                .min(0)
                .progress(progress)
                .showTickMarksType(TickMarkType.OVAL)
                .showIndicatorType(IndicatorType.CIRCULAR_BUBBLE)
                .indicatorColor(mContext.getResources().getColor(R.color.colorPrimary))
                .thumbColor(mContext.getResources().getColor(R.color.colorPrimary))
                .trackProgressColor(mContext.getResources().getColor(R.color.colorPrimary))
                .build();

        IndicatorStayLayout stayLayout = new IndicatorStayLayout(getContext());
        stayLayout.attachTo(seekBar);
        linearLayout.addView(stayLayout);

        seekBar.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {

            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
                int postion = seekBar.getProgress();
                SpUtils.getInstant().put(Constant.BARRAGE_SPEED, postion);
                mBarrageSpeed.setRightSummary(String.valueOf(postion));
            }
        });
        buider.create().show();
    }

    @OnClick({R2.id.barrage_avatar_size})
    public void listenerAvatarSize() {
        AlertDialog.Builder buider = new AlertDialog.Builder(mContext, R.style.CornersAlertDialog);
        buider.setTitle(R.string.barrage_avatar_size);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_defined, null);
        buider.setView(dialogView);
        buider.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        LinearLayout linearLayout = dialogView.findViewById(R.id.rootLayout);

        int progress = SpUtils.getInstant().getInt(Constant.BARRAGE_AVATAR_SIZE, Constant.BARRAGE_AVATAR_SIZE_DEFAULT);
        IndicatorSeekBar seekBar = IndicatorSeekBar.with(getContext())
                .max(100)
                .min(50)
                .progress(progress)
                .showTickMarksType(TickMarkType.OVAL)
                .showIndicatorType(IndicatorType.CIRCULAR_BUBBLE)
                .indicatorColor(mContext.getResources().getColor(R.color.colorPrimary))
                .thumbColor(mContext.getResources().getColor(R.color.colorPrimary))
                .trackProgressColor(mContext.getResources().getColor(R.color.colorPrimary))
                .build();

        IndicatorStayLayout stayLayout = new IndicatorStayLayout(getContext());
        stayLayout.attachTo(seekBar);
        linearLayout.addView(stayLayout);

        seekBar.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
                int avatarSize = seekBar.getProgress();
                SpUtils.getInstant().put(Constant.BARRAGE_AVATAR_SIZE, avatarSize);
                mBarrageAvatarSize.setRightSummary(String.valueOf(avatarSize));
            }
        });
        buider.create().show();
    }

    @OnClick({R2.id.barrage_text_size})
    public void listenerTextSize() {
        AlertDialog.Builder buider = new AlertDialog.Builder(mContext, R.style.CornersAlertDialog);
        buider.setTitle(R.string.barrage_text_size);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_defined, null);
        buider.setView(dialogView);
        buider.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        LinearLayout linearLayout = dialogView.findViewById(R.id.rootLayout);

        int progress = SpUtils.getInstant().getInt(Constant.BARRAGE_TEXT_SIZE, Constant.BARRAGE_TEXT_SIZE_DEFAULT);
        IndicatorSeekBar seekBar = IndicatorSeekBar.with(getContext())
                .max(25)
                .min(15)
                .progress(progress)
                .showTickMarksType(TickMarkType.OVAL)
                .showIndicatorType(IndicatorType.CIRCULAR_BUBBLE)
                .indicatorColor(mContext.getResources().getColor(R.color.colorPrimary))
                .thumbColor(mContext.getResources().getColor(R.color.colorPrimary))
                .trackProgressColor(mContext.getResources().getColor(R.color.colorPrimary))
                .build();

        IndicatorStayLayout stayLayout = new IndicatorStayLayout(getContext());
        stayLayout.attachTo(seekBar);
        linearLayout.addView(stayLayout);

        seekBar.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
                int textSize = seekBar.getProgress();
                SpUtils.getInstant().put(Constant.BARRAGE_TEXT_SIZE, textSize);
                mBarrageTextSize.setRightSummary(String.valueOf(textSize));
            }
        });
        buider.create().show();
    }

    @OnClick({R2.id.barrage_click})
    public void listenerClick() {
        boolean barrageClick = SpUtils.getInstant().getBoolean(Constant.BARRAGE_CLICK, Constant.BARRAGE_CLICK_DEFAULT);
        mBarrageClick.setCheck(!barrageClick);
        SpUtils.getInstant().put(Constant.BARRAGE_CLICK, !barrageClick);
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

    @OnClick({R2.id.barrage_background_style})
    public void listenerBarrageBackgroundStyle() {
        AlertDialog.Builder buider = new AlertDialog.Builder(mContext, R.style.CornersAlertDialog);
        buider.setTitle(R.string.barrage_background_style);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_barrage_background, null);
        buider.setView(dialogView);
        buider.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        //top left
        IndicatorSeekBar topLeftSeekbar = dialogView.findViewById(R.id.top_left_radius);
        int topLeftProgress = SpUtils.getInstant().getInt(Constant.BARRAGE_BACKGROUND_TOP_LEFT, Constant.BARRAGE_BACKGROUND_TOP_LEFT_DEFAULT);
        topLeftSeekbar.setProgress(topLeftProgress);
        topLeftSeekbar.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
                SpUtils.getInstant().put(Constant.BARRAGE_BACKGROUND_TOP_LEFT, seekBar.getProgress());
            }
        });

        //top right
        IndicatorSeekBar topRightSeekbar = dialogView.findViewById(R.id.top_right_radius);
        int topRightProgress = SpUtils.getInstant().getInt(Constant.BARRAGE_BACKGROUND_TOP_RIGHT, Constant.BARRAGE_BACKGROUND_TOP_RIGHT_DEFAULT);
        topRightSeekbar.setProgress(topRightProgress);
        topRightSeekbar.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
                SpUtils.getInstant().put(Constant.BARRAGE_BACKGROUND_TOP_RIGHT, seekBar.getProgress());
            }
        });
        //bottom right
        IndicatorSeekBar bottomRightSeekbar = dialogView.findViewById(R.id.bottom_right_radius);
        int bottomRightProgress = SpUtils.getInstant().getInt(Constant.BARRAGE_BACKGROUND_BOTTOM_RIGHT, Constant.BARRAGE_BACKGROUND_BOTTOM_RIGHT_DEFAULT);
        bottomRightSeekbar.setProgress(bottomRightProgress);
        bottomRightSeekbar.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
                SpUtils.getInstant().put(Constant.BARRAGE_BACKGROUND_BOTTOM_RIGHT, seekBar.getProgress());
            }
        });
        //bottom left
        IndicatorSeekBar bottomLeftSeekbar = dialogView.findViewById(R.id.bottom_left_radius);
        int bottomLeftProgress = SpUtils.getInstant().getInt(Constant.BARRAGE_BACKGROUND_BOTTOM_LEFT, Constant.BARRAGE_BACKGROUND_BOTTOM_LEFT_DEFAULT);
        bottomLeftSeekbar.setProgress(bottomLeftProgress);
        bottomLeftSeekbar.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {
                SpUtils.getInstant().put(Constant.BARRAGE_BACKGROUND_BOTTOM_LEFT, seekBar.getProgress());
            }
        });
        buider.create().show();
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
