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
import android.view.View;
import android.widget.EditText;

import com.flask.colorpicker.ColorPickerView;
import com.flask.colorpicker.OnColorSelectedListener;
import com.flask.colorpicker.builder.ColorPickerClickListener;
import com.flask.colorpicker.builder.ColorPickerDialogBuilder;
import com.journeyOS.base.Constant;
import com.journeyOS.base.persistence.SpUtils;
import com.journeyOS.base.utils.BaseUtils;
import com.journeyOS.base.widget.SettingSwitch;
import com.journeyOS.base.widget.SettingView;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.api.edge.IEdge;
import com.journeyOS.core.base.BaseFragment;
import com.journeyOS.plugins.R;
import com.journeyOS.plugins.R2;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

public class LabFragment extends BaseFragment {

    @BindView(R2.id.lab)
    SettingSwitch mLab;

    @BindView(R2.id.status_bar)
    SettingSwitch mStatusBar;

    @BindView(R2.id.edge_item_txt)
    SettingSwitch mItemText;

    @BindView(R2.id.edge_count)
    SettingView mEdgeCount;

    @BindView(R2.id.innerBall)
    SettingView mInnerBall;


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
        boolean toggle = SpUtils.getInstant().getBoolean(Constant.EDGE_LAB, Constant.EDGE_LAB_DEFAULT);
        mLab.setCheck(toggle);

        boolean statusBar = SpUtils.getInstant().getBoolean(Constant.EDGE_LAB_DEBUG, Constant.EDGE_LAB_DEBUG_DEFAULT);
        mStatusBar.setCheck(statusBar);

        boolean itemText = SpUtils.getInstant().getBoolean(Constant.EDGE_ITEM_TEXT, Constant.EDGE_ITEM_TEXT_DEFAULT);
        mItemText.setCheck(itemText);

        int count = SpUtils.getInstant().getInt(Constant.EDGE_CONUT, Constant.EDGE_CONUT_DEFAULT);
        mEdgeCount.setRightSummary(count + "");
    }

    @OnClick({R2.id.lab})
    public void listenerLab() {
        boolean daemon = SpUtils.getInstant().getBoolean(Constant.EDGE_LAB, Constant.EDGE_LAB_DEFAULT);
        mLab.setCheck(!daemon);
        SpUtils.getInstant().put(Constant.EDGE_LAB, !daemon);

        List<View> views = new ArrayList<>();
        views.add(mEdgeCount);
        views.add(mInnerBall);
        views.add(mItemText);
        views.add(mStatusBar);
        for (View view : views) {
            setViewEnabled(view, !daemon);
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
        String title = mContext.getString(R.string.edge_item_txt);
        final EditText et = new EditText(mContext);
        AlertDialog dialog = new AlertDialog.Builder(mContext)
                .setTitle(title)
                .setView(et)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String info = et.getText().toString();
                        if (!BaseUtils.isNull(info)) {
                            try {
                                int count = Integer.parseInt(info);
                                SpUtils.getInstant().put(Constant.EDGE_CONUT, count);
                                mEdgeCount.setRightSummary(count + "");
                            } catch (Exception e) {
                                Toasty.error(mActivity, mContext.getString(R.string.was_not_number));
                            }
                        } else {
                            Toasty.error(mActivity, mContext.getString(R.string.set_user_info_was_null));
                        }
                        //dialog.dismiss();
                    }
                }).show();
    }

    @OnClick({R2.id.innerBall})
    public void listenerInnerBall() {
        ColorPickerDialogBuilder
                .with(mContext)
                .setTitle(mContext.getString(R.string.inner_ball_color_title))
//                .initialColor(currentBackgroundColor)
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
                        int color =  SpUtils.getInstant().getInt(Constant.INNER_BALL_COLOR, Constant.INNER_BALL_COLOR_DEFAULT);
                        CoreManager.getDefault().getImpl(IEdge.class).updateInnerBall(color);
                    }
                })
                .build()
                .show();
    }
}
