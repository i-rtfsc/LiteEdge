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

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.journeyOS.core.database.app.App;
import com.journeyOS.core.database.app.AppDao;
import com.journeyOS.core.database.ball.Ball;
import com.journeyOS.core.database.ball.BallDao;
import com.journeyOS.core.database.city.City;
import com.journeyOS.core.database.city.CityDao;
import com.journeyOS.core.database.edge.Edge;
import com.journeyOS.core.database.edge.EdgeDao;
import com.journeyOS.core.database.edgelab.EdgeLab;
import com.journeyOS.core.database.edgelab.EdgeLabDao;
import com.journeyOS.core.database.gesture.Gesture;
import com.journeyOS.core.database.gesture.GestureDao;
import com.journeyOS.core.database.user.User;
import com.journeyOS.core.database.user.UserDao;
import com.journeyOS.core.database.weather.Weather;
import com.journeyOS.core.database.weather.WeatherDao;

@Database(entities = {Edge.class, Ball.class, City.class, Weather.class, User.class, App.class, EdgeLab.class, Gesture.class}, version = DBConfigs.DB_VERSION, exportSchema = false)
public abstract class EdgeDatabase extends RoomDatabase {
    public abstract EdgeDao edgeDao();

    public abstract EdgeLabDao edgeLabDao();

    public abstract BallDao ballDao();

    public abstract CityDao cityDao();

    public abstract WeatherDao weatherDao();

    public abstract UserDao userDao();

    public abstract AppDao appDao();

    public abstract GestureDao gestureDao();
}
