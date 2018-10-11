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

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.journeyOS.core.api.edgeprovider.DBConfigs;
import com.journeyOS.core.api.edgeprovider.EdgeConfig;

import java.util.List;

@Dao
public interface EdgeDao {

    @Query("SELECT * FROM " + DBConfigs.EDGE_TABLE)
    List<EdgeConfig> getConfigs();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<EdgeConfig> configs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(EdgeConfig config);

    @Query("SELECT * FROM " + DBConfigs.EDGE_TABLE + " WHERE " + DBConfigs.EDGE_ITEM + " LIKE :item LIMIT 1")
    EdgeConfig searchConfig(String item);

    @Query("SELECT * FROM " + DBConfigs.EDGE_TABLE + " WHERE " + DBConfigs.EDGE_DIRECTION + " LIKE :direction LIMIT 6")
    List<EdgeConfig> findConfig(String direction);

    @Delete
    void delete(EdgeConfig config);

    @Query("DELETE FROM " + DBConfigs.EDGE_TABLE)
    void deleteAll();

}
