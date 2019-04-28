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

package com.journeyOS.plugins.scene;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.journeyOS.base.adapter.BaseRecyclerAdapter;
import com.journeyOS.base.utils.AppUtils;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.Messages;
import com.journeyOS.core.api.plugins.IPlugins;
import com.journeyOS.core.base.BaseFragment;
import com.journeyOS.core.viewmodel.ModelProvider;
import com.journeyOS.i007Service.I007Manager;
import com.journeyOS.literouter.RouterListener;
import com.journeyOS.literouter.RouterMsssage;
import com.journeyOS.plugins.R;
import com.journeyOS.plugins.R2;
import com.journeyOS.plugins.scene.adapter.SceneHolder;
import com.journeyOS.plugins.scene.adapter.SceneInfoData;

import java.util.List;

import butterknife.BindView;

public class SceneFragment extends BaseFragment implements RouterListener {
    private static final String TAG = SceneFragment.class.getSimpleName();

    public static final int SCENE_GAME = 0x01;
    public static final int SCENE_VIDEO = 0x02;
    static int sScene;

    @BindView(R2.id.apps_recyclerView)
    RecyclerView mAllAppsView;

    BaseRecyclerAdapter mAllAppsAdapter;

    SceneModel mModel;

    static Activity mContext;

    public static Fragment newInstance(Activity activity, int scene) {
        SceneFragment fragment = new SceneFragment();
        sScene = scene;
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
        mContext = getActivity();
    }

    @Override
    protected void initDataObserver(Bundle savedInstanceState) {
        super.initDataObserver(savedInstanceState);

        mModel = ModelProvider.getModel(this, SceneModel.class);
        mModel.getSceneApps(sScene);
        mModel.getSceneData().observe(this, new Observer<List<SceneInfoData>>() {
            @Override
            public void onChanged(@Nullable List<SceneInfoData> sceneInfoData) {
                onAllApps(sceneInfoData);
            }
        });
    }

    @Override
    public void onFragmentResume() {
        super.onFragmentResume();

        if (mModel == null) {
            mModel = ModelProvider.getModel(this, SceneModel.class);
        }
        mModel.getSceneApps(sScene);
    }

    @Override
    public void onMenuAdd() {
        super.onMenuAdd();
        CoreManager.getDefault().getImpl(IPlugins.class).navigationSceneSelector(mContext, sScene);
    }


    @Override
    public void onShowMessage(RouterMsssage message) {
        Messages msg = (Messages) message;
        switch (msg.what) {
            case Messages.MSG_SCENE_REMOVE_GAME:
                showRemoveDialog((String) msg.obj, SCENE_GAME);
                break;
            case Messages.MSG_SCENE_REMOVE_VIDEO:
                showRemoveDialog((String) msg.obj, SCENE_VIDEO);
                break;
        }
    }

    void onAllApps(final List<SceneInfoData> infoData) {
        LinearLayoutManager appLayoutManager = new LinearLayoutManager(mContext);
        mAllAppsView.setLayoutManager(appLayoutManager);
        mAllAppsAdapter = new BaseRecyclerAdapter(mContext);
        mAllAppsAdapter.setData(infoData);
        mAllAppsAdapter.registerHolder(SceneHolder.class, R.layout.layout_app_item);
        mAllAppsView.setAdapter(mAllAppsAdapter);
    }

    void showRemoveDialog(final String packageName, final int scene) {
        final AlertDialog dialog = new AlertDialog.Builder(mContext, com.journeyOS.core.R.style.CornersAlertDialog)
                .setTitle(getString(R.string.remove_app_title, AppUtils.getAppName(mContext, packageName)))
                .setMessage(R.string.remove_app_message)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (SCENE_GAME == scene) {
                            I007Manager.removeGame(mContext.getPackageName(), packageName);
                        } else if (SCENE_VIDEO == scene) {
                            I007Manager.removeVideo(mContext.getPackageName(), packageName);
                        }
                        mModel.getSceneApps(scene);
                    }
                })
                .create();
        dialog.show();
    }
}
