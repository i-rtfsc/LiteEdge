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

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;

import com.journeyOS.base.utils.AppUtils;
import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.api.thread.ICoreExecutors;
import com.journeyOS.core.viewmodel.BaseViewModel;
import com.journeyOS.i007Service.I007Manager;
import com.journeyOS.plugins.app.adapter.AppInfoData;
import com.journeyOS.plugins.scene.SceneFragment;

import java.util.ArrayList;
import java.util.List;

public class AppModel extends BaseViewModel {
    private static final String TAG = AppModel.class.getSimpleName();
    private Context mContext;
    private MutableLiveData<List<AppInfoData>> mAppInfoData = new MutableLiveData<>();

    @Override
    protected void onCreate() {
        mContext = CoreManager.getDefault().getContext();
    }

    /**
     * @param from 添加到edge、ball手势、还是场景等。
     */
    public void getAllApps(final int from, final int scene) {
        CoreManager.getDefault().getImpl(ICoreExecutors.class).diskIOThread().execute(new Runnable() {
            @Override
            public void run() {
                List<String> apps = AppUtils.getLauncherApp(mContext);
                LogUtils.d(TAG, "all apps = " + apps.size());
                List<AppInfoData> appInfoDatas = new ArrayList<>();
                for (String app : apps) {
                    AppInfoData appInfoData = new AppInfoData();
                    String packageName = app;
                    appInfoData.packageName = packageName;
                    appInfoData.drawable = AppUtils.getAppIcon(mContext, packageName);
                    appInfoData.appName = AppUtils.getAppName(mContext, packageName);
                    appInfoData.from = from;
                    appInfoData.scene = scene;

                    if (AppSelectorFragment.FROM_SCENE == from) {
                        if (SceneFragment.SCENE_GAME == scene) {
                            boolean isGame = I007Manager.isGame(packageName);
                            if (!isGame) {
                                appInfoDatas.add(appInfoData);
                            }
                        } else if (SceneFragment.SCENE_VIDEO == scene) {
                            boolean isVideo = I007Manager.isVideo(packageName);
                            if (!isVideo) {
                                appInfoDatas.add(appInfoData);
                            }
                        }
                    } else {
                        appInfoDatas.add(appInfoData);
                    }
                }
                mAppInfoData.postValue(appInfoDatas);
            }
        });


    }

    public MutableLiveData<List<AppInfoData>> getAllAppData() {
        return mAppInfoData;
    }
}
