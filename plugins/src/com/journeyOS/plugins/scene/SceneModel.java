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

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.journeyOS.base.utils.AppUtils;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.api.thread.ICoreExecutors;
import com.journeyOS.core.viewmodel.BaseViewModel;
import com.journeyOS.i007Service.I007Manager;
import com.journeyOS.liteprovider.utils.LogUtils;
import com.journeyOS.plugins.scene.adapter.SceneInfoData;

import java.util.ArrayList;
import java.util.List;

public class SceneModel extends BaseViewModel {
    private static final String TAG = SceneModel.class.getSimpleName();
    private Context mContext;
    private List<String> mLauncherApps = new ArrayList<>();

    private MutableLiveData<List<SceneInfoData>> mSceneInfoData = new MutableLiveData<>();

    @Override
    protected void onCreate() {
        mContext = CoreManager.getDefault().getContext();
        List<String> apps = AppUtils.getLauncherApp(mContext);
        mLauncherApps.addAll(apps);
    }

    public void getSceneApps(final int scene) {
        LogUtils.d(TAG, "get game apps");
        CoreManager.getDefault().getImpl(ICoreExecutors.class).diskIOThread().execute(new Runnable() {
            @Override
            public void run() {
                /**
                 * I007中游戏列表很多，所以我们从所以按照的应用里遍历
                 */
                LogUtils.d(TAG, "all apps = " + mLauncherApps.size());
                List<SceneInfoData> infoDatas = new ArrayList<>();
                for (String app : mLauncherApps) {
                    String packageName = app;
                    if (SceneFragment.SCENE_GAME == scene) {
                        if (I007Manager.isGame(packageName)) {
                            Drawable drawable = AppUtils.getAppIcon(mContext, packageName);
                            String appName = AppUtils.getAppName(mContext, packageName);
                            SceneInfoData infoData = new SceneInfoData();
                            infoData.packageName = packageName;
                            infoData.appName = appName;
                            infoData.drawable = drawable;
                            infoData.scene = scene;
                            infoDatas.add(infoData);
                        }
                    } else if (SceneFragment.SCENE_VIDEO == scene) {
                        if (I007Manager.isVideo(packageName)) {
                            Drawable drawable = AppUtils.getAppIcon(mContext, packageName);
                            String appName = AppUtils.getAppName(mContext, packageName);
                            SceneInfoData infoData = new SceneInfoData();
                            infoData.packageName = packageName;
                            infoData.appName = appName;
                            infoData.drawable = drawable;
                            infoData.scene = scene;
                            infoDatas.add(infoData);
                        }
                    }
                }
                mSceneInfoData.postValue(infoDatas);
            }
        });
    }

    public MutableLiveData<List<SceneInfoData>> getSceneData() {
        return mSceneInfoData;
    }
}
