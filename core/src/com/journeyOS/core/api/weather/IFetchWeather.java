package com.journeyOS.core.api.weather;

import com.journeyOS.core.api.ICoreApi;
import com.journeyOS.core.weather.Air;
import com.journeyOS.core.weather.Weather;

public interface IFetchWeather extends ICoreApi {
    /**
     * 查询天气
     *
     * @param city         城市id
     * @param forceNetWork 是否强制用网络查询
     *                     true 强制用网络查询
     *                     false 优先用数据库查询，但是如果数据库中的数据与当前间隔大于4小时，则用网络查询。
     * @return
     */
    Weather queryWeather(String city, boolean forceNetWork);

    /**
     * 查询空气质量
     *
     * @param city         城市id
     * @param forceNetWork 是否强制用网络查询
     *                     true 强制用网络查询
     *                     false 优先用数据库查询，但是如果数据库中的数据与当前间隔大于4小时，则用网络查询。
     * @return
     */
    Air queryAir(String city, boolean forceNetWork);
}
