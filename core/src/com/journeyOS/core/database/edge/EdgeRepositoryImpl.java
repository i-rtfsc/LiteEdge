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

package com.journeyOS.core.database.edge;

import com.journeyOS.base.Constant;
import com.journeyOS.base.persistence.SpUtils;
import com.journeyOS.base.utils.FileIOUtils;
import com.journeyOS.base.utils.JsonHelper;
import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.api.edgeprovider.IEdgeProvider;
import com.journeyOS.core.database.DBConfigs;
import com.journeyOS.core.database.DBHelper;
import com.journeyOS.core.database.EdgeDatabase;
import com.journeyOS.core.database.entity.EdgeBean;
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
    public List<Edge> getConfigs() {
        synchronized (mLock) {
            return edgeDao.getConfigs();
        }
    }

    @Override
    public List<Edge> getConfigs(String direction) {
        synchronized (mLock) {
            return edgeDao.findConfig(direction);
        }
    }

    @Override
    public Edge getConfig(String item) {
        synchronized (mLock) {
            return edgeDao.searchConfig(item);
        }
    }

    @Override
    public void insertOrUpdateConfig(Edge config) {
        synchronized (mLock) {
            edgeDao.insert(config);
        }
    }

    @Override
    public void deleteConfig(Edge config) {
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
                    Edge config = new Edge();
                    config.packageName = edge.packageName;

                    config.direction = edge.direction.toLowerCase();
                    if (EdgeDirection.LEFT.name().toLowerCase().equals(config.direction)) {
                        config.item = encodeItem(EdgeDirection.LEFT, edge.postion);
                    } else if (EdgeDirection.RIGHT.name().toLowerCase().equals(config.direction)) {
                        config.item = encodeItem(EdgeDirection.RIGHT, edge.postion);
                    } else if (EdgeDirection.UP.name().toLowerCase().equals(config.direction)) {
                        config.item = encodeItem(EdgeDirection.UP, edge.postion);
                    }
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
