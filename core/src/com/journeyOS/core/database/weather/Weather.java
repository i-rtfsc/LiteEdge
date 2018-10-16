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

package com.journeyOS.core.database.weather;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

import com.journeyOS.core.database.DBConfigs;

@Entity(tableName = DBConfigs.WEATHER_TABLE, primaryKeys = {DBConfigs.WEATHER_CITY})
public class Weather {

    @NonNull
    @ColumnInfo(name = DBConfigs.WEATHER_CITY)
    public String cityId;

    @ColumnInfo(name = DBConfigs.WEATHER_WEATHER)
    public String weather;

    @ColumnInfo(name = DBConfigs.WEATHER_AIR)
    public String air;

    /**
     * 实际上是long类型
     */
    @ColumnInfo(name = DBConfigs.WEATHER_TIME)
    public String time;
}
