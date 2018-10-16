package com.journeyOS.core.location;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.journeyOS.base.utils.BaseUtils;
import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.api.edgeprovider.ICityProvider;
import com.journeyOS.core.api.location.ILocation;
import com.journeyOS.core.api.thread.ICoreExecutors;
import com.journeyOS.core.database.city.City;
import com.journeyOS.literouter.annotation.ARouterInject;

import java.util.ArrayList;

@ARouterInject(api = ILocation.class)
public class LocationImpl implements ILocation {
    private static final String TAG = LocationImpl.class.getSimpleName();
    private AMapLocationClient mLocationClient;
    private ArrayList<LocationChangedListener> mListeners = new ArrayList<>();
    private City mLocatedCity;

    @Override
    public void onCreate() {
        initLocation();
    }

    @Override
    public void addChangedListener(LocationChangedListener listener) {
        synchronized (mListeners) {
            mListeners.add(listener);
        }
    }

    @Override
    public void removeChangedListener(LocationChangedListener listener) {
        synchronized (mListeners) {
            mListeners.remove(listener);
        }
    }

    @Override
    public void startLocation() {
        if (BaseUtils.isNull(mLocationClient)) {
            initLocation();
        }

        mLocationClient.startLocation();
    }

    @Override
    public void stopLocation() {
        if (!BaseUtils.isNull(mLocationClient)) {
            mLocationClient.stopLocation();
        }
    }

    @Override
    public String getLocatedCityId() {
        return mLocatedCity != null ? mLocatedCity.cityId : null;
    }

    @Override
    public City getLocatedCity() {
        return mLocatedCity;
    }

    private void initLocation() {
        LogUtils.d(TAG, "init location sdk");
        mLocationClient = new AMapLocationClient(CoreManager.getDefault().getContext());
        AMapLocationClientOption option = new AMapLocationClientOption();
        option.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        option.setOnceLocationLatest(true);
        option.setInterval(2000);
        mLocationClient.setLocationOption(option);
        mLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                LogUtils.d(TAG, "on location changed " + aMapLocation.toString());
                final String city = aMapLocation.getCity().substring(0, 2);
                final String district = aMapLocation.getDistrict().substring(0, 2);

                CoreManager.getDefault().getImpl(ICoreExecutors.class).diskIOThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        mLocatedCity = CoreManager.getDefault().getImpl(ICityProvider.class).searchCity(city, district);

                        //城市库全名不匹配
                        if (mLocatedCity == null) {
                            String cityName = city.substring(0, 2);
                            String county = district.substring(0, 2);
                            mLocatedCity = CoreManager.getDefault().getImpl(ICityProvider.class).searchCity(cityName, county);
                        }

                        if (mLocatedCity != null) {
                            for (LocationChangedListener listener : mListeners) {
                                listener.onLocationChanged(mLocatedCity);
                            }
                        }
                    }
                });
            }
        });
    }
}
