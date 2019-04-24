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

package com.journeyOS.plugins.about;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import com.journeyOS.base.Constant;
import com.journeyOS.base.utils.AppUtils;
import com.journeyOS.base.utils.BaseUtils;
import com.journeyOS.base.widget.SettingView;
import com.journeyOS.core.Version;
import com.journeyOS.core.base.BaseFragment;
import com.journeyOS.core.pay.PayManager;
import com.journeyOS.plugins.R;
import com.journeyOS.plugins.R2;

import butterknife.BindView;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

public class AboutFragment extends BaseFragment {

    @BindView(R2.id.version)
    SettingView mVersion;
    @BindView(R2.id.email)
    SettingView mEmail;

    public static final int REQUEST_CODE = 1010;

    static Activity mContext;

    public static Fragment newInstance(Activity activity) {
        AboutFragment fragment = new AboutFragment();
        mContext = activity;
        return fragment;
    }

    @Override
    public int attachLayoutRes() {
        return R.layout.fragment_about;
    }

    @Override
    public void initViews() {
        mVersion.setRightSummary(Version.getVersionName(mContext));
        mEmail.setRightSummary(Constant.EMAIL);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            PayManager.getDefault().payTencentMM();
        } else {
            //还未获得权限
        }
    }

    @OnClick({R2.id.version})
    void listenerVersion() {
        BaseUtils.openInMarket(mContext);
    }

    @OnClick({R2.id.email})
    void listenerEmail() {
        BaseUtils.launchEmail(mContext, Constant.EMAIL);
    }


    @OnClick({R2.id.alipay})
    void listenerAlipay() {
        boolean existed = AppUtils.isPackageExisted(mContext, PayManager.ALIPAY_PACKAGE);
        if (existed) {
            PayManager.getDefault().payAlipay();
        } else {
            Toasty.warning(mContext, mContext.getString(R.string.app_not_existed)).show();
        }
    }


    @OnClick({R2.id.tencentmm})
    void listenerTencentMMpay() {
        boolean existed = AppUtils.isPackageExisted(mContext, PayManager.TENCENT_PACKAGE);
        if (existed) {
            if (ContextCompat.checkSelfPermission(mContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                //已经有权限
                showDialog();
            } else {
                ActivityCompat.requestPermissions(mContext, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
            }
        } else {
            Toasty.warning(mContext, mContext.getString(R.string.app_not_existed)).show();
        }
    }

    private void showDialog() {
        final AlertDialog dialog = new AlertDialog.Builder(mContext, R.style.CornersAlertDialog)
                .setTitle(mContext.getString(R.string.pay_tencent_mm_dialog_title))
                .setMessage(mContext.getString(R.string.pay_tencent_mm_dialog_message))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        PayManager.getDefault().payTencentMM();
                        dialog.dismiss();
                    }
                })
                .create();
        dialog.show();
    }
}
