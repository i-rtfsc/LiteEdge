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

package com.journeyOS.core.database.ball;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.journeyOS.core.database.DBConfigs;

import java.util.List;

@Dao
public interface BallDao {

    @Query("SELECT * FROM " + DBConfigs.BALL_TABLE)
    List<Ball> getConfigs();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<Ball> configs);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Ball config);

    @Query("SELECT * FROM " + DBConfigs.BALL_TABLE + " WHERE " + DBConfigs.BALL_ORIENTATION + " LIKE :orientation  LIMIT 1")
    Ball searchConfig(int orientation);

    @Delete
    void delete(Ball config);

    @Query("DELETE FROM " + DBConfigs.BALL_TABLE)
    void deleteAll();

}
