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

package com.journeyOS.plugins.pay;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.journeyOS.base.adapter.BaseRecyclerAdapter;
import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.GlobalType;
import com.journeyOS.core.Messages;
import com.journeyOS.core.api.edgeprovider.IGestureProvider;
import com.journeyOS.core.api.thread.ICoreExecutors;
import com.journeyOS.core.base.BaseFragment;
import com.journeyOS.core.database.gesture.Gesture;
import com.journeyOS.core.type.FingerDirection;
import com.journeyOS.core.viewmodel.ModelProvider;
import com.journeyOS.literouter.RouterListener;
import com.journeyOS.literouter.RouterMsssage;
import com.journeyOS.plugins.R;
import com.journeyOS.plugins.R2;
import com.journeyOS.plugins.pay.adapter.PayHolder;
import com.journeyOS.plugins.pay.adapter.PayInfoData;

import java.util.List;

import butterknife.BindView;

public class PaySelectorFragment extends BaseFragment implements RouterListener {
    private static final String TAG = PaySelectorFragment.class.getSimpleName();

    @BindView(R2.id.apps_recyclerView)
    RecyclerView mAllAppsView;

    BaseRecyclerAdapter mAllAppsAdapter;

    PayModel mPayModel;

    static Activity mContext;

    static int sRotation;
    static FingerDirection sDirection;

    public static Fragment newInstance(Activity activity, int rotation, FingerDirection direction) {
        PaySelectorFragment fragment = new PaySelectorFragment();
        sRotation = rotation;
        sDirection = direction;
        mContext = activity;
        return fragment;
    }

    @Override
    public int attachLayoutRes() {
        return R.layout.fragment_app_selector;
    }

    @Override
    public void initBeforeView() {
        super.initBeforeView();
    }

    @Override
    public void initViews() {

    }

    @Override
    protected void initDataObserver(Bundle savedInstanceState) {
        super.initDataObserver(savedInstanceState);
        mPayModel = ModelProvider.getModel(this, PayModel.class);
        mPayModel.getPayApps();

        mPayModel.getAllPayData().observe(this, new Observer<List<PayInfoData>>() {
            @Override
            public void onChanged(@Nullable List<PayInfoData> infoDatas) {
                onAllApps(infoDatas);
            }
        });

    }

    void onAllApps(final List<PayInfoData> appInfoData) {
        LinearLayoutManager appLayoutManager = new LinearLayoutManager(mContext);
        mAllAppsView.setLayoutManager(appLayoutManager);
        mAllAppsAdapter = new BaseRecyclerAdapter(mContext);
        mAllAppsAdapter.setData(appInfoData);
        mAllAppsAdapter.registerHolder(PayHolder.class, R.layout.layout_app_item);
        mAllAppsView.setAdapter(mAllAppsAdapter);
    }

    @Override
    public void onShowMessage(RouterMsssage message) {
        Messages msg = (Messages) message;
        switch (msg.what) {
            case Messages.MSG_ADD_GESTURE_PAY:
                PayInfoData infoData = (PayInfoData) msg.obj;
                save(infoData);
                break;
        }
    }

    public void save(final PayInfoData infoData) {
        LogUtils.d(TAG, " save packageName = " + infoData);
        if (infoData == null) {
            mContext.finish();
            return;
        }

        CoreManager.getDefault().getImpl(ICoreExecutors.class).diskIOThread().execute(new Runnable() {
            @Override
            public void run() {
                if (sDirection != null || sRotation != -1) {
                    String item = CoreManager.getDefault().getImpl(IGestureProvider.class).encodeItem(sDirection, sRotation);
                    Gesture gesture = new Gesture();
                    gesture.gestureDirection = item;
                    gesture.orientation = sRotation;
                    gesture.type = GlobalType.PAY;
                    gesture.action = infoData.getPackageName();
                    gesture.comment = infoData.getAppName();
                    CoreManager.getDefault().getImpl(IGestureProvider.class).insertOrUpdateConfig(gesture);
                    mContext.finish();
                }
            }
        });
    }

}
