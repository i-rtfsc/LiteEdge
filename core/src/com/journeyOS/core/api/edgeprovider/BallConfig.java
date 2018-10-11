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

package com.journeyOS.core.api.edgeprovider;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

@Entity(tableName = DBConfigs.BALL_TABLE, primaryKeys = {DBConfigs.BALL_ORIENTATION})
public class BallConfig {

    @NonNull
    @ColumnInfo(name = DBConfigs.BALL_ORIENTATION)
    public int orientation;

    @ColumnInfo(name = DBConfigs.BALL_LAYOUT_X)
    public int layoutX;

    @ColumnInfo(name = DBConfigs.BALL_LAYOUT_Y)
    public int layoutY;

}
