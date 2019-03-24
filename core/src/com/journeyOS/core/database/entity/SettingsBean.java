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

package com.journeyOS.core.database.entity;

import com.journeyOS.base.Constant;

import java.util.List;

public class SettingsBean {

    public boolean autoSync = Constant.AUTO_SYNC_DEFAULT;

    public boolean daemon = Constant.DAEMON_DEFAULT;

    public boolean ball = Constant.BALL_DEFAULT;

    public boolean barrage = Constant.BARRAGE_DEFAULT;

    public boolean edgeLabDebug = Constant.EDGE_LAB_DEBUG_DEFAULT;

    public int edgeCount = Constant.EDGE_CONUT_DEFAULT;

    public boolean edgeItemText = Constant.EDGE_ITEM_TEXT_DEFAULT;

    public List<EdgeLab> edgeLabs;

    public static class EdgeLab {

        public String edge;

        public int radius;

        public int peek;
    }
}
