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
import android.support.v4.app.Fragment;

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
        mBarrage.setCheck(barrage);

        if (!barrage) {
            mBarrageSelector.setEnabled(false);
            mBarrageTest.setEnabled(false);
        }
    }

    @OnClick({R2.id.barrage})
    public void listenerBarrage() {
        if (!CoreManager.getDefault().getImpl(IPermission.class).hasListenerNotification(mContext)) {
            CoreManager.getDefault().getImpl(IPermission.class).listenerNotification(mContext);
        }
        boolean barrage = SpUtils.getInstant().getBoolean(Constant.BARRAGE, Constant.BARRAGE_DEFAULT);
        mBarrage.setCheck(!barrage);
        SpUtils.getInstant().put(Constant.BARRAGE, !barrage);

        mBarrageSelector.setEnabled(!barrage);
        mBarrageTest.setEnabled(!barrage);
        mBarrageSelector.setAlpha(!barrage ? 1.0f : 0.5f);
        mBarrageTest.setAlpha(!barrage ? 1.0f : 0.5f);
    }

    @OnClick({R2.id.barrage_selector})
    public void listenerBarrageSelector() {
        CoreManager.getDefault().getImpl(IContainer.class).subActivity(mContext, BarrageSelectorFragment.newInstance(mContext), mContext.getString(R.string.barrage_whitelist));
    }

    @OnClick({R2.id.barrage_test})
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
