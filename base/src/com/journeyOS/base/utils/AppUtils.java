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

package com.journeyOS.base.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static android.content.pm.PackageManager.NameNotFoundException;

public class AppUtils {
    private static final String EDGE_PACKAGE = "com.journeyOS.edge";
    private static final String EDGE_SERVICE_AIDL = "com.journeyOS.edge.action.EdgeService";

    private static final String ACCESSIBILITY_SERVICES = "com.journeyOS.edge/com.journeyOS.i007Service.core.accessibility.AccessibilityService";

    public static void startEdge(Context context) {
        Intent intent = new Intent();
        intent.setPackage(EDGE_PACKAGE);
        intent.setAction(EDGE_SERVICE_AIDL);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent);
        } else {
            context.startService(intent);
        }
    }

    public static List<String> getLauncherApp(Context context) {
        PackageManager pm = context.getPackageManager();
        final Intent intent = new Intent(Intent.ACTION_MAIN, null);
        //it should appear in the Launcher as a top-level application
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> resolveInfoList = pm.queryIntentActivities(intent, 0);
        List<String> fpackages = new ArrayList<>();
        for (ResolveInfo ri : resolveInfoList) {
            //ri.activityInfo.packageName;
            String packageName = ri.activityInfo.packageName;
            fpackages.add(packageName);
        }

        return fpackages;
    }

    public static String getAppName(Context context, String packageName, int length) {
        String appName = getAppName(context, packageName);
        if (appName != null && appName.length() > length) {
            return appName.substring(0, length);
        } else {
            return appName;
        }
    }

    public static String getAppName(Context context, String packageName) {
        final PackageManager pm = context.getPackageManager();
        ApplicationInfo ai;
        try {
            ai = pm.getApplicationInfo(packageName, 0);
        } catch (final NameNotFoundException e) {
            ai = null;
        }
        return (String) (ai != null ? pm.getApplicationLabel(ai) : "(unknown)");
    }

    public static Drawable getAppIcon(Context context, String packageName) {
        if (BaseUtils.isNull(packageName)) return null;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            return pi == null ? null : pi.applicationInfo.loadIcon(pm);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getAppVersionName(Context context, String packageName) {
        if (BaseUtils.isNull(packageName)) return null;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            return pi == null ? null : pi.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static int getAppVersionCode(Context context, String packageName) {
        if (BaseUtils.isNull(packageName)) return -1;
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(packageName, 0);
            return pi == null ? -1 : pi.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }

    public static boolean startApp(Context context, Intent intent) {
        if (BaseUtils.isNull(intent)) return false;

        try {
            context.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean startApp(Context context, String packageName) {
        if (BaseUtils.isNull(packageName)) return false;
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(packageName);
        if (intent == null) {
            //LogUtils.w(TAG, "startActivity() called with app not found!");
            return false;
        }

        try {
            context.startActivity(intent);
            return true;
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            return false;
        }

    }

    public static boolean startUri(Context context, String uri) {
        try {
            Intent intent = Intent.parseUri(uri, Intent.URI_INTENT_SCHEME);
            context.startActivity(intent);
            return true;
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return false;
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isPackageExisted(Context context, String targetPackage) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(targetPackage, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }

    @Deprecated
    public static Context getPackageContext(Context context, String packageName) {
        Context packageContext = null;
        try {
            packageContext = context.createPackageContext(packageName,
                    Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return packageContext;
    }

    public static boolean isServiceEnabled(Context context) {
        int enabled = 0;
        boolean found = false;
        try {
            enabled = Settings.Secure.getInt(context.getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);
            LogUtils.d(LogUtils.TAG, "enabled = " + enabled);
        } catch (Settings.SettingNotFoundException e) {
            LogUtils.d(LogUtils.TAG, "accessibility to not found: " + e.getMessage());
        }

        TextUtils.SimpleStringSplitter sCS = new TextUtils.SimpleStringSplitter(':');
        if (enabled == 1) {
            String settingValue = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                TextUtils.SimpleStringSplitter splitter = sCS;
                splitter.setString(settingValue);
                while (splitter.hasNext()) {
                    String accessabilityService = splitter.next();
                    LogUtils.d(LogUtils.TAG, " accessabilityService = " + accessabilityService);
                    if (accessabilityService.equalsIgnoreCase(ACCESSIBILITY_SERVICES)) {
                        LogUtils.d(LogUtils.TAG, "we've found the correct accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            LogUtils.d(LogUtils.TAG, "accessibility is disabled");
        }

        return found;
    }
}
