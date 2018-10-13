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

package com.journeyOS.core.database.city;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.journeyOS.core.database.DBConfigs;

@Entity(tableName = DBConfigs.CITY_TABLE)
public class City {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = DBConfigs.CITY_ID)
    public String cityId;

    @ColumnInfo(name = DBConfigs.COUNTRY)
    public String country;

    @ColumnInfo(name = DBConfigs.COUNTRY_EN)
    public String countryEn;

    @ColumnInfo(name = DBConfigs.CITY_NAME)
    public String cityName;

    @ColumnInfo(name = DBConfigs.PROVINCE)
    public String province;

    @ColumnInfo(name = DBConfigs.PROVINCE_EN)
    public String provinceEn;

    @ColumnInfo(name = DBConfigs.LONGITUDE)
    public String longitude;

    @ColumnInfo(name = DBConfigs.LATITUDE)
    public String latitude;
}
