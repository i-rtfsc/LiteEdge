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
import com.journeyOS.core.type.FingerDirection;

public interface IPlugins extends ICoreApi {
    /**
     * 添加app到Edge
     *
     * @param context
     * @param postion   edge具体元素的位置
     * @param direction edge的方向
     */
    void navigationEdgeSelector(Context context, int postion, EdgeDirection direction);

    /**
     * 添加app到手势
     *
     * @param context
     * @param rotation edge具体元素的位置
     */
    void navigationGestureSelector(Context context, int rotation, FingerDirection direction);

    /**
     * 添加app到场景
     *
     * @param context
     * @param rotation edge具体元素的位置
     */
    void navigationSceneSelector(Context context, int scene);

    /**
     * 启动选择手势功能的的activity
     *
     * @param context
     * @param rotation  屏幕的方向
     * @param direction 手势的方向
     */
    void navigationMoreSelectorActivity(Context context, int rotation, FingerDirection direction);

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


    /**
     * 获取实验室Fragment
     */
    Fragment provideLabFragment(Activity activity);

    /**
     * 获取实验室Fragment
     */
    Fragment provideGestureFragment(Activity activity, int orientation);

    /**
     * 获取管理员Fragment
     */
    Fragment provideAdminFragment(Activity activity);

    /**
     * 获取实验室Fragment
     */
    Fragment provideSceneFragment(Activity activity, int scene);
}
