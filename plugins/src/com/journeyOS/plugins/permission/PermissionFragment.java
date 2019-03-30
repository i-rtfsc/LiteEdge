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

package com.journeyOS.plugins.permission;

import android.app.Activity;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.journeyOS.base.utils.AppUtils;
import com.journeyOS.base.widget.SettingView;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.base.BaseFragment;
import com.journeyOS.core.permission.IPermission;
import com.journeyOS.i007Service.I007Manager;
import com.journeyOS.plugins.R;
import com.journeyOS.plugins.R2;

import butterknife.BindView;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

public class PermissionFragment extends BaseFragment {

    static Activity mContext;

    @BindView(R2.id.overflow)
    SettingView mOverflow;
    @BindView(R2.id.notification)
    SettingView mNotification;
    @BindView(R2.id.accessibility)
    SettingView mAccessibility;
    @BindView(R2.id.device_admin)
    SettingView mDeviceAdmin;

    public static Fragment newInstance(Activity activity) {
        PermissionFragment fragment = new PermissionFragment();
        mContext = activity;
        return fragment;
    }

    @Override
    public int attachLayoutRes() {
        return R.layout.fragment_permission;
    }

    @Override
    public void initBeforeView() {
        super.initBeforeView();
    }

    @Override
    public void initViews() {
        if (Build.VERSION.SDK_INT >= 23) {
            boolean canDrawOverlays = CoreManager.getDefault().getImpl(IPermission.class).canDrawOverlays(mContext);
            mOverflow.setRightSummary(mContext.getString(canDrawOverlays ? R.string.permission_on : R.string.permission_off));
        }

        boolean hasNotification = CoreManager.getDefault().getImpl(IPermission.class).hasListenerNotification(mContext);
        mNotification.setRightSummary(mContext.getString(hasNotification ? R.string.permission_on : R.string.permission_off));

        boolean isAccessibility = AppUtils.isServiceEnabled(mContext);
        mAccessibility.setRightSummary(mContext.getString(isAccessibility ? R.string.permission_on : R.string.permission_off));

        boolean isAdminActive = CoreManager.getDefault().getImpl(IPermission.class).isAdminActive(mContext);
        mDeviceAdmin.setRightSummary(mContext.getString(isAdminActive ? R.string.permission_on : R.string.permission_off));
    }

    @OnClick({R2.id.overflow})
    public void listenerOverflow() {
        CoreManager.getDefault().getImpl(IPermission.class).drawOverlays(mContext, false);
    }

    @OnClick({R2.id.notification})
    public void listenerNotification() {
        CoreManager.getDefault().getImpl(IPermission.class).listenerNotification(mContext, false);
    }

    @OnClick({R2.id.accessibility})
    public void listenerAccessibility() {
        I007Manager.openSettingsAccessibilityService();
    }

    @OnClick({R2.id.device_admin})
    public void listenerDeviceAdmin() {
        CoreManager.getDefault().getImpl(IPermission.class).enableAdminActive(mContext);
    }

    @OnClick({R2.id.remove_device_admin})
    public void listenerRemoveDeviceAdmin() {
        boolean isAdminActive = CoreManager.getDefault().getImpl(IPermission.class).isAdminActive(mContext);
        if (isAdminActive) {
            CoreManager.getDefault().getImpl(IPermission.class).disableAdminActive(mContext);
        } else {
            String message = mContext.getString(R.string.hasnot_permission) + mContext.getString(R.string.device_admin);
            Toasty.warning(mContext, message, Toast.LENGTH_SHORT).show();
        }
    }

}
