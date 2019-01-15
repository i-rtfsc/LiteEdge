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

package com.journeyOS.plugins.user;

import android.arch.lifecycle.MutableLiveData;

import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.core.StatusDataResource;
import com.journeyOS.core.database.user.EdgeUser;
import com.journeyOS.core.viewmodel.BaseViewModel;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FetchUserInfoListener;

public class LoginModel extends BaseViewModel {
    static final String TAG = LoginModel.class.getSimpleName();

    MutableLiveData<StatusDataResource> mUserInfo = new MutableLiveData<>();

    protected MutableLiveData<StatusDataResource> getUserInfo() {
        return mUserInfo;
    }


    @Override
    protected void onCreate() {

    }

    public void fetchUserInfo() {
        EdgeUser.fetchUserInfo(new FetchUserInfoListener<BmobUser>() {
            @Override
            public void done(BmobUser user, BmobException e) {
                if (e == null) {
                    EdgeUser edgeUser = BmobUser.getCurrentUser(EdgeUser.class);
                    mUserInfo.postValue(StatusDataResource.success(edgeUser));
                } else {
                    LogUtils.e(TAG, "fetch user info error = " + e.getMessage());
                }
            }
        });
    }
}
