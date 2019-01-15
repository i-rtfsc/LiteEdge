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

package com.journeyOS.core.database.user;

import com.journeyOS.core.CoreManager;
import com.journeyOS.core.api.edgeprovider.IUserProvider;
import com.journeyOS.core.database.DBConfigs;
import com.journeyOS.core.database.DBHelper;
import com.journeyOS.core.database.EdgeDatabase;
import com.journeyOS.literouter.annotation.ARouterInject;

@ARouterInject(api = IUserProvider.class)
public class UserRepositoryImpl implements IUserProvider {
    private static final String TAG = UserRepositoryImpl.class.getSimpleName();
    private UserDao userDao;
    private Object mLock = new Object();

    @Override
    public void onCreate() {
        EdgeDatabase database = DBHelper.provider(CoreManager.getDefault().getContext(), EdgeDatabase.class, DBConfigs.DB_NAME);
        userDao = database.userDao();
    }


    @Override
    public User getCurrentAccount() {
        synchronized (mLock) {
            return userDao.getCurrentUser();
        }
    }

    @Override
    public User getConfig(String userId) {
        synchronized (mLock) {
            return userDao.getUser(userId);
        }
    }

    @Override
    public void insertOrUpdateUser(User user) {
        synchronized (mLock) {
            userDao.insert(user);
        }
    }

    @Override
    public void deleteUser(User user) {
        synchronized (mLock) {
            userDao.delete(user);
        }
    }

    @Override
    public void deleteAll() {
        synchronized (mLock) {
            userDao.deleteAll();
        }
    }
}
