/*
 * Copyright (c) 2018 anqi.huang@outlook.com.
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

package com.journeyOS.plugins;

import android.arch.lifecycle.Observer;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.journeyOS.base.adapter.BaseRecyclerAdapter;
import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.base.utils.UIUtils;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.Messages;
import com.journeyOS.core.api.edge.IEdge;
import com.journeyOS.core.api.edgeprovider.IEdgeProvider;
import com.journeyOS.core.api.thread.ICoreExecutors;
import com.journeyOS.core.base.BaseActivity;
import com.journeyOS.core.database.edge.Edge;
import com.journeyOS.core.type.EdgeDirection;
import com.journeyOS.core.viewmodel.ModelProvider;
import com.journeyOS.literouter.RouterListener;
import com.journeyOS.literouter.RouterMsssage;
import com.journeyOS.plugins.adapter.AppHolder;
import com.journeyOS.plugins.adapter.AppInfoData;

import java.util.List;

import butterknife.BindView;

public class SelectorActivity extends BaseActivity implements RouterListener {
    private static final String TAG = SelectorActivity.class.getSimpleName();

    private static final String EXTRA_POSTION = "extra_postion";
    private static final String EXTRA_DIRECTION = "extra_direction";

    private static int sPostion;
    private static EdgeDirection sDirection;

    @BindView(R2.id.toolbar)
    Toolbar mToolbar;
    @BindView(R2.id.apps_recyclerView)
    RecyclerView mAllAppsView;

    private BaseRecyclerAdapter mAllAppsAdapter;

    private AppModel mAppModel;

    public static void navigationActivity(Context from, int postion, EdgeDirection direction) {
        try {
            Intent intent = new Intent(from, SelectorActivity.class);
            intent.putExtra(EXTRA_POSTION, postion);
            intent.putExtra(EXTRA_DIRECTION, direction);
            from.startActivity(intent);
            CoreManager.getDefault().getImpl(IEdge.class).hidingEdge(true);
        } catch (ActivityNotFoundException e) {
            LogUtils.d(TAG, e);
        }

    }

    public static void navigationFromApplication(Context from, int postion, EdgeDirection direction) {
        Intent intent = new Intent(from, SelectorActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(EXTRA_POSTION, postion);
        intent.putExtra(EXTRA_DIRECTION, direction);
        from.startActivity(intent);
        CoreManager.getDefault().getImpl(IEdge.class).hidingEdge(true);
    }

    public void save(final String packageName) {
        LogUtils.d(TAG, " save packageName = " + packageName);
        if (packageName == null) {
            finishActivity();
            return;
        }

        CoreManager.getDefault().getImpl(ICoreExecutors.class).diskIOThread().execute(new Runnable() {
            @Override
            public void run() {
                if (sDirection != null || sPostion != -1) {
                    String item = CoreManager.getDefault().getImpl(IEdgeProvider.class).encodeItem(sDirection, sPostion);
                    Edge config = new Edge();
                    config.item = item;
                    config.direction = sDirection.name().toLowerCase();
                    config.packageName = packageName;
                    CoreManager.getDefault().getImpl(IEdgeProvider.class).insertOrUpdateConfig(config);
                    finishActivity();
                }
            }
        });
    }

    private void finishActivity() {
        this.finish();
        CoreManager.getDefault().getImpl(IEdge.class).showingEdge(sDirection);
    }


    @Override
    public int attachLayoutRes() {
        return R.layout.activity_selector;
    }

    @Override
    public void initViews() {
        UIUtils.setStatusBarColor(this, this.getResources().getColor(R.color.lightskyblue));
//        mToolbar.setTitle(R.string.tool_bar_title);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sPostion = getIntent().getIntExtra(EXTRA_POSTION, -1);
        sDirection = (EdgeDirection) getIntent().getSerializableExtra(EXTRA_DIRECTION);
    }

    @Override
    protected void initDataObserver(Bundle savedInstanceState) {
        super.initDataObserver(savedInstanceState);
        LogUtils.d(TAG, "data observer has been called!");
        mAppModel = ModelProvider.getModel(this, AppModel.class);
        mAppModel.getAllApps();
        mAppModel.getAllAppData().observe(this, new Observer<List<AppInfoData>>() {
            @Override
            public void onChanged(@Nullable List<AppInfoData> appInfoData) {
                onAllApps(appInfoData);
            }
        });
    }

    void onAllApps(final List<AppInfoData> appInfoData) {
        LogUtils.d(TAG, "observer app info data = " + appInfoData);
        LinearLayoutManager appLayoutManager = new LinearLayoutManager(this);
        mAllAppsView.setLayoutManager(appLayoutManager);
        mAllAppsAdapter = new BaseRecyclerAdapter(this);
        mAllAppsAdapter.setData(appInfoData);
        mAllAppsAdapter.registerHolder(AppHolder.class, R.layout.layout_app_item);
        mAllAppsView.setAdapter(mAllAppsAdapter);
    }

    @Override
    public void onShowMessage(RouterMsssage message) {
        Messages msg = (Messages) message;
        switch (msg.what) {
            case Messages.MSG_ADD_ITEM:
                String packageName = (String) msg.obj;
                save(packageName);
                break;
        }
    }
}
