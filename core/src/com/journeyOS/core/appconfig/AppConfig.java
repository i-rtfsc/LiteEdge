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

package com.journeyOS.core.appconfig;

import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;

import com.facebook.stetho.Stetho;
import com.journeyOS.base.persistence.SpUtils;
import com.journeyOS.core.BuildConfig;
import com.journeyOS.core.R;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;

import es.dmoral.toasty.Toasty;


public class AppConfig {
    private static final String APP_NAME = "edge";
    private static final String BUGLY_APPID = "6268c7a221";

    public static void initialize(Application context) {
        initANRWatch(context);
        initCrashReport(context);
        initSharedPreference(context);
        initToastyConfig(context);
    }

    private static void initCrashReport(Context context) {
        Beta.autoInit = true;
        Beta.autoCheckUpgrade = true;
        Beta.initDelay = 3 * 1000;
        Beta.largeIconId = R.drawable.svg_core_ball;
        Beta.smallIconId = R.drawable.svg_core_ball;
        Beta.defaultBannerId = R.drawable.svg_core_ball;
        Beta.storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        Bugly.init(context, BUGLY_APPID, BuildConfig.DEBUG);
    }


    private static void initANRWatch(Application context) {
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(context);
            // LeakCanary.install(context);
        }
    }

    private static void initSharedPreference(Context context) {
        SpUtils.init(context);
    }

    private static void initToastyConfig(Context context) {
        Toasty.Config.getInstance()
                .setErrorColor(context.getResources().getColor(R.color.red))
                .setInfoColor(context.getResources().getColor(R.color.colorPrimary))
                .setSuccessColor(context.getResources().getColor(R.color.darkturquoise))
                .setWarningColor(context.getResources().getColor(R.color.tomato))
                .setTextColor(context.getResources().getColor(R.color.white))
                .tintIcon(true)
                .apply();
    }
}
