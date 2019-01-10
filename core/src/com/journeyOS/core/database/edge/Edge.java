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

package com.journeyOS.core.database.edge;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.support.annotation.NonNull;

import com.journeyOS.core.database.DBConfigs;

@Entity(tableName = DBConfigs.EDGE_TABLE, primaryKeys = {DBConfigs.EDGE_ITEM})
public class Edge {
    /**
     * direction+"#"+postion
     */
    @NonNull
    @ColumnInfo(name = DBConfigs.EDGE_ITEM)
    public String item = "";

    /**
     * app包名
     */
    @ColumnInfo(name = DBConfigs.EDGE_PACKAGE_NAME)
    public String packageName;

    /**
     * 哪个edge(left,right,up)
     */
    @ColumnInfo(name = DBConfigs.EDGE_DIRECTION)
    public String direction;


    @Override
    public String toString() {
        return "Edge{" +
                "item='" + item + '\'' +
                ", packageName='" + packageName + '\'' +
                ", direction='" + direction + '\'' +
                '}';
    }
}
