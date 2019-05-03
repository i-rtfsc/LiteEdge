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

package com.journeyOS.barrage.control.speed;


public final class SpeedController implements ISpeedController {

    private static float MAX_SPEED = 3.5f;

    private static float MIN_SPEED = 8.5f;

    private float width;

    private float speed;

    @Override
    public void setWidthPixels(int width) {
        this.width = width;
    }

    @Override
    public void setSpeed(int speed) {
        this.speed = speed;
    }

    @Override
    public float getSpeed() {
        return speed;
        //return (float) (((Math.random() * (MAX_SPEED - MIN_SPEED) + MIN_SPEED)) / RATE) * width;
    }

    public float getMaxSpeed() {
        return MAX_SPEED;
    }

    public float getMinSpeed() {
        return MIN_SPEED;
    }


}
