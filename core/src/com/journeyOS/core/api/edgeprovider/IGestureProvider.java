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
import com.journeyOS.core.database.gesture.Gesture;
import com.journeyOS.core.type.FingerDirection;

import java.util.List;

public interface IGestureProvider extends ICoreApi {

    /**
     * 获取全部手势的配置
     *
     * @return 手势的配置
     */
    List<Gesture> getConfigs();
    /**
     * 获取手势的配置
     *
     * @param direction 手势的方向
     * @return 手势的配置
     */
    Gesture getConfig(String direction);

    /**
     * 获取手势的配置
     *
     * @param orientation 屏幕的方向
     * @return 手势的配置
     */
    List<Gesture> getConfig(int orientation);

    /**
     * 更新手势的配置
     *
     * @param config 手势的配置
     */
    void insertOrUpdateConfig(Gesture config);

    /**
     * 删除手势的配置
     *
     * @param config 手势的配置
     */
    void deleteConfig(Gesture config);

    /**
     * 删除全部悬浮球的配置
     */
    void deleteAll();

    void initConfig();


    /**
     * 非数据库操作，FingerDirection+orientation转化成item
     *
     * @param direction   手势方向
     * @param orientation 屏幕方向
     * @return item
     */
    String encodeItem(FingerDirection direction, int orientation);

    /**
     * @param item:FingerDirection+orientation
     * @return FingerDirection
     */
    String getDirection(String item);

    /**
     * @param item:FingerDirection+orientation
     * @return orientation
     */
    int getOrientation(String item);
}
