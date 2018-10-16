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

package com.journeyOS.core.api.edge;

import com.journeyOS.core.api.ICoreApi;
import com.journeyOS.core.type.EdgeDirection;

public interface IEdge extends ICoreApi {
    /**
     * 显示edge
     *
     * @param direction edge的方向
     *                  1：竖屏的左
     *                  2：竖屏的右
     *                  3：横屏的上
     */
    void showingEdge(int direction);

    /**
     * 延时显示edge
     *
     * @param direction   edge的方向
     *                    1：竖屏的左
     *                    2：竖屏的右
     *                    3：横屏的上
     * @param delayMillis 延迟时间
     */
    void showingEdge(int direction, long delayMillis);

    /**
     * 显示edge
     *
     * @param direction 枚举com.journeyOS.core.type.EdgeDirection
     */
    void showingEdge(EdgeDirection direction);

    /**
     * 延时显示edge
     *
     * @param direction 枚举com.journeyOS.core.type.EdgeDirection
     */
    void showingEdge(EdgeDirection direction, long delayMillis);

    /**
     * 隐藏edge
     */
    void hidingEdge();

    /**
     * 延迟隐藏edge
     *
     * @param delayMillis 延迟时间
     */
    void hidingEdge(long delayMillis);
}
