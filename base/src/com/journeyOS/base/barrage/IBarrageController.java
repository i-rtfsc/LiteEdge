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

package com.journeyOS.base.barrage;

public interface IBarrageController {

    /**
     * 设置弹幕速度
     */
    void setSpeed(float speed);

    /**
     * 获取弹幕速度
     */
    float getSpeed();

    /**
     * 设置弹幕移动方向
     */
    void setDirection(int direction);

    /**
     * 获取弹幕移动方向
     */
    int getDirection();

    /**
     * 设置弹幕位置
     */
    void setPostion(int postion);

    /**
     * 获取弹幕位置
     */
    int getPostion();

    /**
     * 设置头像大小
     */
    void setAvatarSize(int avatatSize);

    /**
     * 获取头像大小
     */
    int getAvatarSize();

    /**
     * 设置字体大小
     */
    void setTextSize(int textSize);

    /**
     * 获取字体大小
     */
    int getTextSize();

    /**
     * 设置标题字体颜色
     */
    void setTextTitleColor(int titleColor);

    /**
     * 获取标题字体颜色
     */
    int getTextTitleColor();

    /**
     * 设置内容字体颜色
     */
    void setTextContentColor(int contentColor);

    /**
     * 获取内容字体颜色
     */
    int getTextContentColor();

    /**
     * 设置背景颜色
     */
    void setBackgroundColor(int backgroundColor);

    /**
     * 获取背景颜色
     */
    int getBackgroundColor();

    /**
     * 设置背景四个角度
     */
    void setBackgroundRadius(float[] radii);

    /**
     * 获取背景四个角度
     */
    float[] getBackgroundRadius();
}
