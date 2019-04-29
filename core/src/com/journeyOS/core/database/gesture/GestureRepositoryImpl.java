/*
 * Copyright (c) 2019 anqi.huang@outlook.com
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

package com.journeyOS.core.database.gesture;

import android.util.LruCache;

import com.journeyOS.base.Constant;
import com.journeyOS.base.persistence.SpUtils;
import com.journeyOS.base.device.DeviceUtils;
import com.journeyOS.base.utils.FileIOUtils;
import com.journeyOS.base.utils.JsonHelper;
import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.core.BuildConfig;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.SyncManager;
import com.journeyOS.core.api.edgeprovider.IGestureProvider;
import com.journeyOS.core.database.DBConfigs;
import com.journeyOS.core.database.DBHelper;
import com.journeyOS.core.database.EdgeDatabase;
import com.journeyOS.core.database.ball.BallRepositoryImpl;
import com.journeyOS.core.database.entity.GestureBean;
import com.journeyOS.core.type.FingerDirection;
import com.journeyOS.literouter.annotation.ARouterInject;

import java.util.List;

@ARouterInject(api = IGestureProvider.class)
public class GestureRepositoryImpl implements IGestureProvider {
    private static final String TAG = BallRepositoryImpl.class.getSimpleName();
    private static final String GESTURE_FILES = "gesture.json";
    private static final String GESTURE_FILES_OWNER = "gesture_owner.json";

    private GestureDao gestureDao;
    private Object mLock = new Object();

    private LruCache<String, Gesture> mCacheGestures = new LruCache<String, Gesture>(25);

    @Override
    public void onCreate() {
        EdgeDatabase database = DBHelper.provider(CoreManager.getDefault().getContext(), EdgeDatabase.class, DBConfigs.DB_NAME);
        gestureDao = database.gestureDao();
    }


    @Override
    public List<Gesture> getConfigs() {
        synchronized (mLock) {
            return gestureDao.getConfigs();
        }
    }

    @Override
    public Gesture getConfig(String direction) {
        synchronized (mLock) {
            Gesture gesture = mCacheGestures.get(direction);
            if (gesture == null) {
                gesture = gestureDao.searchConfig(direction);
                if (gesture != null) {
                    mCacheGestures.put(direction, gesture);
                }
            }

            return gesture;
        }
    }

    @Override
    public List<Gesture> getConfig(int orientation) {
        synchronized (mLock) {
            return gestureDao.searchConfig(orientation);
        }
    }

    @Override
    public void insertOrUpdateConfig(Gesture config) {
        if (config == null) {
            return;
        }
        synchronized (mLock) {
            gestureDao.insert(config);
            mCacheGestures.put(config.gestureDirection, config);
            SyncManager.getDefault().syncGesture();
        }
    }

    @Override
    public void deleteConfig(Gesture config) {
        if (config == null) {
            return;
        }
        synchronized (mLock) {
            gestureDao.delete(config);
            mCacheGestures.remove(config.gestureDirection);
            SyncManager.getDefault().syncGesture();
        }
    }

    @Override
    public void deleteAll() {
        synchronized (mLock) {
            gestureDao.deleteAll();
        }
    }

    @Override
    public void initConfig() {
        if (SpUtils.getInstant().getBoolean(Constant.GESTURE_INITED, false)) {
            return;
        }

        synchronized (mLock) {
            boolean isOwner = false;
            if (BuildConfig.DEBUG || DeviceUtils.SAMSUNG.equals(DeviceUtils.getDeviceBrand())) {
                isOwner = true;
            }
            String json = FileIOUtils.readFileFromAsset(CoreManager.getDefault().getContext(), isOwner ? GESTURE_FILES_OWNER : GESTURE_FILES);
            LogUtils.d(TAG, "read " + (isOwner ? GESTURE_FILES_OWNER : GESTURE_FILES) + " from asset, json = " + json);
            GestureBean bean = JsonHelper.fromJson(json, GestureBean.class);
            if (bean != null) {
                List<GestureBean.Gesture> gestures = bean.gestures;
                for (GestureBean.Gesture gesture : gestures) {
                    Gesture config = new Gesture();
                    config.gestureDirection = gesture.gestureDirection;
                    config.orientation = getOrientation(gesture.gestureDirection);
                    config.type = gesture.type;
                    config.action = gesture.action;
                    config.comment = gesture.comment;
                    LogUtils.d(TAG, " gesture = " + config.toString());
                    mCacheGestures.put(config.gestureDirection, config);
                    gestureDao.insert(config);
                }
                SpUtils.getInstant().put(Constant.GESTURE_INITED, true);
            }
        }
    }

    @Override
    public String encodeItem(FingerDirection direction, int orientation) {
        StringBuilder sb = new StringBuilder(orientation + "").append(Constant.SEPARATOR).append(direction.name().toLowerCase());
        return new String(sb);
    }

    @Override
    public String getDirection(String item) {
        String[] items = item.split(Constant.SEPARATOR);
        if (items != null) {
            return items[1];
        }
        return null;
    }

    @Override
    public int getOrientation(String item) {
        int postion = -1;
        String[] items = item.split(Constant.SEPARATOR);
        if (items != null) {
            postion = Integer.parseInt(items[0]);
        }
        return postion;
    }
}
