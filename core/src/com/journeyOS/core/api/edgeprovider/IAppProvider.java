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


package com.journeyOS.core.api.edgeprovider;

import com.journeyOS.core.api.ICoreApi;
import com.journeyOS.core.database.app.App;

import java.util.List;

public interface IAppProvider extends ICoreApi {

    /**
     * 通过包名获取app
     *
     * @param packageName 包名
     * @return App
     */
    App getApp(String packageName);

    /**
     * 获取所以的App
     *
     * @return App
     */
    List<App> getApps();

    /**
     * 保存或者更新App
     *
     * @param app App
     */
    void insertOrUpdate(App app);

    /**
     * 保存或者更新App
     *
     * @param apps App
     */
    void insertOrUpdate(List<App> apps);

    /**
     * 删除App
     *
     * @param app
     */
    void delete(App app);

    /**
     * 删除App
     *
     * @param apps
     */
    void delete(List<App> apps);

    /**
     * 初始化App
     */
    void loadApps();

    /**
     * 每次进程重启后检查是否有新的app已经安装，但edge还未初始化
     */
    void checkApps();
}
