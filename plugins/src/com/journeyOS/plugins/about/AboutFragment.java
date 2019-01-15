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

package com.journeyOS.plugins.about;

import android.app.Activity;
import android.support.v4.app.Fragment;

import com.journeyOS.base.Constant;
import com.journeyOS.base.utils.BaseUtils;
import com.journeyOS.base.widget.SettingView;
import com.journeyOS.core.Version;
import com.journeyOS.core.base.BaseFragment;
import com.journeyOS.plugins.R;
import com.journeyOS.plugins.R2;

import butterknife.BindView;
import butterknife.OnClick;

public class AboutFragment extends BaseFragment {

    @BindView(R2.id.version)
    SettingView mVersion;
    @BindView(R2.id.email)
    SettingView mEmail;


    static Activity mContext;

    public static Fragment newInstance(Activity activity) {
        AboutFragment fragment = new AboutFragment();
        mContext = activity;
        return fragment;
    }

    @Override
    public int attachLayoutRes() {
        return R.layout.fragment_about;
    }

    @Override
    public void initViews() {
        mVersion.setSummary(Version.getVersionName(mContext));
        mEmail.setSummary(Constant.EMAIL);
    }

    @OnClick({R2.id.version})
    void listenerVersion() {
        BaseUtils.openInMarket(mContext);
    }

    @OnClick({R2.id.email})
    void listenerEmail() {
        BaseUtils.launchEmail(mContext, Constant.EMAIL);
    }

}
