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

package com.journeyOS.core.type;

import java.util.HashMap;
import java.util.Map;

public enum FingerDirection {
    UP(1),
    LONG_UP(2),
    DOWN(3),
    LONG_DOWN(4),
    LEFT(5),
    LONG_LEFT(6),
    RIGHT(7),
    LONG_RIGHT(8),
    CLICK(9),
    LONG_PRESS(10),
    NONE(-1);

    private int value;
    private static Map map = new HashMap<>();

    private FingerDirection(int value) {
        this.value = value;
    }

    static {
        for (FingerDirection direction : FingerDirection.values()) {
            map.put(direction.value, direction);
        }
    }

    public static FingerDirection valueOf(int direction) {
        return (FingerDirection) map.get(direction);
    }

    public int getValue() {
        return value;
    }
}
