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

package com.journeyOS.edge.ui;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.journeyOS.base.Constant;
import com.journeyOS.base.persistence.SpUtils;
import com.journeyOS.base.utils.UIUtils;
import com.journeyOS.base.widget.SettingSwitch;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.api.edgeprovider.ICityProvider;
import com.journeyOS.core.api.thread.ICoreExecutors;
import com.journeyOS.core.base.BaseActivity;
import com.journeyOS.core.permission.IPermission;
import com.journeyOS.edge.EdgeController;
import com.journeyOS.edge.R;
import com.journeyOS.plugins.R2;

import butterknife.BindView;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

public class SettingsActivity extends BaseActivity {

    private Context mContext;

    @BindView(R2.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.daemon)
    SettingSwitch mDaemon;

    @BindView(R.id.ball)
    SettingSwitch mBall;

    @Override
    public void initBeforeView() {
        super.initBeforeView();
        mContext = CoreManager.getDefault().getContext();
    }

    @Override
    public int getContentViewId() {
        return R.layout.activity_settings;
    }

    @Override
    public void initViews() {
        UIUtils.setStatusBarColor(this, this.getResources().getColor(R.color.lightskyblue));
//        mToolbar.setTitle(R.string.tool_bar_title);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        EdgeController.getDefault().bindEgdeService();
        boolean daemon = SpUtils.getInstant().getBoolean(Constant.DAEMON, true);
        mDaemon.setCheck(daemon);

        boolean ball = SpUtils.getInstant().getBoolean(Constant.BALL, false);
        mBall.setCheck(ball);
        if (ball) {
            EdgeController.getDefault().showingOrHidingBall(true);
        }

        CoreManager.getDefault().getImpl(ICoreExecutors.class).diskIOThread().execute(new Runnable() {
            @Override
            public void run() {
                CoreManager.getDefault().getImpl(ICityProvider.class).loadCitys();
            }
        });
    }

    @OnClick({R.id.daemon})
    public void listenerAutoStart() {
        boolean daemon = SpUtils.getInstant().getBoolean(Constant.DAEMON, true);
        mDaemon.setCheck(!daemon);
        SpUtils.getInstant().put(Constant.DAEMON, !daemon);
    }

    @OnClick({R.id.ball})
    public void listenerBall() {
        boolean ball = SpUtils.getInstant().getBoolean(Constant.BALL, false);
        mBall.setCheck(!ball);
        SpUtils.getInstant().put(Constant.BALL, !ball);
        EdgeController.getDefault().showingOrHidingBall(!ball);
    }

    @OnClick({R.id.overflow})
    public void overflowPermission() {
        boolean hasPermission = CoreManager.getDefault().getImpl(IPermission.class).canDrawOverlays(mContext);
        if (hasPermission) {
            String message = mContext.getString(R.string.has_permission) + mContext.getString(R.string.overflow);
            Toasty.success(mContext, message, Toast.LENGTH_SHORT).show();
        }
    }

}
