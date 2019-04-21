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

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.journeyOS.base.utils.AppUtils;
import com.journeyOS.base.utils.BaseUtils;
import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.api.edgeprovider.IAppProvider;
import com.journeyOS.core.api.thread.ICoreExecutors;
import com.journeyOS.core.database.app.App;
import com.journeyOS.core.viewmodel.BaseViewModel;
import com.journeyOS.plugins.app.AppModel;
import com.journeyOS.plugins.barrage.adapter.BarrageInfoData;

import java.util.ArrayList;
import java.util.List;

public class BarrageModel extends BaseViewModel {
    private static final String TAG = AppModel.class.getSimpleName();
    private Context mContext;
    private MutableLiveData<List<BarrageInfoData>> mBarrageInfoData = new MutableLiveData<>();

    @Override
    protected void onCreate() {
        mContext = CoreManager.getDefault().getContext();
    }

    void getAllBarrages() {
        CoreManager.getDefault().getImpl(ICoreExecutors.class).diskIOThread().execute(new Runnable() {
            @Override
            public void run() {
                List<App> allApps = CoreManager.getDefault().getImpl(IAppProvider.class).getApps();
                LogUtils.d(TAG, "search apps result = " + allApps.size());
                if (!BaseUtils.isNull(allApps)) {
                    List<BarrageInfoData> infoDatas = new ArrayList<>();
                    for (App app : allApps) {
                        String packageName = app.packageName;
                        Boolean toggle = (app.barrage == 1);
                        String label = app.appName;
                        Drawable drawable = AppUtils.getAppIcon(mContext, packageName);
                        if (drawable != null) {
                            BarrageInfoData infoData = new BarrageInfoData(drawable, label, packageName, toggle, "");
                            infoDatas.add(infoData);
                        } else {
                            CoreManager.getDefault().getImpl(IAppProvider.class).delete(app);
                        }
                    }
                    mBarrageInfoData.postValue(infoDatas);
                }
            }
        });
    }

    MutableLiveData<List<BarrageInfoData>> getAllBarrageData() {
        return mBarrageInfoData;
    }
}
