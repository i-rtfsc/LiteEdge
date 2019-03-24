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

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.journeyOS.core.database.DBConfigs;

import java.util.List;

@Dao
public interface EdgeLabDao {

    @Query("SELECT * FROM " + DBConfigs.EDGE_LAB_TABLE)
    List<EdgeLab> getConfigs();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<EdgeLab> configs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(EdgeLab config);

    @Query("SELECT * FROM " + DBConfigs.EDGE_LAB_TABLE + " WHERE " + DBConfigs.EDGE_LAB_EDGE + " LIKE :edge LIMIT 1")
    EdgeLab searchConfig(String edge);

    @Query("SELECT * FROM " + DBConfigs.EDGE_LAB_TABLE + " WHERE " + DBConfigs.EDGE_LAB_EDGE + " LIKE :direction LIMIT :limit")
    List<EdgeLab> searchConfig(String direction, int limit);

    @Delete
    void delete(EdgeLab config);

    @Query("DELETE FROM " + DBConfigs.EDGE_LAB_TABLE)
    void deleteAll();

}
