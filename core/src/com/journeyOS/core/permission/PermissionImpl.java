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
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import com.journeyOS.core.CoreManager;
import com.journeyOS.core.api.location.ILocation;
import com.journeyOS.core.base.BaseActivity;
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
        final String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(activity, permissions, URGENT_PERMISSION);

        for (final String permission : permissions) {
            if ((ActivityCompat.checkSelfPermission(CoreManager.getDefault().getContext(), permission) != PackageManager.PERMISSION_GRANTED)) {
                if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permission)) {
                } else if (Manifest.permission.ACCESS_FINE_LOCATION.equals(permission)) {
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
                if (Manifest.permission.WRITE_EXTERNAL_STORAGE.equals(permissions[index]) && grantResults[index] == PackageManager.PERMISSION_GRANTED) {
                } else if (Manifest.permission.ACCESS_FINE_LOCATION.equals(permissions[index]) && grantResults[index] == PackageManager.PERMISSION_GRANTED) {
                    CoreManager.getDefault().getImpl(ILocation.class).startLocation();
                }
            }
        }
    }

    @Override
    public boolean canDrawOverlays(Context activity) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(activity)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                activity.startActivity(intent);
                return false;
            } else {
                return true;
            }
        }
        return true;
    }

}
