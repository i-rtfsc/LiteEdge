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
import android.graphics.drawable.Drawable;

import com.journeyOS.base.utils.AppUtils;
import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.api.thread.ICoreExecutors;
import com.journeyOS.core.viewmodel.BaseViewModel;
import com.journeyOS.plugins.app.adapter.AppInfoData;

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
     * @param edge 添加到edge还是添加到ball手势
     *             true: 添加到edge
     *             false: ball手势
     */
    public void getAllApps(final boolean edge) {
        CoreManager.getDefault().getImpl(ICoreExecutors.class).diskIOThread().execute(new Runnable() {
            @Override
            public void run() {
                List<String> apps = AppUtils.getLauncherApp(mContext);
                LogUtils.d(TAG, "all apps = " + apps.size());
                List<AppInfoData> appInfoDatas = new ArrayList<>();
                for (String app : apps) {
                    String packageName = app;
                    Boolean toggle = edge;
                    Drawable drawable = AppUtils.getAppIcon(mContext, packageName);
                    String label = AppUtils.getAppName(mContext, packageName);
                    AppInfoData appInfoData = new AppInfoData(drawable, label, packageName, toggle);
                    appInfoDatas.add(appInfoData);
                }
                mAppInfoData.postValue(appInfoDatas);
            }
        });


    }

    public MutableLiveData<List<AppInfoData>> getAllAppData() {
        return mAppInfoData;
    }
}
