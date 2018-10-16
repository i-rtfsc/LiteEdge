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

package com.journeyOS.core.database.weather;

import com.journeyOS.core.CoreManager;
import com.journeyOS.core.api.edgeprovider.IWeatherProvider;
import com.journeyOS.core.database.DBConfigs;
import com.journeyOS.core.database.EdgeDatabase;
import com.journeyOS.core.repository.DBHelper;
import com.journeyOS.literouter.annotation.ARouterInject;

@ARouterInject(api = IWeatherProvider.class)
public class WeatherRepositoryImpl implements IWeatherProvider {
    private static final String TAG = WeatherRepositoryImpl.class.getSimpleName();
    private WeatherDao weatherDao;
    private Object mLock = new Object();

    @Override
    public void onCreate() {
        EdgeDatabase database = DBHelper.provider(CoreManager.getDefault().getContext(), EdgeDatabase.class, DBConfigs.DB_NAME);
        weatherDao = database.weatherDao();
    }

    @Override
    public Weather getWeather(String cityId) {
        synchronized (mLock) {
            return weatherDao.searchWeather(cityId);
        }
    }

    @Override
    public void saveWeather(Weather weather) {
        synchronized (mLock) {
            weatherDao.insertWeather(weather);
        }
    }
}
