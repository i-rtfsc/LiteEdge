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

package com.journeyOS.core.database.gesture;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.journeyOS.core.database.DBConfigs;

import java.util.List;

@Dao
public interface GestureDao {

    @Query("SELECT * FROM " + DBConfigs.GESTURE_TABLE)
    List<Gesture> getConfigs();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Gesture> configs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Gesture config);

    @Query("SELECT * FROM " + DBConfigs.GESTURE_TABLE + " WHERE " + DBConfigs.GESTURE_DIRECTION + " LIKE :direction  LIMIT 1")
    Gesture searchConfig(String direction);

    @Query("SELECT * FROM " + DBConfigs.GESTURE_TABLE + " WHERE " + DBConfigs.GESTURE_ORIENTATION + " LIKE :orientation")
    List<Gesture> searchConfig(int orientation);

    @Delete
    void delete(Gesture config);

    @Query("DELETE FROM " + DBConfigs.BALL_TABLE)
    void deleteAll();

}
