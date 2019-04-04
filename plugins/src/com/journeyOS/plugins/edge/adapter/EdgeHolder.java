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

package com.journeyOS.plugins.edge.adapter;

import android.view.View;

import com.journeyOS.base.adapter.BaseRecyclerAdapter;
import com.journeyOS.base.adapter.BaseViewHolder;
import com.journeyOS.base.widget.SettingView;
import com.journeyOS.core.Messages;
import com.journeyOS.literouter.Router;
import com.journeyOS.plugins.R;
import com.journeyOS.plugins.R2;

import butterknife.BindView;
import butterknife.OnClick;

public class EdgeHolder extends BaseViewHolder<EdgeInfoData> {

    @BindView(R2.id.app_item)
    SettingView mView;

    EdgeInfoData mInfoData;

    public EdgeHolder(View itemView, BaseRecyclerAdapter baseRecyclerAdapter) {
        super(itemView, baseRecyclerAdapter);
    }

    @Override
    public void updateItem(EdgeInfoData data, int position) {
        mInfoData = data;
        mView.setIcon(data.getDrawable());
        mView.setTitle(data.getAppName());
    }

    @Override
    public int getContentViewId() {
        return R.layout.layout_app_item;
    }


    @OnClick({R2.id.app_item})
    void listenerSwitch() {
        Messages msg = new Messages();
        msg.what = Messages.MSG_ADD_GESTURE_EDGE;
        msg.obj = mInfoData;
        Router.getDefault().post(msg);
    }

}
