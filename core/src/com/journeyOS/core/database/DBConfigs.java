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

package com.journeyOS.core.database;

public class DBConfigs {
    //database name
    public static final String DB_NAME = "edge";

    public static final int DB_VERSION = 3;

    //table
    public static final String EDGE_TABLE = "edge";
    //column
    public static final String EDGE_ITEM = "item";
    //column
    public static final String EDGE_PACKAGE_NAME = "packageName";
    //column
    public static final String EDGE_DIRECTION = "direction";

    //table
    public static final String BALL_TABLE = "ball";
    //column
    public static final String BALL_ORIENTATION = "orientation";
    //column
    public static final String BALL_LAYOUT_X = "x";
    //column
    public static final String BALL_LAYOUT_Y = "y";

    //table
    public static final String CITY_TABLE = "city";
    //column
    public static final String CITY_ID = "cityId";
    //column
    public static final String COUNTRY = "country";
    //column
    public static final String COUNTRY_EN = "countryEn";
    //column
    public static final String CITY_NAME = "cityName";
    //column
    public static final String PROVINCE = "province";
    //column
    public static final String PROVINCE_EN = "provinceEn";
    //column
    public static final String LONGITUDE = "longitude";
    //column
    public static final String LATITUDE = "latitude";

    //table
    public static final String WEATHER_TABLE = "weather";
    //column
    public static final String WEATHER_CITY = "cityId";
    //column
    public static final String WEATHER_WEATHER = "weather";
    //column
    public static final String WEATHER_AIR = "air";
    //column
    public static final String WEATHER_TIME = "time";

}
