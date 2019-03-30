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

package com.journeyOS.core.database.gesture;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

import com.journeyOS.core.database.DBConfigs;

@Entity(tableName = DBConfigs.GESTURE_TABLE, primaryKeys = {DBConfigs.GESTURE_DIRECTION})
public class Gesture {

    @NonNull
    @ColumnInfo(name = DBConfigs.GESTURE_DIRECTION)
    public String gestureDirection = "";

    @ColumnInfo(name = DBConfigs.GESTURE_ORIENTATION)
    public int orientation = 0;

    @ColumnInfo(name = DBConfigs.GESTURE_TYPE)
    public String type;

    @ColumnInfo(name = DBConfigs.GESTURE_ACTION)
    public String action;

    @ColumnInfo(name = DBConfigs.GESTURE_COMMENT)
    public String comment;

    @Override
    public String toString() {
        return "Gesture{" +
                "gestureDirection='" + gestureDirection + '\'' +
                ", orientation=" + orientation +
                ", type='" + type + '\'' +
                ", action='" + action + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }
}
