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

    //SharedPreferences file(icon)
    public static final boolean LOCAL_ICON = false;
    public static final String SP_BIG_FILE = "big";
    public static final String BIG_FILE_USER_ICON = "userIcon";

    //SharedPreferences KEY & default value
    public static final String DAEMON = "daemon";
    public static final boolean DAEMON_DEFAULT = true;

    public static final String EXCLUDE = "exclude";
    public static final boolean EXCLUDE_DEFAULT = false;

    public static final String BALL = "ball";
    public static final boolean BALL_DEFAULT = false;

    public static final String BALL_SIZE = "ballSize";
    public static final int BALL_SIZE_DEFAULT = 175;

    public static final String CITY_ID = "cityId";
    public static final String CITY_ID_DEFAULT = "CN101020100";

    public static final String AUTO_SYNC = "autoSync";
    public static final boolean AUTO_SYNC_DEFAULT = true;

    public static final String BARRAGE = "barrage";
    public static final boolean BARRAGE_DEFAULT = true;

    public static final String USE_CACHE = "useCache";
    public static final boolean USE_CACHE_DEFAULT = true;

    public static final String BARRAGE_ICONO = "barrageIcon";
    public static final boolean BARRAGE_ICONO_DEFAULT = false;

    public static final String EDGE_CONUT = "edgeCount";
    public static final int EDGE_CONUT_DEFAULT = 7;

    public static final String BARRAGE_CLICK = "barrageClick";
    public static final int BARRAGE_CLICK_DEFAULT = 0;

    public static final String INNER_BALL_COLOR = "innerBallColor";
    public static final int INNER_BALL_COLOR_DEFAULT = -16743937;

    public static final String EDGE_ITEM_TEXT = "edgeItemText";
    public static final boolean EDGE_ITEM_TEXT_DEFAULT = false;

    public static final String EDGE_LAB_DEBUG = "edgeLabDebug";
    public static final boolean EDGE_LAB_DEBUG_DEFAULT = true;

    public static final String BARRAGE_TITLE_COLOR = "barrageTitleColor";
    public static final int BARRAGE_TITLE_COLOR_DEFAULT = 0;

    public static final String BARRAGE_SUMMARY_COLOR = "barrageSummaryColor";
    public static final int BARRAGE_SUMMARY_COLOR_DEFAULT = 0;

    public static final String BARRAGE_BACKGROUND_COLOR = "barrageBackgroundColor";
    public static final int BARRAGE_BACKGROUND_COLOR_DEFAULT = 0;

    public static final String BARRAGE_SPEED = "barrageSpeed";
    public static final int BARRAGE_SPEED_DEFAULT = 3;

    public static final String PORTRAIT = "portrait";
    public static final int PORTRAIT_DEFAULT = 4;

    public static final String LANDSCAPE = "landscape";
    public static final int LANDSCAPE_DEFAULT = 4;

    //SharedPreferences KEY & default value
    public static final String MUSIC_CONTROL_SHOW_BARRAGE = "mcsb";
    public static final boolean MUSIC_CONTROL_SHOW_BARRAGE_DEFAULT = false;

    public static final String AUTO_HIDE_BALL = "ahb";
    public static final int AUTO_HIDE_BALL_NONE = 0;
    public static final int AUTO_HIDE_BALL_DEFAULT = 0;
    public static final int AUTO_HIDE_BALL_GAME = 0x01;
    public static final int AUTO_HIDE_BALL_VIDEO = 0x02;

    public static final String DB_INITED = "dbInited";
    public static final String CITY_INITED = "cityInited";
    public static final String EDGE_LAB_INITED = "labInited";
    public static final String APP_INITED = "appInited";
    public static final String GUIDE_INITED = "guide_inited";
    public static final String GESTURE_INITED = "gestureInited";
    public static final String IMAGE_ENGINE = "imageEngine";

    //image engine type
    public static final String IMAGE_ENGINE_PICASSO = "imageEnginePicasso";
    public static final String IMAGE_ENGINE_GLIDE = "imageEngineGlide";

    //menu key
    public static final int MENU_USER = 0;
    public static final int MENU_PERMISSION = 1;
    public static final int MENU_SETTINGS = 2;
    public static final int MENU_BARRAGE = 3;
    public static final int MENU_LAB = 4;
    public static final int MENU_ABOUT = 5;
    public static final int MENU_LEARN = 6;
    public static final int MENU_ADMIN = 7;

    public static final String USER = "Solo";
    public static final String EMAIL = "anqi.huang@outlook.com";
    public static final String PHONE = "15622881738";
    //测试账户，也给管理员权限
    public static final String PHONE_TEST = "12345678901";

    public static final String DEFAULT_PASSWORD = "123456";
}
