/*
 * Copyright (c) 2018 anqi.huang@outlook.com
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

package com.journeyOS.core.database.ball;

import com.journeyOS.core.CoreManager;
import com.journeyOS.core.database.DBConfigs;
import com.journeyOS.core.api.edgeprovider.IBallProvider;
import com.journeyOS.core.database.EdgeDatabase;
import com.journeyOS.core.repository.DBHelper;
import com.journeyOS.literouter.annotation.ARouterInject;

@ARouterInject(api = IBallProvider.class)
public class BallRepositoryImpl implements IBallProvider {
    private static final String TAG = BallRepositoryImpl.class.getSimpleName();
    private BallDao ballDao;
    private Object mLock = new Object();

    @Override
    public void onCreate() {
        EdgeDatabase database = DBHelper.provider(CoreManager.getDefault().getContext(), EdgeDatabase.class, DBConfigs.DB_NAME);
        ballDao = database.ballDao();
    }

    @Override
    public Ball getConfig(int orientation) {
        synchronized (mLock) {
            return ballDao.searchConfig(orientation);
        }
    }

    @Override
    public void insertOrUpdateConfig(Ball config) {
        synchronized (mLock) {
            ballDao.insert(config);
        }
    }

    @Override
    public void deleteConfig(Ball config) {
        synchronized (mLock) {
            ballDao.delete(config);
        }
    }

    @Override
    public void deleteAll() {
        synchronized (mLock) {
            ballDao.deleteAll();
        }
    }
}
