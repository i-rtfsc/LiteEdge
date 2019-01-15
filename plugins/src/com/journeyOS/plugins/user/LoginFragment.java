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

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.View;
import android.widget.RelativeLayout;

import com.journeyOS.base.Constant;
import com.journeyOS.base.persistence.SpUtils;
import com.journeyOS.base.utils.BaseUtils;
import com.journeyOS.base.utils.PhoneUtil;
import com.journeyOS.base.widget.SettingSwitch;
import com.journeyOS.base.widget.SettingText;
import com.journeyOS.base.widget.SettingView;
import com.journeyOS.base.widget.TimingButton;
import com.journeyOS.core.AccountManager;
import com.journeyOS.core.StatusDataResource;
import com.journeyOS.core.SyncManager;
import com.journeyOS.core.base.BaseFragment;
import com.journeyOS.core.database.user.EdgeUser;
import com.journeyOS.core.viewmodel.ModelProvider;
import com.journeyOS.plugins.R;
import com.journeyOS.plugins.R2;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.LogInListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;

public class LoginFragment extends BaseFragment {
    private static final String TAG = LoginFragment.class.getSimpleName();
    static Activity mContext;

    //login
    @BindView(R2.id.user_info)
    RelativeLayout mUserLayout;

    @BindView(R2.id.auto_sync)
    SettingSwitch mAutoSync;

    @BindView(R2.id.user_id)
    SettingText mUserIdView;

    @BindView(R2.id.user_phone)
    SettingText mPhoneView;

    @BindView(R2.id.user_email)
    SettingView mEmailView;

    //login
    @BindView(R2.id.login_view)
    RelativeLayout mLoginLayout;

    //register
    @BindView(R2.id.register_view)
    RelativeLayout mRegisterLayout;

    @BindView(R2.id.smsButton)
    TimingButton smsButton;


    String mPhone = null;
    String mPassword = null;
    String mCode = null;

    String mUser = null;
    String mToken = null;

    LoginModel mLoginModel;

    final Observer<StatusDataResource> userInfoObserver = new Observer<StatusDataResource>() {
        @Override
        public void onChanged(StatusDataResource statusDataResource) {
            handleUserInfoObserver(statusDataResource);
        }
    };

    public static Fragment newInstance(Activity activity) {
        LoginFragment fragment = new LoginFragment();
        mContext = activity;
        return fragment;
    }

    @Override
    public int attachLayoutRes() {
        return R.layout.fragment_login;
    }

    @Override
    public void initViews() {
        boolean isLogin = AccountManager.getDefault().isLogin();
        if (isLogin) {
            mUserLayout.setVisibility(View.VISIBLE);
            mLoginLayout.setVisibility(View.GONE);
            mRegisterLayout.setVisibility(View.GONE);

            boolean daemon = SpUtils.getInstant().getBoolean(Constant.AUTO_SYNC, true);
            mAutoSync.setCheck(daemon);

        } else {
            mUserLayout.setVisibility(View.GONE);
            mRegisterLayout.setVisibility(View.GONE);
            smsButton.setEnabled(false);
        }
    }

    @Override
    protected void initDataObserver(Bundle savedInstanceState) {
        super.initDataObserver(savedInstanceState);
        mLoginModel = ModelProvider.getModel(this, LoginModel.class);
        mLoginModel.fetchUserInfo();

        mLoginModel.getUserInfo().observe(this, userInfoObserver);
    }

    @OnClick(R2.id.fab_register)
    public void startRegisterClick(View view) {
        mUserLayout.setVisibility(View.GONE);
        mLoginLayout.setVisibility(View.GONE);
        mRegisterLayout.setVisibility(View.VISIBLE);
    }

