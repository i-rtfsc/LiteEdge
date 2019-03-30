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

package com.journeyOS.plugins.app;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.journeyOS.base.adapter.BaseRecyclerAdapter;
import com.journeyOS.base.utils.AppUtils;
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
import com.journeyOS.plugins.app.adapter.AppHolder;
import com.journeyOS.plugins.app.adapter.AppInfoData;

import java.util.List;

import butterknife.BindView;

public class AppSelectorFragment extends BaseFragment implements RouterListener {
    private static final String TAG = AppSelectorFragment.class.getSimpleName();

    @BindView(R2.id.apps_recyclerView)
    RecyclerView mAllAppsView;

    BaseRecyclerAdapter mAllAppsAdapter;

    AppModel mAppModel;

    static Activity mContext;

    static int sRotation;
    static FingerDirection sDirection;

    public static Fragment newInstance(Activity activity, int rotation, FingerDirection direction) {
        AppSelectorFragment fragment = new AppSelectorFragment();
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

        mAppModel = ModelProvider.getModel(this, AppModel.class);
        mAppModel.getAllApps(false);
        mAppModel.getAllAppData().observe(this, new Observer<List<AppInfoData>>() {
            @Override
            public void onChanged(@Nullable List<AppInfoData> appInfoData) {
                onAllApps(appInfoData);
            }
        });

    }

    void onAllApps(final List<AppInfoData> appInfoData) {
        LogUtils.d(TAG, "observer app info data = " + appInfoData);
        LinearLayoutManager appLayoutManager = new LinearLayoutManager(mContext);
        mAllAppsView.setLayoutManager(appLayoutManager);
        mAllAppsAdapter = new BaseRecyclerAdapter(mContext);
        mAllAppsAdapter.setData(appInfoData);
        mAllAppsAdapter.registerHolder(AppHolder.class, R.layout.layout_app_item);
        mAllAppsView.setAdapter(mAllAppsAdapter);
    }

    @Override
    public void onShowMessage(RouterMsssage message) {
        Messages msg = (Messages) message;
        switch (msg.what) {
            case Messages.MSG_ADD_GESTURE_APP:
                String packageName = (String) msg.obj;
                save(packageName);
                break;
        }
    }

    public void save(final String packageName) {
        LogUtils.d(TAG, " save packageName = " + packageName);
        if (packageName == null) {
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
                    gesture.type = GlobalType.APP;
                    gesture.action = packageName;
                    gesture.comment = AppUtils.getAppName(mContext, packageName);
                    LogUtils.d(TAG, " save gesture = " + gesture.toString());
                    CoreManager.getDefault().getImpl(IGestureProvider.class).insertOrUpdateConfig(gesture);
                    mContext.finish();
                }
            }
        });
    }

}
