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

package com.journeyOS.plugins.edge;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.res.Configuration;

import com.journeyOS.base.R;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.viewmodel.BaseViewModel;
import com.journeyOS.plugins.edge.adapter.EdgeInfoData;

import java.util.ArrayList;
import java.util.List;

public class EdgeModel extends BaseViewModel {
    private static final String TAG = EdgeModel.class.getSimpleName();
    private Context mContext;
    private MutableLiveData<List<EdgeInfoData>> mInfoData = new MutableLiveData<>();

    @Override
    protected void onCreate() {
        mContext = CoreManager.getDefault().getContext();
    }

    public void getEdges(int rotation) {
        List<EdgeInfoData> infoDatas = new ArrayList<>();

        boolean isPortrait = rotation == Configuration.ORIENTATION_PORTRAIT;
        if (isPortrait) {
            infoDatas.add(new EdgeInfoData(mContext.getDrawable(R.drawable.svg_edge_icon_left),
                    mContext.getString(R.string.left_edge), Integer.toString(1)));

            infoDatas.add(new EdgeInfoData(mContext.getDrawable(R.drawable.svg_edge_icon_right),
                    mContext.getString(R.string.right_edge), Integer.toString(2)));
        } else {
            infoDatas.add(new EdgeInfoData(mContext.getDrawable(R.drawable.svg_edge_icon_up),
                    mContext.getString(R.string.up_edge), Integer.toString(3)));
        }

        mInfoData.postValue(infoDatas);
    }


    public MutableLiveData<List<EdgeInfoData>> getAllEdges() {
        return mInfoData;
    }
}
