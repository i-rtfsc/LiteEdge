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

package com.journeyOS.core;

import android.os.Build;

import com.journeyOS.base.utils.BaseUtils;
import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.base.utils.Singleton;
import com.journeyOS.core.api.edgeprovider.IUserProvider;
import com.journeyOS.core.api.thread.ICoreExecutors;
import com.journeyOS.core.database.user.EdgeUser;
import com.journeyOS.core.database.user.User;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

public class AccountManager {
    private static final String TAG = AccountManager.class.getSimpleName();
    private final static boolean DEBUG = "eng".equals(Build.TYPE) || "userdebug".equals(Build.TYPE);

    private AccountManager() {
    }

    private static final Singleton<AccountManager> gDefault = new Singleton<AccountManager>() {
        @Override
        protected AccountManager create() {
            return new AccountManager();
        }
    };

    public static AccountManager getDefault() {
        return gDefault.get();
    }


    public void login() {
        CoreManager.getDefault().getImpl(ICoreExecutors.class).diskIOThread().execute(new Runnable() {
            @Override
            public void run() {
                User user = CoreManager.getDefault().getImpl(IUserProvider.class).getCurrentAccount();
                if (!BaseUtils.isNull(user)) {
                    EdgeUser.loginByAccount(user.userId, user.token, new LogInListener<User>() {
                        @Override
                        public void done(User user, BmobException e) {
                            if (e == null) {
                                LogUtils.d(TAG, "login success");
                            } else {
                                LogUtils.d(TAG, "login error = " + e.getMessage());
                            }
                        }
                    });
                }
            }
        });
    }

    public void logOut() {
        EdgeUser.logOut();
    }

    public boolean isLogin() {
        return EdgeUser.isLogin();
    }

    //just for debug
    public void save2Db(final String userId, final String password) {
        if (!DEBUG) {
            LogUtils.e(TAG, "can't save user password!!!");
            return;
        }
        CoreManager.getDefault().getImpl(ICoreExecutors.class).diskIOThread().execute(new Runnable() {
            @Override
            public void run() {
                User user = new User();
                user.userId = userId;
                user.userName = userId;
                user.token = password;
                CoreManager.getDefault().getImpl(IUserProvider.class).insertOrUpdateUser(user);
            }
        });
    }
}
