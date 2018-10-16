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


package com.journeyOS.core.api.edgeprovider;

import com.journeyOS.core.api.ICoreApi;
import com.journeyOS.core.database.ball.Ball;

public interface IBallProvider extends ICoreApi {

    /**
     * 获取悬浮球的配置
     *
     * @param orientation 屏幕的方向
     * @return 悬浮球的配置
     */
    Ball getConfig(int orientation);

    /**
     * 更新悬浮球的配置
     *
     * @param config 悬浮球的配置
     */
    void insertOrUpdateConfig(Ball config);

    /**
     * 删除悬浮球的配置
     *
     * @param config 悬浮球的配置
     */
    void deleteConfig(Ball config);

    /**
     * 删除全部悬浮球的配置
     */
    void deleteAll();
}
