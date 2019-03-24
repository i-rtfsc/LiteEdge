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

package com.journeyOS.core.database.edgelab;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

import com.journeyOS.core.database.DBConfigs;

@Entity(tableName = DBConfigs.EDGE_LAB_TABLE, primaryKeys = {DBConfigs.EDGE_LAB_EDGE})
public class EdgeLab {
    /**
     * edge(left,right,up)
     */
    @NonNull
    @ColumnInfo(name = DBConfigs.EDGE_LAB_EDGE)
    public String edge = "";

    /**
     * start,end
     */
    @ColumnInfo(name = DBConfigs.EDGE_LAB_GRAVITY)
    public int gravity;

    /**
     * v,h
     */
    @ColumnInfo(name = DBConfigs.EDGE_LAB_ORIENTATION)
    public int orientation;

    /**
     * 弧度
     */
    @ColumnInfo(name = DBConfigs.EDGE_LAB_RADIUS)
    public int radius;

    /**
     * 弧度
     */
    @ColumnInfo(name = DBConfigs.EDGE_LAB_PEEK)
    public int peek;

    /**
     * 弧度
     */
    @ColumnInfo(name = DBConfigs.EDGE_LAB_ROTATE)
    public int rotate;

}
