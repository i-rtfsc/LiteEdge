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

import android.graphics.Bitmap;

public class BarrageModel {
    public final static int RIGHT_TO_LEFT = 1;
    public final static int LEFT_TO_RIGHT = 2;

    public final static int POSTION_FULL = 1;
    public final static int POSTION_TOP = 2;
    public final static int POSTION_MIDDLE = 3;
    public final static int POSTION_BOTTOM = 4;

    public Bitmap avatar;
    public String title;
    public String content;
    public BarrageController controller;
}
