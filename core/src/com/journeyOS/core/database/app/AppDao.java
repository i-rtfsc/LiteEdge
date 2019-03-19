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

package com.journeyOS.core.database.app;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.journeyOS.core.database.DBConfigs;

import java.util.List;

@Dao
public interface AppDao {

    @Query("SELECT * FROM " + DBConfigs.APP_TABLE)
    List<App> getApps();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(App app);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<App> apps);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void update(App app);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void update(List<App> apps);

    @Delete
    void delete(App apps);

    @Delete
    void delete(List<App> apps);

    @Query("SELECT * FROM " + DBConfigs.APP_TABLE + " WHERE packageName LIKE :packageName LIMIT 1")
    App getApp(String packageName);
}
