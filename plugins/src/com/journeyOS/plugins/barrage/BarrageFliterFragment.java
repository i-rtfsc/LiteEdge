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
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;

import com.journeyOS.base.Constant;
import com.journeyOS.base.persistence.SpUtils;
import com.journeyOS.base.utils.Base64Utils;
import com.journeyOS.base.utils.JsonHelper;
import com.journeyOS.base.widget.LabelTextView;
import com.journeyOS.core.base.BaseFragment;
import com.journeyOS.plugins.R;
import com.journeyOS.plugins.R2;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class BarrageFliterFragment extends BaseFragment {
    private static final String TAG = BarrageFliterFragment.class.getSimpleName();

    static Activity mContext;

    @BindView(R2.id.label)
    LabelTextView mLabel;

    @BindView(R2.id.edit_add)
    EditText mEditText;

    private List<String> labels = new ArrayList<>();

    private String label;

    public static Fragment newInstance(Activity activity) {
        BarrageFliterFragment fragment = new BarrageFliterFragment();
        mContext = activity;
        return fragment;
    }

    @Override
    public int attachLayoutRes() {
        return R.layout.fragment_barrage_fliter;
    }

    @Override
    public void initBeforeView() {
        super.initBeforeView();

        String fliter = SpUtils.getInstant().getString(Constant.BARRAGE_FLITER, Constant.BARRAGE_FLITER_DEFAULT);
        if (fliter == null) {
            labels.add("下载");
            labels.add("正在");
            labels.add("M/S");
            labels.add("K/S");
            labels.add("B/S");
            SpUtils.getInstant().put(Constant.BARRAGE_FLITER, Base64Utils.toBase64(JsonHelper.toJson(labels)));
        } else {
            labels = JsonHelper.fromJson(Base64Utils.fromBase64(fliter), List.class);
        }
    }

    @Override
    public void initViews() {
        mLabel.setLabels(labels);
        mLabel.setOnLabelClickListener(new LabelTextView.OnLabelClickListener() {
            @Override
            public void onClick(int index, View v, String s) {
                labels.remove(index);
                mLabel.setLabels(labels);
                SpUtils.getInstant().put(Constant.BARRAGE_FLITER, Base64Utils.toBase64(JsonHelper.toJson(labels)));
            }
        });
    }

    @Override
    protected void initDataObserver(Bundle savedInstanceState) {
        super.initDataObserver(savedInstanceState);

    }

    @OnTextChanged(value = R2.id.edit_add, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void registerPasswordChanged(Editable s) {
        label = s.toString();
    }

    @OnClick({R2.id.btn_add})
    public void listenerBtnAdd() {
        if (label != null || label != "") {
            labels.add(label);
            mLabel.setLabels(labels);
            mEditText.setText("");
            SpUtils.getInstant().put(Constant.BARRAGE_FLITER, Base64Utils.toBase64(JsonHelper.toJson(labels)));
        }
    }

}
