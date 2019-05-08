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

import com.journeyOS.base.utils.BaseUtils;
import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.base.utils.Singleton;
import com.journeyOS.core.api.edgeprovider.IUserProvider;
import com.journeyOS.core.api.thread.ICoreExecutors;
import com.journeyOS.core.database.user.EdgeUser;
import com.journeyOS.core.database.user.User;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;

public class AccountManager {
    private static final String TAG = AccountManager.class.getSimpleName();
//    private final static boolean DEBUG = "eng".equals(Build.TYPE) || "userdebug".equals(Build.TYPE);

    private static List<OnAccountListener> mListeners = new CopyOnWriteArrayList<OnAccountListener>();

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
                    login(user.userId, user.token);
                }
            }
        });
    }

    public void login(String userId, String token) {
        EdgeUser.loginByAccount(userId, token, new LogInListener<EdgeUser>() {
            @Override
            public void done(final EdgeUser edgeUser, BmobException e) {
                if (e == null) {
                    CoreManager.getDefault().getImpl(ICoreExecutors.class).mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            loginSuccess(edgeUser);
                        }
                    });
                } else {
                    LogUtils.d(TAG, "login error = " + e.getMessage());
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


    public EdgeUser getCurrentUser() {
        EdgeUser edgeUser = BmobUser.getCurrentUser(EdgeUser.class);
        LogUtils.i(TAG, "get current user, is null = " + (edgeUser == null));
        return edgeUser;
    }

    public void loginSuccess(EdgeUser edgeUser) {
        for (OnAccountListener listener : mListeners) {
            listener.onLoginSuccess(edgeUser);
        }
    }

    public void logOutSuccess() {
        for (OnAccountListener listener : mListeners) {
            listener.onLogOutSuccess();
        }
    }

    public void registerAccountChangedListener(OnAccountListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener should not be null");
        }

        if (!mListeners.contains(listener)) {
            mListeners.add(listener);
        }
    }

    public void unregisterAccountChangedListener(OnAccountListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener should not be null");
        }

        if (mListeners.contains(listener)) {
            mListeners.remove(listener);
        }
    }

    public interface OnAccountListener {
        void onLoginSuccess(EdgeUser edgeUser);

        void onLogOutSuccess();
    }
}
