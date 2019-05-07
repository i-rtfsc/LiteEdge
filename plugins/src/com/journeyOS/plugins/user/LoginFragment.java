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
import android.app.AlertDialog;
import android.arch.lifecycle.Observer;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.journeyOS.base.Constant;
import com.journeyOS.base.persistence.SpUtils;
import com.journeyOS.base.utils.BaseUtils;
import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.base.utils.PhoneUtil;
import com.journeyOS.base.utils.TimeUtils;
import com.journeyOS.base.widget.SettingSwitch;
import com.journeyOS.base.widget.SettingView;
import com.journeyOS.base.widget.TimingButton;
import com.journeyOS.core.AccountManager;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.StatusDataResource;
import com.journeyOS.core.SyncManager;
import com.journeyOS.core.api.thread.ICoreExecutors;
import com.journeyOS.core.base.BaseFragment;
import com.journeyOS.core.database.user.EdgeUser;
import com.journeyOS.core.viewmodel.ModelProvider;
import com.journeyOS.plugins.R;
import com.journeyOS.plugins.R2;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import es.dmoral.toasty.Toasty;

public class LoginFragment extends BaseFragment {
    private static final String TAG = LoginFragment.class.getSimpleName();
    static Activity mContext;

    @IntDef({User.USER_NAME, User.PHONE, User.EMAIL})
    @Retention(RetentionPolicy.SOURCE)
    public @interface User {
        int USER_NAME = 1;
        int PHONE = 2;
        int EMAIL = 3;
    }

    //login
    @BindView(R2.id.user_info)
    RelativeLayout mUserLayout;

    @BindView(R2.id.auto_sync)
    SettingSwitch mAutoSync;

    @BindView(R2.id.user_id)
    SettingView mUserIdView;

    @BindView(R2.id.user_phone)
    SettingView mPhoneView;

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
    EdgeUser mEdgeUser;

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

