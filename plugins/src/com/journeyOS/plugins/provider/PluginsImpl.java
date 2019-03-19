/*
 * Copyright (c) 2018 anqi.huang@outlook.com.
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

package com.journeyOS.plugins.provider;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

import com.journeyOS.base.utils.AppUtils;
import com.journeyOS.core.api.plugins.IPlugins;
import com.journeyOS.core.type.EdgeDirection;
import com.journeyOS.literouter.annotation.ARouterInject;
import com.journeyOS.plugins.LearnActivity;
import com.journeyOS.plugins.SelectorActivity;
import com.journeyOS.plugins.about.AboutFragment;
import com.journeyOS.plugins.barrage.BarrageFragment;
import com.journeyOS.plugins.permission.PermissionFragment;
import com.journeyOS.plugins.search.SearchActivity;
import com.journeyOS.plugins.settings.SettingsFragment;
import com.journeyOS.plugins.user.LoginFragment;

@ARouterInject(api = IPlugins.class)
public class PluginsImpl implements IPlugins {
    private static final String WEATHER_PKG = "com.journeyOS.liteweather";

    @Override
    public void onCreate() {

    }


    @Override
    public void navigationSelectorActivity(Context context, int postion, EdgeDirection direction) {
//        SelectorActivity.navigationActivity(context, postion, direction);
        SelectorActivity.navigationFromApplication(context, postion, direction);
    }

    @Override
    public void navigationSearchActivity(Context context) {
        SearchActivity.navigationFromApplication(context);
    }

    @Override
    public void navigationLearnActivity(Context context) {
        LearnActivity.navigationActivity(context);
    }

    @Override
    public boolean isWeatherAppExisted(Context context) {
        return AppUtils.isPackageExisted(context, WEATHER_PKG);
    }

    @Override
    public void navigationWeatherApp(Context context) {
        AppUtils.startApp(context, WEATHER_PKG);
    }

    @Override
    public Fragment provideSettingsFragment(Activity activity) {
        return SettingsFragment.newInstance(activity);
    }

    @Override
    public Fragment provideAboutFragment(Activity activity) {
        return AboutFragment.newInstance(activity);
    }

    @Override
    public Fragment providePermissionFragment(Activity activity) {
        return PermissionFragment.newInstance(activity);
    }

    @Override
    public Fragment provideLoginFragment(Activity activity) {
        return LoginFragment.newInstance(activity);
    }

    @Override
    public Fragment provideBarrageFragment(Activity activity) {
        return BarrageFragment.newInstance(activity);
    }

}
