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

package com.journeyOS.plugins.barrage.adapter;

import android.view.View;

import com.journeyOS.base.adapter.BaseRecyclerAdapter;
import com.journeyOS.base.adapter.BaseViewHolder;
import com.journeyOS.base.widget.SettingSwitch;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.api.edgeprovider.IAppProvider;
import com.journeyOS.core.api.thread.ICoreExecutors;
import com.journeyOS.core.database.app.App;
import com.journeyOS.plugins.R;
import com.journeyOS.plugins.R2;

import butterknife.BindView;
import butterknife.OnClick;

public class BarrageHolder extends BaseViewHolder<BarrageInfoData> {

    @BindView(R2.id.app_item)
    SettingSwitch mSwitch;

    BarrageInfoData mAppInfoData;

    public BarrageHolder(View itemView, BaseRecyclerAdapter baseRecyclerAdapter) {
        super(itemView, baseRecyclerAdapter);
    }

    @Override
    public void updateItem(BarrageInfoData data, int position) {
        mAppInfoData = data;
        mSwitch.setIcon(data.getDrawable());
        mSwitch.setTitle(data.getAppName());
        mSwitch.setCheck(data.getToogle());
    }

    @Override
    public int getContentViewId() {
        return R.layout.layout_barrage_app_item;
    }


    @OnClick({R2.id.app_item})
    void listenerSwitch() {

        CoreManager.getDefault().getImpl(ICoreExecutors.class).diskIOThread().execute(new Runnable() {
            @Override
            public void run() {
                String packageName = mAppInfoData.getPackageName();
                App app = CoreManager.getDefault().getImpl(IAppProvider.class).getApp(packageName);
//                boolean toggle = !app.barrage;
                boolean toggle = !(app.barrage == 1);
                setChecked(toggle);
                app.barrage = toggle ? 1 : 0;
                CoreManager.getDefault().getImpl(IAppProvider.class).insertOrUpdate(app);
            }
        });
    }

    void setChecked(final boolean checked) {
        CoreManager.getDefault().getImpl(ICoreExecutors.class).mainThread().execute(new Runnable() {
            @Override
            public void run() {
                mSwitch.setCheck(checked);
            }
        });
    }
}

