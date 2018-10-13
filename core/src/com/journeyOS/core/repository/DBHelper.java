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

package com.journeyOS.core.repository;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;

public class DBHelper {

    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull final SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE city (id INTEGER NOT NULL, cityId TEXT, country TEXT, countryEn TEXT, cityName TEXT, province TEXT, provinceEn TEXT, longitude TEXT, latitude TEXT, PRIMARY KEY(id))");
        }
    };

    public static <T extends RoomDatabase> T provider(Context context, Class<T> dbCls, String dbName) {
        return Room.databaseBuilder(context, dbCls, dbName)
                .addMigrations(MIGRATION_1_2)
                .fallbackToDestructiveMigration().build();
    }
}
