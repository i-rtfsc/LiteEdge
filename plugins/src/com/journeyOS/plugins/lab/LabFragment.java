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
import android.support.v4.app.Fragment;

import com.journeyOS.core.base.BaseFragment;
import com.journeyOS.plugins.R;

public class LabFragment extends BaseFragment {

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
    }

}
