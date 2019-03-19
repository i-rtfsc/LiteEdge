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

package com.journeyOS.core.api.plugins;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

import com.journeyOS.core.api.ICoreApi;
import com.journeyOS.core.type.EdgeDirection;

public interface IPlugins extends ICoreApi {
    /**
     * 启动选择app的activity
     *
     * @param context
     * @param postion   edge具体元素的位置
     * @param direction edge的方向
     */
    void navigationSelectorActivity(Context context, int postion, EdgeDirection direction);

    /**
     * 启动搜索城市的activity
     *
     * @param context
     */
    void navigationSearchActivity(Context context);

    /**
     * 启动学习的activity
     *
     * @param context
     */
    void navigationLearnActivity(Context context);

    /**
     * 检查com.journeyOS.liteweather是否安装
     */
    boolean isWeatherAppExisted(Context context);

    /**
     * 启动com.journeyOS.liteweather
     */
    void navigationWeatherApp(Context context);

    /**
     * 获取设置Fragment
     */
    Fragment provideSettingsFragment(Activity activity);

    /**
     * 获取关于Fragment
     */
    Fragment provideAboutFragment(Activity activity);

    /**
     * 获取权限Fragment
     */
    Fragment providePermissionFragment(Activity activity);

    /**
     * 获取登陆注册Fragment
     */
    Fragment provideLoginFragment(Activity activity);

    /**
     * 获取弹幕Fragment
     */
    Fragment provideBarrageFragment(Activity activity);
}
