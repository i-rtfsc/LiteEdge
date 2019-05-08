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

package com.journeyOS.core.database.music;

import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.api.edgeprovider.IMusicProvider;
import com.journeyOS.core.database.DBConfigs;
import com.journeyOS.core.database.DBHelper;
import com.journeyOS.core.database.EdgeDatabase;
import com.journeyOS.literouter.annotation.ARouterInject;

@Deprecated
@ARouterInject(api = IMusicProvider.class)
public class MusicRepositoryImpl implements IMusicProvider {
    private static final String TAG = MusicRepositoryImpl.class.getSimpleName();
    private MusicDao musicDao;
    private Object mLock = new Object();

    @Override
    public void onCreate() {
        EdgeDatabase database = DBHelper.provider(CoreManager.getDefault().getContext(), EdgeDatabase.class, DBConfigs.DB_NAME);
        musicDao = database.musicDao();
    }


    @Override
    public Music getConfig(String packageName) {
        synchronized (mLock) {
            return musicDao.getMusic(packageName);
        }
    }

    @Override
    public void insertOrUpdateMusic(Music music) {
        LogUtils.d(TAG, "insert or update music = [" + music.config + "]");
        synchronized (mLock) {
            musicDao.insert(music);
        }
    }

    @Override
    public void deleteMusic(Music music) {
        synchronized (mLock) {
            musicDao.delete(music);
        }
    }

    @Override
    public void deleteAll() {
        synchronized (mLock) {
            musicDao.deleteAll();
        }
    }
}
