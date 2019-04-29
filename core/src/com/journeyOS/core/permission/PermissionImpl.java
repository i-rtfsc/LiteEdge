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

package com.journeyOS.core.permission;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;

import com.journeyOS.base.device.CommonUtils;
import com.journeyOS.base.device.HuaweiUtils;
import com.journeyOS.base.device.MeizuUtils;
import com.journeyOS.base.device.MiuiUtils;
import com.journeyOS.base.device.OppoUtils;
import com.journeyOS.base.device.QikuUtils;
import com.journeyOS.base.device.RomUtils;
import com.journeyOS.base.utils.AppUtils;
import com.journeyOS.core.CoreDeviceAdmin;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.R;
import com.journeyOS.core.api.location.ILocation;
import com.journeyOS.core.base.BaseActivity;
import com.journeyOS.i007Service.core.notification.NotificationListenerService;
import com.journeyOS.literouter.annotation.ARouterInject;


@ARouterInject(api = IPermission.class)
public class PermissionImpl implements IPermission {
    private static final String TAG = PermissionImpl.class.getSimpleName();
    private static final int URGENT_PERMISSION = 0x01;

    @Override
    public void onCreate() {
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void initUrgentPermission(final BaseActivity activity) {
        final String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(activity, permissions, URGENT_PERMISSION);

        for (final String permission : permissions) {
            if ((ActivityCompat.checkSelfPermission(CoreManager.getDefault().getContext(), permission) != PackageManager.PERMISSION_GRANTED)) {
                if (Manifest.permission.ACCESS_FINE_LOCATION.equals(permission)) {
                    CoreManager.getDefault().getImpl(ILocation.class).startLocation();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(BaseActivity activity, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (activity.isFinishing() || activity.isDestroyed()) {
            return;
        }

        if (requestCode == URGENT_PERMISSION) {
            for (int index = 0; index < permissions.length; index++) {
                if (Manifest.permission.ACCESS_FINE_LOCATION.equals(permissions[index]) && grantResults[index] == PackageManager.PERMISSION_GRANTED) {
                    CoreManager.getDefault().getImpl(ILocation.class).startLocation();
                }
            }
        }
    }

    @Override
    public boolean canDrawOverlays(Context activity) {
        if (Build.VERSION.SDK_INT < 23) {
            if (RomUtils.checkIsMiuiRom()) {
                return MiuiUtils.checkFloatWindowPermission(activity);
            } else if (RomUtils.checkIsMeizuRom()) {
                return MeizuUtils.checkFloatWindowPermission(activity);
            } else if (RomUtils.checkIsHuaweiRom()) {
                return HuaweiUtils.checkFloatWindowPermission(activity);
            } else if (RomUtils.checkIs360Rom()) {
                return QikuUtils.checkFloatWindowPermission(activity);
            } else if (RomUtils.checkIsOppoRom()) {
                return OppoUtils.checkFloatWindowPermission(activity);
            }
        } else {
            if (RomUtils.checkIsMeizuRom()) {
                return MeizuUtils.checkFloatWindowPermission(activity);
            } else {
                if (!Settings.canDrawOverlays(activity)) {
                    return false;
                } else {
                    return true;
                }
            }
        }
        return true;
    }

    @Override
    public void drawOverlays(final Context activity, boolean showDialog) {
        if (showDialog) {
            String message = activity.getString(R.string.hasnot_permission) + activity.getString(R.string.overflow);
            final AlertDialog dialog = new AlertDialog.Builder(activity, R.style.CornersAlertDialog)
                    .setTitle(R.string.overflow)
                    .setMessage(message)
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            applyDevicePermission(activity);
                        }
                    })
                    .create();
            dialog.show();
        } else {
            applyDevicePermission(activity);
        }
    }

    @Override
    public boolean hasListenerNotification(Context activity) {
        String notifications = Settings.Secure.getString(activity.getContentResolver(),
                "enabled_notification_listeners");
        if (notifications == null) {
            return false;
        } else {
            if (!notifications.contains(NotificationListenerService.class.getName())) {
                return false;
            } else {
                return true;
            }
        }
    }

    @Override
    public void listenerNotification(final Context activity, boolean showDialog) {
        if (showDialog) {
            String message = activity.getString(R.string.hasnot_permission) + activity.getString(R.string.notification_permission);
            final AlertDialog dialog = new AlertDialog.Builder(activity, R.style.CornersAlertDialog)
                    .setTitle(R.string.notification_permission)
                    .setMessage(message)
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int i) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                            AppUtils.startIntentInternal(activity, intent);
                        }
                    })
                    .create();
            dialog.show();
        } else {
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            AppUtils.startIntentInternal(activity, intent);
        }
    }

    @Override
    public boolean isAdminActive(Context activity) {
        DevicePolicyManager dpm = CoreDeviceAdmin.getDefault().getManager(activity);
        boolean isAdminActive = dpm.isAdminActive(CoreDeviceAdmin.getDefault().getComponentName(activity));
        return isAdminActive;
    }

    @Override
    public void enableAdminActive(Context activity) {
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN,
                CoreDeviceAdmin.getDefault().getComponentName(activity));
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
                activity.getString(R.string.device_admin_description));
        AppUtils.startIntentInternal(activity, intent);
    }

    @Override
    public void disableAdminActive(Context activity) {
        DevicePolicyManager dpm = CoreDeviceAdmin.getDefault().getManager(activity);
        dpm.removeActiveAdmin(CoreDeviceAdmin.getDefault().getComponentName(activity));
    }

    private void applyDevicePermission(Context activity) {
        if (Build.VERSION.SDK_INT < 23) {
            if (RomUtils.checkIsMiuiRom()) {
                MiuiUtils.applyMiuiPermission(activity);
            } else if (RomUtils.checkIsMeizuRom()) {
                MeizuUtils.applyPermission(activity);
            } else if (RomUtils.checkIsHuaweiRom()) {
                HuaweiUtils.applyPermission(activity);
            } else if (RomUtils.checkIs360Rom()) {
                QikuUtils.applyPermission(activity);
            } else if (RomUtils.checkIsOppoRom()) {
                OppoUtils.applyOppoPermission(activity);
            }
        } else {
            CommonUtils.commonROMPermissionApplyInternal(activity);
        }
    }
}
