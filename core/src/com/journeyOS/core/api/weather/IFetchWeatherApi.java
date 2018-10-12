package com.journeyOS.core.api.weather;

import com.journeyOS.core.api.ICoreApi;
import com.journeyOS.core.weather.Weather;

public interface IFetchWeatherApi extends ICoreApi {
    Weather queryWeather(String city);
}
