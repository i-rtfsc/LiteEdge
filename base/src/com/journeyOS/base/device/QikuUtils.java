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
package com.journeyOS.base.device;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.util.Log;

import com.journeyOS.base.utils.AppUtils;
import com.journeyOS.base.utils.LogUtils;

import java.lang.reflect.Method;

//https://github.com/zhaozepeng/FloatWindowPermission
public class QikuUtils {
    private static final String TAG = QikuUtils.class.getSimpleName();

    /**
     * 检测 360 悬浮窗权限
     */
    public static boolean checkFloatWindowPermission(Context context) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            return checkOp(context, 24); //OP_SYSTEM_ALERT_WINDOW = 24;
        }
        return true;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private static boolean checkOp(Context context, int op) {
        final int version = Build.VERSION.SDK_INT;
        if (version >= 19) {
            AppOpsManager manager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            try {
                Class clazz = AppOpsManager.class;
                Method method = clazz.getDeclaredMethod("checkOp", int.class, int.class, String.class);
                return AppOpsManager.MODE_ALLOWED == (int) method.invoke(manager, op, Binder.getCallingUid(), context.getPackageName());
            } catch (Exception e) {
                LogUtils.e(TAG, Log.getStackTraceString(e));
            }
        } else {
            LogUtils.e(TAG, "Below API 19 cannot invoke!");
        }
        return false;
    }

    /**
     * 去360权限申请页面
     */
    public static void applyPermission(Context context) {
        Intent intent = new Intent();
        intent.setClassName("com.android.settings",
                "com.android.settings.Settings$OverlaySettingsActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (AppUtils.isIntentAvailable(context, intent)) {
            AppUtils.startIntentInternal(context, intent);
        } else {
            intent.setClassName("com.qihoo360.mobilesafe",
                    "com.qihoo360.mobilesafe.ui.index.AppEnterActivity");
            if (AppUtils.isIntentAvailable(context, intent)) {
                AppUtils.startIntentInternal(context, intent);
            } else {
                LogUtils.e(TAG, "can't open permission page with particular name, please use " +
                        "\"adb shell dumpsys activity\" command and tell me the name of the float window permission page");
            }
        }
    }

}
