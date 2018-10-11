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

package com.journeyOS.core.database;

import com.journeyOS.base.Constant;
import com.journeyOS.base.persistence.SpUtils;
import com.journeyOS.base.utils.FileIOUtils;
import com.journeyOS.base.utils.JsonHelper;
import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.api.edgeprovider.DBConfigs;
import com.journeyOS.core.api.edgeprovider.EdgeConfig;
import com.journeyOS.core.api.edgeprovider.IEdgeProvider;
import com.journeyOS.core.database.entity.EdgeBean;
import com.journeyOS.core.repository.DBHelper;
import com.journeyOS.core.type.EdgeDirection;
import com.journeyOS.literouter.annotation.ARouterInject;

import java.util.List;

@ARouterInject(api = IEdgeProvider.class)
public class EdgeRepositoryImpl implements IEdgeProvider {
    private static final String TAG = EdgeRepositoryImpl.class.getSimpleName();
    private static final String EDGE_FILES = "edge.json";
    private EdgeDao edgeDao;

    private Object mLock = new Object();

    @Override
    public void onCreate() {
        EdgeDatabase edgeDatabase = DBHelper.provider(CoreManager.getDefault().getContext(), EdgeDatabase.class, DBConfigs.DB_NAME);
        LogUtils.d(TAG, "edge database is NULL = " + (edgeDatabase == null));
        edgeDao = edgeDatabase.edgeDao();
    }

    @Override
    public List<EdgeConfig> getConfigs() {
        synchronized (mLock) {
            return edgeDao.getConfigs();
        }
    }

    @Override
    public List<EdgeConfig> getConfigs(String direction) {
        synchronized (mLock) {
            return edgeDao.findConfig(direction);
        }
    }

    @Override
    public EdgeConfig getConfig(String item) {
        synchronized (mLock) {
            return edgeDao.searchConfig(item);
        }
    }

    @Override
    public void insertOrUpdateConfig(EdgeConfig config) {
        synchronized (mLock) {
            edgeDao.insert(config);
        }
    }

    @Override
    public void deleteConfig(EdgeConfig config) {
        synchronized (mLock) {
            edgeDao.delete(config);
        }
    }

    @Override
    public void deleteAll() {
        synchronized (mLock) {
            edgeDao.deleteAll();
        }
    }

    @Override
    public void initConfig() {
        if (SpUtils.getInstant().getBoolean(Constant.DB_INITED, false)) {
            return;
        }

        synchronized (mLock) {
            String json = FileIOUtils.readFileFromAsset(CoreManager.getDefault().getContext(), EDGE_FILES);
            LogUtils.d(TAG, "read " + EDGE_FILES + " from asset, json = " + json);
            EdgeBean bean = JsonHelper.fromJson(json, EdgeBean.class);
            if (bean != null) {
                List<EdgeBean.Edge> edges = bean.edge;
                for (EdgeBean.Edge edge : edges) {
                    EdgeConfig config = new EdgeConfig();
                    config.packageName = edge.packageName;

                    //left
                    config.direction = EdgeDirection.LEFT.name().toLowerCase();
                    config.item = encodeItem(EdgeDirection.LEFT, edge.postion);
                    edgeDao.insert(config);

                    //right
                    config.direction = EdgeDirection.RIGHT.name().toLowerCase();
                    config.item = encodeItem(EdgeDirection.RIGHT, edge.postion);
                    edgeDao.insert(config);

                    //up
                    config.direction = EdgeDirection.UP.name().toLowerCase();
                    config.item = encodeItem(EdgeDirection.UP, edge.postion);
                    edgeDao.insert(config);
                }
                SpUtils.getInstant().put(Constant.DB_INITED, true);
            }
        }
    }

    @Override
    public String encodeItem(EdgeDirection direction, int postion) {
        StringBuilder sb = new StringBuilder(direction.name().toLowerCase()).append(Constant.SEPARATOR).append(postion);
        return new String(sb);
    }

    @Override
    public int getPostion(String item) {
        int postion = -1;
        String[] items = item.split(Constant.SEPARATOR);
        if (items != null) {
            postion = Integer.parseInt(items[1]);
        }
        return postion;
    }
}
