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

package com.journeyOS.core;

import com.journeyOS.base.Constant;
import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.base.utils.Singleton;
import com.journeyOS.core.database.online.ConfigsAir;
import com.journeyOS.i007Service.DataResource;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

public class OnlineManager {
    private static final String TAG = OnlineManager.class.getSimpleName();
    private static final String OBJECT_ID = "26d4f49604";

    ConfigsAir mConfigsAir;
    private long mLastTime;

    private OnlineManager() {
    }

    private static final Singleton<OnlineManager> gDefault = new Singleton<OnlineManager>() {
        @Override
        protected OnlineManager create() {
            return new OnlineManager();
        }
    };

    public static OnlineManager getDefault() {
        return gDefault.get();
    }

    @Deprecated
    public void saveConfigs() {
        ConfigsAir configsAir = new ConfigsAir();
        configsAir.adType = DataResource.APP.VIDEO.name().toLowerCase() + Constant.SEPARATOR
                + DataResource.APP.NEWS.name().toLowerCase() + Constant.SEPARATOR
                + DataResource.APP.BROWSER.name().toLowerCase() + Constant.SEPARATOR
                + DataResource.APP.READER.name().toLowerCase() + Constant.SEPARATOR
                + DataResource.APP.MUSIC.name().toLowerCase();
        configsAir.update(OBJECT_ID, new UpdateListener() {
            @Override
            public void done(BmobException e) {
                LogUtils.d(TAG, "update result = [" + e + "]");
            }
        });
    }

    public void syncConfigs() {
        mLastTime = System.currentTimeMillis();
        BmobQuery<ConfigsAir> bmobQuery = new BmobQuery<>();
        bmobQuery.getObject(OBJECT_ID, new QueryListener<ConfigsAir>() {
            @Override
            public void done(final ConfigsAir configsAir, BmobException e) {
                if (e == null) {
                    mConfigsAir = configsAir;
                }
            }
        });
    }

    /**
     * 为空或者1h就强制查询一次，
     * 本来可以监听服务器上数据库变化的，但是一个月要99块钱的服务费。
     */
    public ConfigsAir getOnlineConfigs() {
        if (mConfigsAir == null ||
                (Math.abs((System.currentTimeMillis() - mLastTime)) > Constant.TIME_INTERVAL / 1)) {
            syncConfigs();
        }
        return mConfigsAir;
    }
}
