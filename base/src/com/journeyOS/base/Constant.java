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

package com.journeyOS.base;

public class Constant {
    public static final boolean DEBUG = true;

    public static final String SEPARATOR = "#";
    public static final int LENGTH = 4;
    //update weather time interval
    public static final long TIME_INTERVAL = 4 * 60 * 60 * 1000;

    public static final String DEFAULT_CITY = "CN101020100";

    //SharedPreferences KEY
    public static final String DAEMON = "daemon";
    public static final String BALL = "ball";
    public static final String DB_INITED = "dbInited";
    public static final String CITY_INITED = "cityInited";
    public static final String CITY_ID = "cityId";
    public static final String AUTO_SYNC = "autoSync";

    //menu key
    public static final int MENU_USER = 0;
    public static final int MENU_SETTINGS = 1;
    public static final int MENU_PERMISSION = 2;
    public static final int MENU_ABOUT = 3;


    public static final String USER = "Solo";
    public static final String EMAIL = "anqi.huang@outlook.com";

    public static final String DEFAULT_PASSWORD = "123456";
}
