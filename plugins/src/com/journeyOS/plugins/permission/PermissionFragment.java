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
import android.support.v4.app.Fragment;
import android.widget.Toast;

import com.journeyOS.core.CoreManager;
import com.journeyOS.core.base.BaseFragment;
import com.journeyOS.core.permission.IPermission;
import com.journeyOS.i007Service.core.notification.NotificationListenerService;
import com.journeyOS.plugins.R;
import com.journeyOS.plugins.R2;

import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

public class PermissionFragment extends BaseFragment {

    static Activity mContext;

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

    }

    @OnClick({R2.id.overflow})
    public void listenerOverflow() {
        boolean hasPermission = CoreManager.getDefault().getImpl(IPermission.class).canDrawOverlays(mContext);
        if (hasPermission) {
            String message = mContext.getString(R.string.has_permission) + mContext.getString(R.string.overflow);
            Toasty.success(mContext, message, Toast.LENGTH_SHORT).show();
        } else {
            CoreManager.getDefault().getImpl(IPermission.class).drawOverlays(mContext);
        }
    }

    @OnClick({R2.id.notification})
    public void listenerNotification() {
        boolean hasPermission = CoreManager.getDefault().getImpl(IPermission.class).hasListenerNotification(mContext);
        if (hasPermission) {
            String message = mContext.getString(R.string.has_permission) + mContext.getString(R.string.notification_permission);
            Toasty.success(mContext, message, Toast.LENGTH_SHORT).show();
        } else {
            CoreManager.getDefault().getImpl(IPermission.class).listenerNotification(mContext);
        }
    }

}
