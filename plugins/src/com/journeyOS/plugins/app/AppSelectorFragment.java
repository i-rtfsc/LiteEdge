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
import android.content.Context;
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
import com.journeyOS.core.api.edge.IEdge;
import com.journeyOS.core.api.edgeprovider.IEdgeProvider;
import com.journeyOS.core.api.edgeprovider.IGestureProvider;
import com.journeyOS.core.api.thread.ICoreExecutors;
import com.journeyOS.core.base.BaseFragment;
import com.journeyOS.core.database.edge.Edge;
import com.journeyOS.core.database.gesture.Gesture;
import com.journeyOS.core.type.EdgeDirection;
import com.journeyOS.core.type.FingerDirection;
import com.journeyOS.core.viewmodel.ModelProvider;
import com.journeyOS.i007Service.I007Manager;
import com.journeyOS.literouter.RouterListener;
import com.journeyOS.literouter.RouterMsssage;
import com.journeyOS.plugins.R;
import com.journeyOS.plugins.R2;
import com.journeyOS.plugins.app.adapter.AppHolder;
import com.journeyOS.plugins.app.adapter.AppInfoData;
import com.journeyOS.plugins.scene.SceneFragment;

import java.util.List;

import butterknife.BindView;

public class AppSelectorFragment extends BaseFragment implements RouterListener {
    private static final String TAG = AppSelectorFragment.class.getSimpleName();

    @BindView(R2.id.apps_recyclerView)
    RecyclerView mAllAppsView;

    BaseRecyclerAdapter mAdapter;

    AppModel mAppModel;

    static Context mContext;
    static Activity mActivity;

    /**
     * 添加到Edge/scene等哪个场景
     */
    static int sFrom;
    public static final int FROM_EDGE = 0x01;
    public static final int FROM_GESTURE = 0x02;
    public static final int FROM_SCENE = 0x04;

    static FingerDirection sFingerDirection;
    static int sRotation;

    static EdgeDirection sEdgeDirection;
    static int sPostion;

    static int sScene = -1;

    public static Fragment newInstanceEdge(Context context, int postion, EdgeDirection direction) {
        AppSelectorFragment fragment = new AppSelectorFragment();
        sFrom = FROM_EDGE;
        sPostion = postion;
        sEdgeDirection = direction;
        mContext = context;
        return fragment;
    }

    public static Fragment newInstanceGesture(Context context, int rotation, FingerDirection direction) {
        AppSelectorFragment fragment = new AppSelectorFragment();
        sFrom = FROM_GESTURE;
        sRotation = rotation;
        sFingerDirection = direction;
        mContext = context;
        return fragment;
    }

    public static Fragment newInstanceScene(Context context, int scene) {
        AppSelectorFragment fragment = new AppSelectorFragment();
        sFrom = FROM_SCENE;
        sScene = scene;
        mContext = context;
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
        mActivity = getActivity();
    }

    @Override
    protected void initDataObserver(Bundle savedInstanceState) {
        super.initDataObserver(savedInstanceState);

        mAppModel = ModelProvider.getModel(this, AppModel.class);
        mAppModel.getAllApps(sFrom, sScene);
        mAppModel.getAllAppData().observe(this, new Observer<List<AppInfoData>>() {
            @Override
            public void onChanged(@Nullable List<AppInfoData> appInfoData) {
                onAllApps(appInfoData);
            }
        });

    }

    void onAllApps(final List<AppInfoData> appInfoData) {
        LinearLayoutManager appLayoutManager = new LinearLayoutManager(mContext);
        mAllAppsView.setLayoutManager(appLayoutManager);
        mAdapter = new BaseRecyclerAdapter(mContext);
        mAdapter.setData(appInfoData);
        mAdapter.registerHolder(AppHolder.class, R.layout.layout_app_item);
        mAllAppsView.setAdapter(mAdapter);
    }

    @Override
    public void onShowMessage(RouterMsssage message) {
        Messages msg = (Messages) message;
        switch (msg.what) {
            case Messages.MSG_ADD_ITEM:
                save2Edge((String) msg.obj);
                break;
            case Messages.MSG_ADD_GESTURE_APP:
                save2Gesture((String) msg.obj);
                break;
            case Messages.MSG_SCENE_SELECTOR_APP:
                save2Scene((String) msg.obj, msg.arg1);
                break;
        }
    }

    void save2Gesture(final String packageName) {
        LogUtils.d(TAG, " save packageName = " + packageName);
        if (packageName == null) {
            mActivity.finish();
            return;
        }

        CoreManager.getDefault().getImpl(ICoreExecutors.class).diskIOThread().execute(new Runnable() {
            @Override
            public void run() {
                if (sFingerDirection != null || sRotation != -1) {
                    String item = CoreManager.getDefault().getImpl(IGestureProvider.class).encodeItem(sFingerDirection, sRotation);
                    Gesture gesture = new Gesture();
                    gesture.gestureDirection = item;
                    gesture.orientation = sRotation;
                    gesture.type = GlobalType.APP;
                    gesture.action = packageName;
                    gesture.comment = AppUtils.getAppName(mContext, packageName);
                    LogUtils.d(TAG, " save gesture = " + gesture.toString());
                    CoreManager.getDefault().getImpl(IGestureProvider.class).insertOrUpdateConfig(gesture);
                    mActivity.finish();
                }
            }
        });
    }

    void save2Edge(final String packageName) {
        LogUtils.d(TAG, " save packageName = " + packageName);
        if (packageName == null) {
            mActivity.finish();
            return;
        }

        CoreManager.getDefault().getImpl(ICoreExecutors.class).diskIOThread().execute(new Runnable() {
            @Override
            public void run() {
                if (sEdgeDirection != null || sPostion != -1) {
                    String item = CoreManager.getDefault().getImpl(IEdgeProvider.class).encodeItem(sEdgeDirection, sPostion);
                    Edge config = new Edge();
                    config.item = item;
                    config.direction = sEdgeDirection.name().toLowerCase();
                    config.packageName = packageName;
                    CoreManager.getDefault().getImpl(IEdgeProvider.class).insertOrUpdateConfig(config);
                    mActivity.finish();
                    CoreManager.getDefault().getImpl(IEdge.class).showingEdge(sEdgeDirection);
                }
            }
        });
    }

    void save2Scene(final String packageName, int scene) {
        LogUtils.d(TAG, "save scene, packageName = [" + packageName + "], scene = [" + scene + "]");
        if (packageName == null) {
            mActivity.finish();
            return;
        }
        if (SceneFragment.SCENE_GAME == scene) {
            I007Manager.addGame(mContext.getPackageName(), packageName);
        } else if (SceneFragment.SCENE_VIDEO == scene) {
            I007Manager.addVideo(mContext.getPackageName(), packageName);
        }
        mActivity.finish();
    }
}
