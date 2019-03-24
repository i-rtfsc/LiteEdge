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
import com.journeyOS.core.database.edgelab.EdgeLab;

import java.util.List;

public interface IEdgeLabProvider extends ICoreApi {
    /**
     * 获取全部EdgeLab的配置
     *
     * @return 获取全部EdgeLab的配置
     */
    List<EdgeLab> getConfigs();

    /**
     * 根据方向获取Edge的配置
     *
     * @param edge 方向
     * @return EdgeLab的配置
     */
    List<EdgeLab> getConfigs(String edge);

    /**
     * 根据edge获取EdgeLab的配置
     *
     * @param item 具体的位置
     * @return EdgeLab的配置
     */
    EdgeLab getConfig(String edge);

    /**
     * 更新EdgeLab的配置
     *
     * @param config EdgeLab的配置
     */
    void insertOrUpdateConfig(EdgeLab config);

    /**
     * 删除Edge的配置
     *
     * @param config EdgeLab的配置
     */
    void deleteConfig(EdgeLab config);

    /**
     * 删除所以Edge的配置
     */
    void deleteAll();

    /**
     * 初始化
     */
    void initConfig();

}
