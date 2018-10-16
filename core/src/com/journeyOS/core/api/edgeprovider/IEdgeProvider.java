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
import com.journeyOS.core.database.edge.Edge;
import com.journeyOS.core.type.EdgeDirection;

import java.util.List;

public interface IEdgeProvider extends ICoreApi {
    /**
     * 获取全部Edge的配置
     *
     * @return 获取全部Edge的配置
     */
    List<Edge> getConfigs();

    /**
     * 根据方向获取Edge的配置
     *
     * @param direction 方向
     * @return Edge的配置
     */
    List<Edge> getConfigs(String direction);

    /**
     * 根据item获取Edge的配置
     *
     * @param item 具体的位置
     * @return Edge的配置
     */
    Edge getConfig(String item);

    /**
     * 更新Edge的配置
     *
     * @param config Edge的配置
     */
    void insertOrUpdateConfig(Edge config);

    /**
     * 删除Edge的配置
     *
     * @param config Edge的配置
     */
    void deleteConfig(Edge config);

    /**
     * 删除所以Edge的配置
     */
    void deleteAll();

    /**
     * 初始化
     */
    void initConfig();

    /**
     * 非数据库操作，EdgeDirection+postion转化成item
     *
     * @param direction edge方向
     * @param postion   egde中item的位置
     * @return item
     */
    String encodeItem(EdgeDirection direction, int postion);

    /**
     * 通过item获取位置
     *
     * @param item
     * @return
     */
    int getPostion(String item);
}
