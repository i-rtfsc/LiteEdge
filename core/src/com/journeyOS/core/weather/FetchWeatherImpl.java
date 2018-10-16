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

package com.journeyOS.core.weather;

import com.journeyOS.base.Constant;
import com.journeyOS.base.utils.Base64Util;
import com.journeyOS.base.utils.JsonHelper;
import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.core.AppHttpClient;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.api.edgeprovider.IWeatherProvider;
import com.journeyOS.core.api.thread.ICoreExecutors;
import com.journeyOS.core.api.weather.IFetchWeather;
import com.journeyOS.literouter.annotation.ARouterInject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

@ARouterInject(api = IFetchWeather.class)
public class FetchWeatherImpl implements IFetchWeather {
    private static final String TAG = FetchWeatherImpl.class.getSimpleName();
    private static final String WEATHER_KEY = "OGFlZWM3NzAxNzcyNGI1MThhNWYwYmE1ZDE4ODg4MjA";

    private WeatherServiceApi mWsa;

    @Override
    public void onCreate() {
        mWsa = AppHttpClient.getDefault().getService(WeatherServiceApi.class);
    }

    @Override
    public Weather queryWeather(final String city, boolean forceNetWork) {
        if (!forceNetWork) {
            com.journeyOS.core.database.weather.Weather weatherDb = CoreManager.getDefault().getImpl(IWeatherProvider.class).getWeather(city);
            if (weatherDb != null) {
                long time = Long.parseLong(weatherDb.time);
                if ((System.currentTimeMillis() - Constant.TIME_INTERVAL) <= time) {
                    String config = weatherDb.weather;
                    if (config != null) {
                        LogUtils.d(TAG, "get weather from database.");
                        return JsonHelper.fromJson(Base64Util.fromBase64(config), Weather.class);
                    }
                }
            }
        }

        try {
            LogUtils.d(TAG, "get weather from network.");
            Call<Weather> weatherCall = mWsa.getWeather(Base64Util.fromBase64(WEATHER_KEY), city);
            Response<Weather> response = weatherCall.execute();
            if (response.isSuccessful()) {
                final Weather weather = response.body();

                //save to databse
                CoreManager.getDefault().getImpl(ICoreExecutors.class).diskIOThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        com.journeyOS.core.database.weather.Weather weatherDb = CoreManager.getDefault().getImpl(IWeatherProvider.class).getWeather(city);
                        if (weatherDb == null) {
                            weatherDb = new com.journeyOS.core.database.weather.Weather();
                            weatherDb.cityId = city;
                        }
                        weatherDb.weather = Base64Util.toBase64(JsonHelper.toJson(weather));
                        weatherDb.time = String.valueOf(System.currentTimeMillis());
                        CoreManager.getDefault().getImpl(IWeatherProvider.class).saveWeather(weatherDb);
                    }
                });

                return weather;
            } else {

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Air queryAir(final String city, boolean forceNetWork) {
        if (!forceNetWork) {
            com.journeyOS.core.database.weather.Weather weatherDb = CoreManager.getDefault().getImpl(IWeatherProvider.class).getWeather(city);
            if (weatherDb != null) {
                long time = Long.parseLong(weatherDb.time);
                if ((System.currentTimeMillis() - Constant.TIME_INTERVAL) <= time) {
                    String config = weatherDb.air;
                    if (config != null) {
                        LogUtils.d(TAG, "get air weather from database.");
                        return JsonHelper.fromJson(Base64Util.fromBase64(config), Air.class);
                    }
                }
            }
        }

        try {
            LogUtils.d(TAG, "get air weather from network.");
            Call<Air> airCall = mWsa.getAqi(Base64Util.fromBase64(WEATHER_KEY), city);
            Response<Air> response = airCall.execute();
            if (response.isSuccessful()) {
                final Air air = response.body();

                //save to databse
                CoreManager.getDefault().getImpl(ICoreExecutors.class).diskIOThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        com.journeyOS.core.database.weather.Weather weatherDb = CoreManager.getDefault().getImpl(IWeatherProvider.class).getWeather(city);
                        if (weatherDb == null) {
                            weatherDb = new com.journeyOS.core.database.weather.Weather();
                            weatherDb.cityId = city;
                        }
                        weatherDb.air = Base64Util.toBase64(JsonHelper.toJson(air));
                        weatherDb.time = String.valueOf(System.currentTimeMillis());
                        CoreManager.getDefault().getImpl(IWeatherProvider.class).saveWeather(weatherDb);
                    }
                });

                return air;
            } else {

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
