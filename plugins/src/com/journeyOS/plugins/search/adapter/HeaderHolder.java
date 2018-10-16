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

package com.journeyOS.plugins.search.adapter;

import android.app.Activity;
import android.support.v4.util.Pair;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.journeyOS.base.adapter.BaseAdapterData;
import com.journeyOS.base.adapter.BaseRecyclerAdapter;
import com.journeyOS.base.adapter.BaseViewHolder;
import com.journeyOS.base.utils.BaseUtils;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.api.edgeprovider.ICityProvider;
import com.journeyOS.core.api.location.ILocation;
import com.journeyOS.core.database.city.City;
import com.journeyOS.literouter.Router;
import com.journeyOS.plugins.R;
import com.journeyOS.plugins.R2;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class HeaderHolder extends BaseViewHolder<HeaderData> {

    @BindView(R2.id.tv_located_city)
    TextView mTvLocatedCity;
    @BindView(R2.id.city_header_recyclerView)
    RecyclerView mRecyclerView;
    private City mLocationCity;

    private BaseRecyclerAdapter mHotCityAdapter;


    public HeaderHolder(View itemView, BaseRecyclerAdapter baseRecyclerAdapter) {
        super(itemView, baseRecyclerAdapter);
        Router.getDefault().register(this);
        initViews();

    }

    @Override
    public void updateItem(HeaderData data, int position) {
        if (BaseUtils.isNull(data)) {
            return;
        }
        mHotCityAdapter.clear();
        List<HotCity> hotCities = new ArrayList<>();
        for (Pair<String, String> city : data.getCities()) {
            hotCities.add(new HotCity(city.first, city.second));
        }
        mHotCityAdapter.registerHolder(HotCityHolder.class, hotCities);
    }

    @Override
    public int getContentViewId() {
        return R.layout.city_item_city_select_header;
    }


    public void initViews() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 3));
        mHotCityAdapter = new BaseRecyclerAdapter(getContext());
        mRecyclerView.setAdapter(mHotCityAdapter);

        City locatedCity = CoreManager.getDefault().getImpl(ILocation.class).getLocatedCity();
        showLocation(locatedCity);

    }

    private void showLocation(City city) {
        mLocationCity = city;
        if (mLocationCity != null) {
            mTvLocatedCity.setText(mLocationCity.country);
        } else {
            mTvLocatedCity.setText(R.string.city_located_failed);
        }
    }

    @OnClick(R2.id.location_layout)
    void locate() {
        if (mLocationCity != null) {
            CoreManager.getDefault().getImpl(ICityProvider.class).saveCity(mLocationCity.cityId);
            if (getContext() instanceof Activity) {
                ((Activity) getContext()).finish();
            }
        } else {
            CoreManager.getDefault().getImpl(ILocation.class).startLocation();
            mTvLocatedCity.setText(R.string.city_locating);
            CoreManager.getDefault().getImpl(ILocation.class).addChangedListener(new ILocation.LocationChangedListener() {
                @Override
                public void onLocationChanged(City city) {
                    showLocation(city);
                }
            });
        }
    }

    static final class HotCity implements BaseAdapterData {
        String mCityName;
        String mCityId;

        HotCity(String cityName, String cityId) {
            mCityName = cityName;
            mCityId = cityId;
        }

        @Override
        public int getContentViewId() {
            return R.layout.city_item_hot_city;
        }
    }

    public static final class HotCityHolder extends BaseViewHolder<HotCity> {

        @BindView(R2.id.tv_hot_city_name)
        TextView mTvHotCityName;
        HotCity mHotCity;

        public HotCityHolder(View itemView, BaseRecyclerAdapter baseRecyclerAdapter) {
            super(itemView, baseRecyclerAdapter);
        }

        @Override
        public void updateItem(HotCity data, int position) {
            mTvHotCityName.setText(data.mCityName);
            mHotCity = data;
        }

        @Override
        public int getContentViewId() {
            return R.layout.city_item_hot_city;
        }

        @OnClick(R2.id.tv_hot_city_name)
        void navigationWeather() {
            String cityId = mHotCity.mCityId;
            CoreManager.getDefault().getImpl(ICityProvider.class).saveCity(cityId);
//            CoreManager.getImpl(IWeatherProvider.class).updateWeather(cityId);
            if (getContext() instanceof Activity) {
                ((Activity) getContext()).finish();
            }
        }
    }
}
