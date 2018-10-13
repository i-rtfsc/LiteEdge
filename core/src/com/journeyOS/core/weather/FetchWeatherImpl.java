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

import com.journeyOS.base.utils.Base64Util;
import com.journeyOS.core.AppHttpClient;
import com.journeyOS.core.api.weather.IFetchWeatherApi;
import com.journeyOS.literouter.annotation.ARouterInject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;

@ARouterInject(api = IFetchWeatherApi.class)
public class FetchWeatherImpl implements IFetchWeatherApi {
    private static final String TAG = FetchWeatherImpl.class.getSimpleName();
    private static final String WEATHER_KEY = "OGFlZWM3NzAxNzcyNGI1MThhNWYwYmE1ZDE4ODg4MjA";

    private WeatherServiceApi mWsa;

    @Override
    public void onCreate() {
        mWsa = AppHttpClient.getDefault().getService(WeatherServiceApi.class);
    }

    @Override
    public Weather queryWeather(String city) {
        try {
            Call<Weather> weatherCall = mWsa.getWeather(Base64Util.fromBase64(WEATHER_KEY), city);
            Response<Weather> response = weatherCall.execute();
            if (response.isSuccessful()) {
                Weather weather = response.body();
                return weather;
            } else {

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
