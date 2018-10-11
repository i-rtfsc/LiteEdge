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

public class DBConfigs {
    //database name
    public static final String DB_NAME = "edge";

    public static final int DB_VERSION = 1;

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

}
