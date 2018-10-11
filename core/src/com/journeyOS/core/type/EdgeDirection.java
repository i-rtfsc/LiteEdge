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

public enum EdgeDirection {
    LEFT(1),
    RIGHT(2),
    UP(3),
    NONE(-1);

    private int value;
    private static Map map = new HashMap<>();

    private EdgeDirection(int value) {
        this.value = value;
    }

    static {
        for (EdgeDirection edgeDirection : EdgeDirection.values()) {
            map.put(edgeDirection.value, edgeDirection);
        }
    }

    public static EdgeDirection valueOf(int direction) {
        return (EdgeDirection) map.get(direction);
    }

    public int getValue() {
        return value;
    }
}
