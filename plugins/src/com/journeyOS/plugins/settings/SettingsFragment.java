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
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.journeyOS.base.Constant;
import com.journeyOS.base.persistence.SpUtils;
import com.journeyOS.base.widget.SettingSwitch;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.api.edge.IEdge;
import com.journeyOS.core.base.BaseFragment;
import com.journeyOS.plugins.R;
import com.journeyOS.plugins.R2;

import butterknife.BindView;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

public class SettingsFragment extends BaseFragment {

    @BindView(R2.id.daemon)
    SettingSwitch mDaemon;

    @BindView(R2.id.ball)
    SettingSwitch mBall;

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
        boolean daemon = SpUtils.getInstant().getBoolean(Constant.DAEMON, true);
        mDaemon.setCheck(daemon);

        boolean ball = SpUtils.getInstant().getBoolean(Constant.BALL, false);
        mBall.setCheck(ball);
        if (ball && Settings.canDrawOverlays(mContext)) {
            CoreManager.getDefault().getImpl(IEdge.class).showingOrHidingBall(true);
        }
    }

    @OnClick({R2.id.daemon})
    public void listenerAutoStart() {
        boolean daemon = SpUtils.getInstant().getBoolean(Constant.DAEMON, true);
        mDaemon.setCheck(!daemon);
        SpUtils.getInstant().put(Constant.DAEMON, !daemon);
    }

    @OnClick({R2.id.ball})
    public void listenerBall() {
        if (!Settings.canDrawOverlays(mContext)) {
            String message = mContext.getString(R.string.hasnot_permission) + mContext.getString(R.string.overflow);
            Toasty.warning(mContext, message, Toast.LENGTH_SHORT).show();
            return;
        }
        boolean ball = SpUtils.getInstant().getBoolean(Constant.BALL, false);
        mBall.setCheck(!ball);
        SpUtils.getInstant().put(Constant.BALL, !ball);
        CoreManager.getDefault().getImpl(IEdge.class).showingOrHidingBall(!ball);
    }
}
