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

package com.journeyOS.core.config;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import com.facebook.stetho.Stetho;
import com.google.android.gms.ads.MobileAds;
import com.journeyOS.base.device.DeviceUtils;
import com.journeyOS.base.persistence.SpUtils;
import com.journeyOS.base.utils.FileIOUtils;
import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.core.AccountManager;
import com.journeyOS.core.BuildConfig;
import com.journeyOS.core.R;
import com.journeyOS.core.Version;
import com.journeyOS.core.database.user.EdgeUser;
import com.journeyOS.core.push.Installation;
import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;

import cn.bmob.push.BmobPush;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobInstallationManager;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.InstallationListener;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import es.dmoral.toasty.Toasty;


public class AppConfig {
    private static final String APP_NAME = "edge";
    private static final String BUGLY_APPID = "6268c7a221";

    private static final String ADMOB_APPID = "ca-app-pub-7876057690602353~3648682075";

    public static void initialize(Application context) {
        initANRWatch(context);
        initCrashReport(context);
        initFile();
        initSharedPreference(context);
        initToastyConfig(context);
        initBmob(context);
        initAd(context);
    }

    private static void initCrashReport(Context context) {
        if (!BuildConfig.DEBUG) {
            Beta.autoInit = true;
            Beta.autoCheckUpgrade = true;
            Beta.initDelay = 3 * 1000;
            Beta.largeIconId = R.drawable.svg_core_ball;
            Beta.smallIconId = R.drawable.svg_core_ball;
            Beta.defaultBannerId = R.drawable.svg_core_ball;
            Beta.storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            Bugly.init(context, BUGLY_APPID, BuildConfig.DEBUG);
        }
    }


    private static void initANRWatch(Application context) {
        if (BuildConfig.DEBUG) {
            Stetho.initializeWithDefaults(context);
            // LeakCanary.install(context);
        }
    }

    private static void initFile() {
        FileIOUtils.init(APP_NAME);
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

    private static void initBmob(final Application context) {
        Bmob.initialize(context, "6aa0fcc54f48025459d573c92e95870b");
        BmobInstallationManager.getInstance().initialize(new InstallationListener<BmobInstallation>() {
            @Override
            public void done(BmobInstallation bmobInstallation, BmobException e) {
                if (e == null) {
                    String objectId = bmobInstallation.getObjectId();
                    LogUtils.d("init bmob installation, objectId = [" + objectId + "]" +
                            ", installationId = [" + bmobInstallation.getInstallationId() + "]");
                    Installation installation = new Installation();
                    if (AccountManager.getDefault().isLogin()) {
                        EdgeUser edgeUser = BmobUser.getCurrentUser(EdgeUser.class);
                        installation.author = edgeUser;
                    }
                    installation.brand = DeviceUtils.getDeviceBrand();
                    installation.model = DeviceUtils.getSystemModel();
                    installation.version = DeviceUtils.getSystemVersion();
                    installation.appVersion = Version.getVersionName(context);
                    installation.update(objectId, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            LogUtils.d("update, e = " + e);
                        }
                    });
                } else {
                    LogUtils.e(e.getMessage());
                }
            }
        });
        // 启动推送服务
        BmobPush.startWork(context);
    }

    private static void initAd(Application context) {
        MobileAds.initialize(context, ADMOB_APPID);
    }
}
