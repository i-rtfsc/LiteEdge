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

package com.journeyOS.core.database.city;

import com.journeyOS.base.Constant;
import com.journeyOS.base.persistence.SpUtils;
import com.journeyOS.base.utils.FileIOUtils;
import com.journeyOS.base.utils.JsonHelper;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.api.edgeprovider.ICityProvider;
import com.journeyOS.core.database.DBConfigs;
import com.journeyOS.core.database.EdgeDatabase;
import com.journeyOS.core.database.entity.CityBean;
import com.journeyOS.core.repository.DBHelper;
import com.journeyOS.literouter.annotation.ARouterInject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@ARouterInject(api = ICityProvider.class)
public class CityRepositoryImpl implements ICityProvider {
    private static final String TAG = CityRepositoryImpl.class.getSimpleName();
    private static final String CITY_FILES = "china_citys.txt";
    private CityDao cityDao;
    private Object mLock = new Object();

    @Override
    public void onCreate() {
        EdgeDatabase database = DBHelper.provider(CoreManager.getDefault().getContext(), EdgeDatabase.class, DBConfigs.DB_NAME);
        cityDao = database.cityDao();
    }

    @Override
    public List<City> queryAllCities() {
        synchronized (mLock) {
            return cityDao.getAll();
        }
    }

    @Override
    public City searchCity(String cityId) {
        synchronized (mLock) {
            return cityDao.searchCity(cityId);
        }
    }

    @Override
    public City searchCity(String cityName, String county) {
        synchronized (mLock) {
            return cityDao.searchCity(cityName, county);
        }
    }

    @Override
    public List<City> matchingCity(String keyword) {
        synchronized (mLock) {
            return cityDao.matchCity(keyword);
        }
    }

    @Override
    public void loadCitys() {
        boolean cityInited = SpUtils.getInstant().getBoolean(Constant.CITY_INITED, false);
        if (cityInited) {
            return;
        }

        CoreManager.getDefault().getImpl(ICityProvider.class).saveCity(Constant.DEFAULT_CITY);

        synchronized (mLock) {
            String citys = FileIOUtils.readFileFromAsset(CoreManager.getDefault().getContext(), CITY_FILES);
            try {
                JSONArray jsonArray = new JSONArray(citys);
                List<City> allCitys = new ArrayList<>();

                for (int index = 0; index < jsonArray.length(); index++) {
                    JSONObject cityObject = jsonArray.getJSONObject(index);
                    CityBean cityBean = JsonHelper.fromJson(cityObject.toString(), CityBean.class);

                    for (CityBean.City city : cityBean.city) {
                        for (CityBean.City.CountyBean county : city.county) {
                            City config = new City();
                            config.province = cityBean.name;
                            config.provinceEn = cityBean.name_en;
                            config.cityName = city.name;
                            config.cityId = county.code;
                            config.country = county.name;
                            config.countryEn = county.name_en;

                            allCitys.add(config);
                        }
                    }

                    Collections.sort(allCitys, new CityComparator());
                    cityDao.insertCities(allCitys);
                    SpUtils.getInstant().put(Constant.CITY_INITED, true);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void saveCity(String cityId) {
        SpUtils.getInstant().put(Constant.CITY_ID, cityId);
    }

    @Override
    public void deleteCity(String cityId) {
        SpUtils.getInstant().remove(Constant.CITY_ID);
    }

    @Override
    public String getCity() {
        return SpUtils.getInstant().getString(Constant.CITY_ID, Constant.DEFAULT_CITY);
    }

    /**
     * a-z排序
     */
    private class CityComparator implements Comparator<City> {
        @Override
        public int compare(City cityLeft, City cityRight) {
            char a = cityLeft.countryEn.charAt(0);
            char b = cityRight.countryEn.charAt(0);
            return a - b;
        }
    }
}