    @OnTextChanged(value = R2.id.login_username, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void loginUserChanged(Editable s) {
        mUser = s.toString();
    }

    @OnTextChanged(value = R2.id.login_password, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void loginPasswordChanged(Editable s) {
        mToken = s.toString();
    }

    @OnClick(R2.id.login)
    public void LoginClick(View view) {
        if (!BaseUtils.isNull(mUser) && !BaseUtils.isNull(mToken)) {
            EdgeUser.loginByAccount(mUser, mToken, new LogInListener<EdgeUser>() {
                @Override
                public void done(final EdgeUser edgeUser, BmobException e) {
                    if (e == null) {
                        mUserLayout.setVisibility(View.VISIBLE);
                        mLoginLayout.setVisibility(View.GONE);
                        mRegisterLayout.setVisibility(View.GONE);
                        AccountManager.getDefault().save2Db(mUser, mToken);
                        SyncManager.getDefault().fetchEdgeAir();
                        mLoginModel.fetchUserInfo();
                    }
                }
            });
        }

    }

    @OnClick(R2.id.fab_close)
    public void gotoLoginClick(View view) {
        mUserLayout.setVisibility(View.GONE);
        mLoginLayout.setVisibility(View.VISIBLE);
        mRegisterLayout.setVisibility(View.GONE);
    }

    @OnClick(R2.id.smsButton)
    public void smsButtonStart(View view) {
        if (PhoneUtil.isMobile(mPhone)) {
            BmobSMS.requestSMSCode(mPhone, "", new QueryListener<Integer>() {
                @Override
                public void done(Integer smsId, BmobException e) {
                    if (BaseUtils.isNull(e)) {
                        smsButton.start();
                    }
                }
            });
        }
    }

    @OnClick(R2.id.register)
    public void registerClick(View view) {
        if (PhoneUtil.isMobile(mPhone) && !BaseUtils.isNull(mPassword) && !BaseUtils.isNull(mCode)) {
            EdgeUser user = new EdgeUser();
            user.setMobilePhoneNumber(mPhone);
            user.setPassword(mPassword);
            user.signOrLogin(mCode, new SaveListener<EdgeUser>() {
                @Override
                public void done(final EdgeUser bmobUser, BmobException e) {
                    if (e == null) {
                        AccountManager.getDefault().save2Db(mUser, mToken);
                        mUserLayout.setVisibility(View.VISIBLE);
                        SyncManager.getDefault().fetchEdgeAir();
                    }
                }
            });
        }
    }

    @OnTextChanged(value = R2.id.register_username, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void registerPhoneChanged(Editable s) {
        String phone = s.toString();
        boolean isPhone = PhoneUtil.isMobile(phone);
        if (isPhone) {
            mPhone = phone;
            smsButton.setEnabled(true);
        } else {
            smsButton.setEnabled(false);
        }
    }

    @OnTextChanged(value = R2.id.register_password, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void registerPasswordChanged(Editable s) {
        mPassword = s.toString();
    }

    @OnTextChanged(value = R2.id.register_code, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    void registerCodeChanged(Editable s) {
        mCode = s.toString();
    }

    @OnClick({R2.id.auto_sync})
    public void listenerAutoSync() {
        boolean daemon = SpUtils.getInstant().getBoolean(Constant.AUTO_SYNC, true);
        mAutoSync.setCheck(!daemon);
        SpUtils.getInstant().put(Constant.AUTO_SYNC, !daemon);
    }

    void handleUserInfoObserver(final StatusDataResource statusDataResource) {
        String notSet = mContext.getString(R.string.not_set);
        switch (statusDataResource.status) {
            case SUCCESS:
                EdgeUser user = (EdgeUser) statusDataResource.data;

                String username = user.getUsername();
                if (!BaseUtils.isNull(username)) {
                    mUserIdView.setRightText(username);
                    mUserIdView.setEnabled(false);
                } else {
                    mUserIdView.setRightText(notSet);
                }

                String phone = user.getMobilePhoneNumber();
                if (!BaseUtils.isNull(phone)) {
                    mPhoneView.setRightText(phone);
                    mPhoneView.setEnabled(false);
                } else {
                    mPhoneView.setRightText(notSet);
                }

                String email = user.getEmail();
                if (!BaseUtils.isNull(email)) {
                    mEmailView.setSummary(email);
                    mEmailView.setEnabled(false);
                } else {
                    mEmailView.setSummary(notSet);
                }

                break;
            case ERROR:

                break;
        }

    }
}
