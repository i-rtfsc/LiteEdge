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

package com.journeyOS.core.api.edgeprovider;

import com.journeyOS.core.api.ICoreApi;
import com.journeyOS.core.database.city.City;

import java.util.List;

public interface ICityProvider extends ICoreApi {

    /**
     * 查询全部城市
     *
     * @return
     */
    List<City> queryAllCities();

    /**
     * 通过城市id查询城市
     *
     * @param cityId 城市id
     * @return 城市
     */
    City searchCity(String cityId);

    /**
     * 通过城市名称、国家查询城市
     *
     * @param cityName 城市名称
     * @param county   国家
     * @return 城市
     */
    City searchCity(String cityName, final String county);

    /**
     * 匹配城市
     *
     * @param keyword 城市关键字
     * @return 是否匹配
     */
    List<City> matchingCity(final String keyword);

    /**
     * 初始化城市
     */
    void loadCitys();

    /**
     * 保存当前城市ID到sp中
     *
     * @param cityId 城市ID
     */
    void saveCity(String cityId);

    /**
     * 删除sp的城市id
     *
     * @param cityId
     */
    void deleteCity(String cityId);

    /**
     * 获取sp中的城市ID
     *
     * @return 城市ID
     */
    String getCity();
}
