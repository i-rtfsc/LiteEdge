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

public final class BarrageController implements IBarrageController {

    private float mSpeed = 80;
    private int mDirection = BarrageModel.RIGHT_TO_LEFT;
    private int mPostion = BarrageModel.POSTION_TOP;
    private int mAvatarSize = 68;
    private int mTextSize = 15;
    private int mTitleColor = -65427;
    private int mContentColor = -7773953;
    private int mBackgroundColor = 811885668;

    @Override
    public void setSpeed(float speed) {
        mSpeed = speed;
    }

    @Override
    public float getSpeed() {
        return mSpeed;
    }

    @Override
    public void setDirection(int direction) {
        mDirection = direction;
    }

    @Override
    public int getDirection() {
        return mDirection;
    }

    @Override
    public void setPostion(int postion) {
        mPostion = postion;
    }

    @Override
    public int getPostion() {
        return mPostion;
    }

    @Override
    public void setAvatarSize(int avatatSize) {
        mAvatarSize = avatatSize;
    }

    @Override
    public int getAvatarSize() {
        return mAvatarSize;
    }

    @Override
    public void setTextSize(int textSize) {
        mTextSize = textSize;
    }

    @Override
    public int getTextSize() {
        return mTextSize;
    }

    @Override
    public void setTextTitleColor(int titleColor) {
        mTitleColor = titleColor;
    }

    @Override
    public int getTextTitleColor() {
        return mTitleColor;
    }

    @Override
    public void setTextContentColor(int contentColor) {
        mContentColor = contentColor;
    }

    @Override
    public int getTextContentColor() {
        return mContentColor;
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        mBackgroundColor = backgroundColor;
    }

    @Override
    public int getBackgroundColor() {
        return mBackgroundColor;
    }
}
