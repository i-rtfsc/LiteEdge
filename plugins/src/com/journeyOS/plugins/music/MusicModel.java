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

package com.journeyOS.plugins.music;

import android.arch.lifecycle.MutableLiveData;
import android.content.Context;

import com.journeyOS.base.R;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.GlobalType;
import com.journeyOS.core.viewmodel.BaseViewModel;
import com.journeyOS.plugins.music.adapter.MusicInfoData;

import java.util.ArrayList;
import java.util.List;

public class MusicModel extends BaseViewModel {
    private static final String TAG = MusicModel.class.getSimpleName();
    private Context mContext;
    private MutableLiveData<List<MusicInfoData>> mInfoData = new MutableLiveData<>();

    @Override
    protected void onCreate() {
        mContext = CoreManager.getDefault().getContext();
    }

    public void getMusicApps() {
        List<MusicInfoData> infoDatas = new ArrayList<>();

        infoDatas.add(new MusicInfoData(mContext.getDrawable(R.drawable.svg_music_previous),
                mContext.getString(R.string.music_last), GlobalType.MUSIC_LAST));

        infoDatas.add(new MusicInfoData(mContext.getDrawable(R.drawable.svg_music_play),
                mContext.getString(R.string.music_play), GlobalType.MUSIC_PLAY));

        infoDatas.add(new MusicInfoData(mContext.getDrawable(R.drawable.svg_music_next),
                mContext.getString(R.string.music_next), GlobalType.MUSIC_NEXT));

        mInfoData.postValue(infoDatas);
    }


    public MutableLiveData<List<MusicInfoData>> getAllMusicData() {
        return mInfoData;
    }
}
