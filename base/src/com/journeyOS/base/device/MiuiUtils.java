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
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.journeyOS.base.utils.AppUtils;
import com.journeyOS.base.utils.LogUtils;

import java.lang.reflect.Method;

//https://github.com/zhaozepeng/FloatWindowPermission
public class MiuiUtils {
    private static final String TAG = MiuiUtils.class.getSimpleName();

    /**
     * 获取小米 rom 版本号，获取失败返回 -1
     *
     * @return miui rom version code, if fail , return -1
     */
    public static int getMiuiVersion() {
        String version = RomUtils.getSystemProperty("ro.miui.ui.version.name");
        if (version != null) {
            try {
                return Integer.parseInt(version.substring(1));
            } catch (Exception e) {
                LogUtils.e(TAG, "get miui version code error, version : " + version);
                LogUtils.e(TAG, Log.getStackTraceString(e));
            }
        }
        return -1;
    }

    /**
     * 检测 miui 悬浮窗权限
     */
    public static boolean checkFloatWindowPermission(Context context) {
        final int version = Build.VERSION.SDK_INT;

        if (version >= 19) {
            return checkOp(context, 24); //OP_SYSTEM_ALERT_WINDOW = 24;
        } else {
//            if ((context.getApplicationInfo().flags & 1 << 27) == 1) {
//                return true;
//            } else {
//                return false;
//            }
            return true;
        }
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
                Log.e(TAG, Log.getStackTraceString(e));
            }
        } else {
            LogUtils.e(TAG, "Below API 19 cannot invoke!");
        }
        return false;
    }

    /**
     * 小米 ROM 权限申请
     */
    public static void applyMiuiPermission(Context context) {
        int versionCode = getMiuiVersion();
        if (versionCode == 5) {
            goToMiuiPermissionActivity_V5(context);
        } else if (versionCode == 6) {
            goToMiuiPermissionActivity_V6(context);
        } else if (versionCode == 7) {
            goToMiuiPermissionActivity_V7(context);
        } else if (versionCode == 8) {
            goToMiuiPermissionActivity_V8(context);
        } else {
            LogUtils.e(TAG, "this is a special MIUI rom version, its version code " + versionCode);
            CommonUtils.commonROMPermissionApplyInternal(context);
        }
    }

    /**
     * 小米 V5 版本 ROM权限申请
     */
    public static void goToMiuiPermissionActivity_V5(Context context) {
        Intent intent = null;
        String packageName = context.getPackageName();
        intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", packageName, null);
        intent.setData(uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        AppUtils.startIntentInternal(context, intent);
    }

    /**
     * 小米 V6 版本 ROM权限申请
     */
    public static void goToMiuiPermissionActivity_V6(Context context) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.setClassName("com.miui.securitycenter",
                "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
        intent.putExtra("extra_pkgname", context.getPackageName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (AppUtils.isIntentAvailable(context, intent)) {
            AppUtils.startIntentInternal(context, intent);
        } else {
            LogUtils.e(TAG, "Intent is not available!");
        }
    }

    /**
     * 小米 V7 版本 ROM权限申请
     */
    public static void goToMiuiPermissionActivity_V7(Context context) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.setClassName("com.miui.securitycenter",
                "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
        intent.putExtra("extra_pkgname", context.getPackageName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        AppUtils.startIntentInternal(context, intent);
    }

    /**
     * 小米 V8 版本 ROM权限申请
     */
    public static void goToMiuiPermissionActivity_V8(Context context) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.setClassName("com.miui.securitycenter",
                "com.miui.permcenter.permissions.PermissionsEditorActivity");
        intent.putExtra("extra_pkgname", context.getPackageName());
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if (AppUtils.isIntentAvailable(context, intent)) {
            AppUtils.startIntentInternal(context, intent);
        } else {
            intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
            intent.setPackage("com.miui.securitycenter");
            intent.putExtra("extra_pkgname", context.getPackageName());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            AppUtils.startIntentInternal(context, intent);
        }
    }
}