            boolean daemon = SpUtils.getInstant().getBoolean(Constant.AUTO_SYNC, Constant.AUTO_SYNC_DEFAULT);
            mAutoSync.setCheckedImmediately(daemon);
        } else {
            mUserLayout.setVisibility(View.GONE);
            mRegisterLayout.setVisibility(View.GONE);
            smsButton.setEnabled(false);
            AccountManager.getDefault().registerAccountChangedListener(mListener);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        AccountManager.getDefault().unregisterAccountChangedListener(mListener);
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
            AccountManager.getDefault().login(mUser, mToken);
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
            user.backUp = mPassword;
            user.manager = false;
            user.vip = false;
            user.skipAdTime = TimeUtils.getLocalTime();
            user.signOrLogin(mCode, new SaveListener<EdgeUser>() {
                @Override
                public void done(final EdgeUser bmobUser, BmobException e) {
                    if (e == null) {
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
        boolean daemon = SpUtils.getInstant().getBoolean(Constant.AUTO_SYNC, Constant.AUTO_SYNC_DEFAULT);
        mAutoSync.setCheck(!daemon);
        SpUtils.getInstant().put(Constant.AUTO_SYNC, !daemon);
    }

    @OnClick({R2.id.user_id})
    public void listenerUserID() {
        if (!BaseUtils.isNull(mEdgeUser)) {
//            String username = mEdgeUser.getNickname();
//            if (BaseUtils.isNull(username)) {
//                updateInfoDialog(User.USER_NAME);
//            } else {
//                Toasty.info(mActivity, mContext.getString(R.string.has_been_set));
//            }
            updateInfoDialog(User.USER_NAME);
        }
    }

    @OnClick({R2.id.user_phone})
    public void listenerUserPhone() {
        if (!BaseUtils.isNull(mEdgeUser)) {
            String phone = mEdgeUser.getMobilePhoneNumber();
            if (BaseUtils.isNull(phone)) {
                updateInfoDialog(User.PHONE);
            } else {
                Toasty.info(mActivity, mContext.getString(R.string.has_been_set));
            }
        }
    }

    @OnClick({R2.id.user_email})
    public void listenerUserEamil() {
        if (!BaseUtils.isNull(mEdgeUser)) {
            String email = mEdgeUser.getEmail();
            if (BaseUtils.isNull(email)) {
                updateInfoDialog(User.EMAIL);
            } else {
                Toasty.info(mActivity, mContext.getString(R.string.has_been_set));
            }
        }
    }

    void handleUserInfoObserver(final StatusDataResource statusDataResource) {
        String notSet = mContext.getString(R.string.not_set);
        switch (statusDataResource.status) {
            case SUCCESS:
                mEdgeUser = (EdgeUser) statusDataResource.data;

                String username = mEdgeUser.nickname;
                if (!BaseUtils.isNull(username)) {
                    mUserIdView.setRightSummary(username);
                } else {
                    mUserIdView.setRightSummary(notSet);
                }

                String phone = mEdgeUser.getMobilePhoneNumber();
                if (!BaseUtils.isNull(phone)) {
                    mPhoneView.setRightSummary(phone);
                } else {
                    mPhoneView.setRightSummary(notSet);
                }

                String email = mEdgeUser.getEmail();
                if (!BaseUtils.isNull(email)) {
                    mEmailView.setRightSummary(email);
                } else {
                    mEmailView.setRightSummary(notSet);
                }

                break;
            case ERROR:

                break;
        }
    }

    void updateInfoDialog(final @User int user) {
        String setUserInfo = mContext.getString(R.string.set_user_info);
        String title = null;
        switch (user) {
            case User.USER_NAME:
                title = setUserInfo + mContext.getString(R.string.userid);
                break;
            case User.PHONE:
                title = setUserInfo + mContext.getString(R.string.userphone);
                break;
            case User.EMAIL:
                title = setUserInfo + mContext.getString(R.string.useremail);
                break;
        }

        final EditText et = new EditText(mActivity);
        final AlertDialog dialog = new AlertDialog.Builder(mActivity)
                .setTitle(title)
                .setView(et)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String info = et.getText().toString();
                        if (!BaseUtils.isNull(info)) {
                            updateUserInfo(user, info);
                        } else {
                            Toasty.error(mActivity, mContext.getString(R.string.set_user_info_was_null));
                        }
                        //dialog.dismiss();
                    }
                }).show();

    }

    void updateUserInfo(@User final int user, final String info) {
        LogUtils.d(TAG, "update user info, user = [" + user + "], info = [" + info + "]");
        final EdgeUser edgeUser = AccountManager.getDefault().getCurrentUser();
        switch (user) {
            case User.USER_NAME:
                edgeUser.nickname = info;
                break;
            case User.PHONE:
                edgeUser.setMobilePhoneNumber(info);
                break;
            case User.EMAIL:
                edgeUser.setEmail(info);
                break;
        }

        edgeUser.update(edgeUser.getObjectId(), new UpdateListener() {
            @Override
            public void done(final BmobException e) {
                LogUtils.d(TAG, "update user info, e = " + e);
                CoreManager.getDefault().getImpl(ICoreExecutors.class).mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        if (BaseUtils.isNull(e)) {
                            switch (user) {
                                case User.USER_NAME:
                                    mUserIdView.setRightSummary(info);
                                    break;
                                case User.PHONE:
                                    mPhoneView.setRightSummary(info);
                                    break;
                                case User.EMAIL:
                                    mEmailView.setRightSummary(info);
                                    break;
                            }

                            Toasty.info(mActivity, mContext.getString(R.string.set_success));
                        } else {
                            Toasty.error(mActivity, e.getMessage());
                        }

                    }
                });
            }
        });
    }

    private final AccountManager.OnAccountListener mListener = new AccountManager.OnAccountListener() {
        @Override
        public void onLoginSuccess(EdgeUser edgeUser) {
            mUserLayout.setVisibility(View.VISIBLE);
            mLoginLayout.setVisibility(View.GONE);
            mRegisterLayout.setVisibility(View.GONE);
            SyncManager.getDefault().fetchEdgeAir();
            mLoginModel.fetchUserInfo();

            EdgeUser user = new EdgeUser();
            user.backUp = mToken;
            user.update(edgeUser.getObjectId(), new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    LogUtils.d(TAG, "update backup = " + e);
                }
            });
        }

        @Override
        public void onLogOutSuccess() {

        }
    };
}
