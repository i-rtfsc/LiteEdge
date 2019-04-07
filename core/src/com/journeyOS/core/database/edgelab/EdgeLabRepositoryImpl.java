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

package com.journeyOS.core.database.edgelab;

import android.content.Context;
import android.util.LruCache;

import com.journeyOS.base.Constant;
import com.journeyOS.base.persistence.SpUtils;
import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.base.utils.UIUtils;
import com.journeyOS.base.widget.LocusLayoutManager;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.api.edgeprovider.IEdgeLabProvider;
import com.journeyOS.core.database.DBConfigs;
import com.journeyOS.core.database.DBHelper;
import com.journeyOS.core.database.EdgeDatabase;
import com.journeyOS.core.database.edge.EdgeRepositoryImpl;
import com.journeyOS.core.type.EdgeDirection;
import com.journeyOS.literouter.annotation.ARouterInject;

import java.util.List;

@ARouterInject(api = IEdgeLabProvider.class)
public class EdgeLabRepositoryImpl implements IEdgeLabProvider {
    private static final String TAG = EdgeRepositoryImpl.class.getSimpleName();
    private static final String EDGE_FILES = "edge.json";
    private EdgeLabDao edgeLabDao;

    private Object mLock = new Object();
    private Context mContext;

    private LruCache<String, EdgeLab> mCacheLabs = new LruCache<String, EdgeLab>(3);

    @Override
    public void onCreate() {
        mContext = CoreManager.getDefault().getContext();
        EdgeDatabase edgeDatabase = DBHelper.provider(mContext, EdgeDatabase.class, DBConfigs.DB_NAME);
        LogUtils.d(TAG, "edge lab database is NULL = " + (edgeDatabase == null));
        edgeLabDao = edgeDatabase.edgeLabDao();
        if (SpUtils.getInstant().getBoolean(Constant.EDGE_LAB_INITED, false)) {
            List<EdgeLab> labs = getConfigs();
            for (EdgeLab lab : labs) {
                mCacheLabs.put(lab.edge, lab);
            }
        }
    }


    @Override
    public List<EdgeLab> getConfigs() {
        synchronized (mLock) {
            return edgeLabDao.getConfigs();
        }
    }

    @Override
    public List<EdgeLab> getConfigs(String edge) {
        synchronized (mLock) {
            return edgeLabDao.searchConfig(edge, 1);
        }
    }

    @Override
    public EdgeLab getConfig(String edge) {
        synchronized (mLock) {
            return edgeLabDao.searchConfig(edge);
        }
    }

    @Override
    public void insertOrUpdateConfig(EdgeLab edgeLab) {
        synchronized (mLock) {
            edgeLabDao.insert(edgeLab);
            if (mCacheLabs != null && mCacheLabs.get(edgeLab.edge) != null) {
                mCacheLabs.remove(edgeLab.edge);
                mCacheLabs.put(edgeLab.edge, edgeLab);
            }
        }
    }

    @Override
    public void deleteConfig(EdgeLab edgeLab) {
        synchronized (mLock) {
            edgeLabDao.delete(edgeLab);
            if (mCacheLabs != null && mCacheLabs.get(edgeLab.edge) != null) {
                mCacheLabs.remove(edgeLab.edge);
            }
        }
    }

    @Override
    public void deleteAll() {
        synchronized (mLock) {
            edgeLabDao.deleteAll();
        }
    }

    @Override
    public void initConfig() {
        if (SpUtils.getInstant().getBoolean(Constant.EDGE_LAB_INITED, false)) {
            return;
        }

        synchronized (mLock) {
            //init left
            EdgeLab leftLab = new EdgeLab();
            leftLab.edge = EdgeDirection.LEFT.name().toLowerCase();
            leftLab.gravity = LocusLayoutManager.Gravity.START;
            leftLab.orientation = LocusLayoutManager.Orientation.VERTICAL;
            leftLab.radius = 1500;
            leftLab.peek = 150;
            leftLab.rotate = 1;
            leftLab.width = (int) (UIUtils.getScreenWidth(mContext) * 2.5 / 9);
            leftLab.height = (int) (UIUtils.getScreenHeight(mContext) * 3 / 4);
            edgeLabDao.insert(leftLab);

            //init right
            EdgeLab rightLab = new EdgeLab();
            rightLab.edge = EdgeDirection.RIGHT.name().toLowerCase();
            rightLab.gravity = LocusLayoutManager.Gravity.END;
            rightLab.orientation = LocusLayoutManager.Orientation.VERTICAL;
            rightLab.radius = 1500;
            rightLab.peek = 150;
            rightLab.rotate = 1;
            rightLab.width = (int) (UIUtils.getScreenWidth(mContext) * 2.5 / 9);
            rightLab.height = (int) (UIUtils.getScreenHeight(mContext) * 3 / 4);
            edgeLabDao.insert(rightLab);

            //init up
            EdgeLab upLab = new EdgeLab();
            upLab.edge = EdgeDirection.UP.name().toLowerCase();
            upLab.gravity = LocusLayoutManager.Gravity.START;
            upLab.orientation = LocusLayoutManager.Orientation.HORIZONTAL;
            upLab.radius = 1500;
            upLab.peek = 150;
            upLab.rotate = 1;
            upLab.width = (int) (UIUtils.getScreenHeight(mContext) * 3 / 4);
            upLab.height = (int) (UIUtils.getScreenWidth(mContext) * 2.5 / 9);
            edgeLabDao.insert(upLab);

            SpUtils.getInstant().put(Constant.EDGE_LAB_INITED, true);
        }
    }

    @Override
    public EdgeLab getCacheConfig(String edge) {
        if (mCacheLabs != null && mCacheLabs.get(edge) != null) {
            return mCacheLabs.get(edge);
        }
        return null;
    }
}
