package com.journeyOS.core.weather;

import com.journeyOS.base.utils.JsonHelper;
import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.core.api.weather.IFetchWeatherApi;
import com.journeyOS.literouter.annotation.ARouterInject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@ARouterInject(api = IFetchWeatherApi.class)
public class FetchWeatherImpl implements IFetchWeatherApi {
    private static final String TAG = FetchWeatherImpl.class.getSimpleName();
    private static final String SECRET_KEY = "4ayrycuognrqhdmu";
    //https://api.seniverse.com/v3/weather/now.json?key=4ayrycuognrqhdmu&location=shanghai&language=zh-Hans&unit=c
    private static final String WEATHER_URL = "https://api.seniverse.com/v3/weather/daily.json?key=" + SECRET_KEY;
    private static final String DAY = "1";

    @Override
    public void onCreate() {

    }

    @Override
    public Weather queryWeather(String city) {
        String json = getWeather(city, DAY);
        Weather weather = JsonHelper.fromJson(json, Weather.class);
        if (weather != null) LogUtils.d(TAG, " weather = " + weather.toString());
        return weather;
    }


    public static String getWeather(String... params) {
        StringBuilder weatherUrl = new StringBuilder();
        weatherUrl.append(WEATHER_URL).append("&location=").append(params[0]).append("&language=zh-Hans&unit=c&start=0").append("&days=").append(params[1]);

        InputStream inputStream = null;

        HttpURLConnection urlConnection = null;

        String result = null;
        try {
            /* forming th java.net.URL object */
            URL url = new URL(weatherUrl.toString());

            urlConnection = (HttpURLConnection) url.openConnection();

            /* optional request header */
            urlConnection.setRequestProperty("Content-Type", "application/json");

            /* optional request header */
            urlConnection.setRequestProperty("Accept", "application/json");

            /* for Get request */
            urlConnection.setRequestMethod("GET");

            int statusCode = urlConnection.getResponseCode();

            /* 200 represents HTTP OK */
            if (statusCode == 200) {
                inputStream = new BufferedInputStream(urlConnection.getInputStream());
                result = convertInputStreamToString(inputStream);
                LogUtils.d("result", "get weather success, json = " + result);
            } else {
                result = null; //"Failed to fetch data!";
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result == null ? null : result.substring(0, result.length() - 2);
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null) {
            result += line;
        }
        /* Close Stream */
        if (null != inputStream) {
            inputStream.close();
        }

        return result.substring(12);
    }
}
