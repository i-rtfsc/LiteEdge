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

import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.journeyOS.base.utils.Singleton;

public class CoreDeviceAdmin extends DeviceAdminReceiver {

    private CoreDeviceAdmin() {
    }

    private static final Singleton<CoreDeviceAdmin> gDefault = new Singleton<CoreDeviceAdmin>() {
        @Override
        protected CoreDeviceAdmin create() {
            return new CoreDeviceAdmin();
        }
    };

    public static CoreDeviceAdmin getDefault() {
        return gDefault.get();
    }


    @Override
    public DevicePolicyManager getManager(Context context) {
        return super.getManager(context);
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
    }

    public ComponentName getComponentName(Context context) {
        return new ComponentName(context.getApplicationContext(), CoreDeviceAdmin.class);
    }

}
