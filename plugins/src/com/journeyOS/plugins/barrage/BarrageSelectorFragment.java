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
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.journeyOS.base.adapter.BaseRecyclerAdapter;
import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.core.base.BaseFragment;
import com.journeyOS.core.viewmodel.ModelProvider;
import com.journeyOS.plugins.R;
import com.journeyOS.plugins.R2;
import com.journeyOS.plugins.barrage.adapter.BarrageHolder;
import com.journeyOS.plugins.barrage.adapter.BarrageInfoData;

import java.util.List;

import butterknife.BindView;

public class BarrageSelectorFragment extends BaseFragment {
    private static final String TAG = BarrageSelectorFragment.class.getSimpleName();

    static Activity mContext;

    @BindView(R2.id.apps_recyclerView)
    RecyclerView mAllAppsView;

    private BaseRecyclerAdapter mAllAppsAdapter;

    private BarrageModel mAppModel;

    public static Fragment newInstance(Activity activity) {
        BarrageSelectorFragment fragment = new BarrageSelectorFragment();
        mContext = activity;
        return fragment;
    }

    @Override
    public int attachLayoutRes() {
        return R.layout.fragment_barrage_selector;
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

        mAppModel = ModelProvider.getModel(this, BarrageModel.class);
        mAppModel.getAllBarrages();
        mAppModel.getAllBarrageData().observe(this, new Observer<List<BarrageInfoData>>() {
            @Override
            public void onChanged(@Nullable List<BarrageInfoData> infoData) {
                onAllApps(infoData);
            }
        });

    }

    void onAllApps(final List<BarrageInfoData> infoData) {
        LogUtils.d(TAG, "observer app info data = " + infoData);
        final LinearLayoutManager appLayoutManager = new LinearLayoutManager(mContext);
        mAllAppsView.setLayoutManager(appLayoutManager);
        mAllAppsAdapter = new BaseRecyclerAdapter(mContext);
        mAllAppsAdapter.setData(infoData);
        mAllAppsAdapter.registerHolder(BarrageHolder.class, R.layout.layout_barrage_app_item);
        mAllAppsView.setAdapter(mAllAppsAdapter);
    }

}
