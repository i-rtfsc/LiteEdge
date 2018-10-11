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

package com.journeyOS.base.widget;

public class OutterBall extends Ball {

    public OutterBall(int centerX, int centerY, int parentRadius, float sizeRate) {
        super(centerX, centerY, parentRadius, sizeRate);
    }

    @Override
    protected void calculateCenter(float fraction) {
//        float rate = fraction;
//        mCenter.x = (int) (mParentCenter.x + mRadius * rate);
    }

    @Override
    protected void calculateRightRadius(float fraction) {
        float rate = 1.0f + fraction * 0.05f;
        mRightRadius = (int) (mOriginRadius * rate);
    }

    @Override
    protected void calculateLeftRadius(float fraction) {
        float rate = 1.0f - fraction * 0.05f;
        mLeftRadius = (int) (mOriginRadius * rate);
    }

    @Override
    protected void calculateRadius(float fraction) {
        float rate = 1.0f - fraction * 0.1f;
        mRadius = (int) (mOriginRadius * rate);
    }
}