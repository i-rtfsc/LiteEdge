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

package com.journeyOS.plugins.key;

import android.accessibilityservice.AccessibilityService;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;

import com.journeyOS.base.R;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.viewmodel.BaseViewModel;
import com.journeyOS.plugins.key.adapter.KeyInfoData;

import java.util.ArrayList;
import java.util.List;

public class KeyModel extends BaseViewModel {
    private static final String TAG = KeyModel.class.getSimpleName();
    private Context mContext;
    private MutableLiveData<List<KeyInfoData>> mKeyInfoData = new MutableLiveData<>();

    @Override
    protected void onCreate() {
        mContext = CoreManager.getDefault().getContext();
    }

    public void getKeyApps() {
        List<KeyInfoData> infoDatas = new ArrayList<>();

        infoDatas.add(new KeyInfoData(mContext.getDrawable(R.drawable.svg_key_back),
                mContext.getString(R.string.key_back), Integer.toString(AccessibilityService.GLOBAL_ACTION_BACK)));

        infoDatas.add(new KeyInfoData(mContext.getDrawable(R.drawable.svg_key_home),
                mContext.getString(R.string.key_home), Integer.toString(AccessibilityService.GLOBAL_ACTION_HOME)));

        infoDatas.add(new KeyInfoData(mContext.getDrawable(R.drawable.svg_key_recents),
                mContext.getString(R.string.key_recents), Integer.toString(AccessibilityService.GLOBAL_ACTION_RECENTS)));

        infoDatas.add(new KeyInfoData(mContext.getDrawable(R.drawable.svg_key_quick_settings),
                mContext.getString(R.string.key_quick_settings), Integer.toString(AccessibilityService.GLOBAL_ACTION_QUICK_SETTINGS)));

        infoDatas.add(new KeyInfoData(mContext.getDrawable(R.drawable.svg_key_power),
                mContext.getString(R.string.key_power), Integer.toString(AccessibilityService.GLOBAL_ACTION_POWER_DIALOG)));

        mKeyInfoData.postValue(infoDatas);
    }


    public MutableLiveData<List<KeyInfoData>> getAllKeyData() {
        return mKeyInfoData;
    }
}
