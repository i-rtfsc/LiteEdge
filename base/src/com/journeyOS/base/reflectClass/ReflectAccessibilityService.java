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

package com.journeyOS.base.reflectClass;

import com.journeyOS.base.reflectUtils.FieldUtils;

public class ReflectAccessibilityService {
    public static final int GLOBAL_ACTION_LOCK_SCREEN;
    public static final int GLOBAL_ACTION_TAKE_SCREENSHOT;

    static {
        GLOBAL_ACTION_LOCK_SCREEN = getLockScreen();
        GLOBAL_ACTION_TAKE_SCREENSHOT = getTakeScreenShot();
    }

    private static int getLockScreen() {
        int lock = 8;
        try {
            Class aClass = Class.forName("android.accessibilityservice.AccessibilityService");
            lock = (int) FieldUtils.readStaticField(aClass, "GLOBAL_ACTION_LOCK_SCREEN");
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return lock;
    }

    private static int getTakeScreenShot() {
        int screenshot = 9;
        try {
            Class aClass = Class.forName("android.accessibilityservice.AccessibilityService");
            screenshot = (int) FieldUtils.readStaticField(aClass, "GLOBAL_ACTION_TAKE_SCREENSHOT");
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return screenshot;
    }

}
