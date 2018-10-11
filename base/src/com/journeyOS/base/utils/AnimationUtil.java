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

package com.journeyOS.base.utils;

import android.annotation.TargetApi;
import android.os.Build;
import android.view.animation.PathInterpolator;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class AnimationUtil {

    public static PathInterpolator getShowCurveMagneticInterpolator() {
        return new PathInterpolator(0.94f, 0.21f, 0.68f, 0.92f);
    }

    public static PathInterpolator getShowCurveResistanceInterpolator() {
        return new PathInterpolator(0.30f, 0.77f, 0.30f, 0.95f);
    }

    public static PathInterpolator getHideCurveInterpolator() {
        return new PathInterpolator(0.66f, 0.16f, 0.88f, 0.77f);
    }

    public static PathInterpolator getEaseInterpolator() {
        return new PathInterpolator(0.25f, 0.10f, 0.25f, 1.f);
    }

    public static PathInterpolator getEaseInInterpolator() {
        return new PathInterpolator(0.42f, 0.f, 1.f, 1.f);
    }

    public static PathInterpolator getEaseOutInterpolator() {
        return new PathInterpolator(0.f, 0.f, 0.58f, 1.f);
    }

    public static PathInterpolator getEaseInOutInterpolator() {
        return new PathInterpolator(0.42f, 0.f, 0.58f, 1.f);
    }
}
