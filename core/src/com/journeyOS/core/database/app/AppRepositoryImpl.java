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

package com.journeyOS.core.database.app;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.journeyOS.base.Constant;
import com.journeyOS.base.persistence.SpUtils;
import com.journeyOS.base.utils.AppUtils;
import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.api.edgeprovider.IAppProvider;
import com.journeyOS.core.database.DBConfigs;
import com.journeyOS.core.database.DBHelper;
import com.journeyOS.core.database.EdgeDatabase;
import com.journeyOS.literouter.annotation.ARouterInject;

import java.util.ArrayList;
import java.util.List;

@ARouterInject(api = IAppProvider.class)
public class AppRepositoryImpl implements IAppProvider {
    private static final String TAG = AppRepositoryImpl.class.getSimpleName();
    private AppDao appDao;
    private Object mLock = new Object();

    @Override
    public void onCreate() {
        EdgeDatabase database = DBHelper.provider(CoreManager.getDefault().getContext(), EdgeDatabase.class, DBConfigs.DB_NAME);
        appDao = database.appDao();
    }

    @Override
    public App getApp(String packageName) {
        synchronized (mLock) {
            return appDao.getApp(packageName);
        }
    }

    @Override
    public List<App> getApps() {
        synchronized (mLock) {
            return appDao.getApps();
        }
    }

    @Override
    public void insertOrUpdate(App app) {
        synchronized (mLock) {
            appDao.insert(app);
        }
    }

    @Override
    public void insertOrUpdate(List<App> apps) {
        synchronized (mLock) {
            appDao.insert(apps);
        }
    }

    @Override
    public void delete(App app) {
        synchronized (mLock) {
            appDao.delete(app);
        }
    }

    @Override
    public void delete(List<App> apps) {
        synchronized (mLock) {
            appDao.delete(apps);
        }
    }

    @Override
    public void loadApps() {
        boolean appInited = SpUtils.getInstant().getBoolean(Constant.APP_INITED, false);
        LogUtils.d(TAG, "device has been init database = " + appInited);
        if (appInited) {
            return;
        }

        List<String> apps = AppUtils.getLauncherApp(CoreManager.getDefault().getContext());
        LogUtils.d(TAG, "device install apps size = " + apps.size());
        List<App> allApps = new ArrayList<>();
        for (String packageName : apps) {
            App app = new App();
            app.appName = AppUtils.getAppName(CoreManager.getDefault().getContext(), packageName);
            app.packageName = packageName;
            app.barrage = 1;
            allApps.add(app);
        }
        appDao.insert(allApps);
        SpUtils.getInstant().put(Constant.APP_INITED, true);
    }

    @Override
    public void checkApps() {
        synchronized (mLock) {
            PackageManager pm = CoreManager.getDefault().getContext().getPackageManager();
            final Intent intent = new Intent(Intent.ACTION_MAIN, null);
            //it should appear in the Launcher as a top-level application
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            List<ResolveInfo> resolveInfoList = pm.queryIntentActivities(intent, 0);
            List<String> fpackages = new ArrayList<>();
            for (ResolveInfo ri : resolveInfoList) {
                String packageName = ri.activityInfo.packageName;
                App app = appDao.getApp(packageName);
                if (app == null) {
                    app = new App();
                    app.appName = AppUtils.getAppName(CoreManager.getDefault().getContext(), packageName);
                    app.packageName = packageName;
                    app.barrage = 1;
                    appDao.insert(app);
                }
            }
        }
    }
}
