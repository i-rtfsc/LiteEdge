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
import com.journeyOS.base.persistence.SpUtils;
import com.journeyOS.base.utils.BaseUtils;
import com.journeyOS.base.utils.JsonHelper;
import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.base.utils.Singleton;
import com.journeyOS.core.api.edgeprovider.IEdgeProvider;
import com.journeyOS.core.api.thread.ICoreExecutors;
import com.journeyOS.core.database.edge.Edge;
import com.journeyOS.core.database.edge.EdgeAir;
import com.journeyOS.core.database.entity.EdgeBean;
import com.journeyOS.core.database.user.EdgeUser;
import com.journeyOS.core.type.EdgeDirection;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class SyncManager {
    private static final String TAG = SyncManager.class.getSimpleName();
    private static final String OBJECT_ID = "objectId";
    private static final String AUTHOR = "author";

    private SyncManager() {
    }

    private static final Singleton<SyncManager> gDefault = new Singleton<SyncManager>() {
        @Override
        protected SyncManager create() {
            return new SyncManager();
        }
    };

    public static SyncManager getDefault() {
        return gDefault.get();
    }

    public void sync() {
        if (BmobUser.isLogin()
                && SpUtils.getInstant().getBoolean(Constant.AUTO_SYNC, true)) {
            EdgeBean bean = new EdgeBean();
            List<EdgeBean.Edge> edges = new ArrayList<>();

            List<Edge> configs = CoreManager.getDefault().getImpl(IEdgeProvider.class).getConfigs();
            LogUtils.d(TAG, "configs size = " + configs.size());
            for (Edge config : configs) {
                String packageName = config.packageName;
                EdgeBean.Edge edge = new EdgeBean.Edge();
                edge.postion = CoreManager.getDefault().getImpl(IEdgeProvider.class).getPostion(config.item);
                edge.direction = config.direction;
                edge.packageName = packageName;
                edges.add(edge);
                if (Constant.DEBUG) {
                    LogUtils.d(TAG, "sync " + edge.direction + " edge, postion = " + edge.postion + " , packageName = " + packageName);
                }
            }
            bean.edge = edges;

            EdgeAir edgeAir = new EdgeAir();
            EdgeUser edgeUser = BmobUser.getCurrentUser(EdgeUser.class);
            edgeAir.author = edgeUser;
            edgeAir.config = JsonHelper.toJson(bean);
            if (Constant.DEBUG) LogUtils.d(TAG, "sync config = " + edgeAir.config);

            String objectId = SpUtils.getInstant().getString(OBJECT_ID, null);
            if (!BaseUtils.isNull(objectId)) {
                edgeAir.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        LogUtils.d(TAG, "sync done(update), e = " + e);
                    }
                });
            } else {
                edgeAir.save(new SaveListener<String>() {
                    @Override
                    public void done(String s, BmobException e) {
                        LogUtils.d(TAG, "sync done(save), result = " + s + " e = " + e);
                        if (BaseUtils.isNull(e)) {
                            SpUtils.getInstant().put(OBJECT_ID, s);
                        }
                    }
                });
            }
        }
    }

    public void fetchEdgeAir() {
        if (BmobUser.isLogin()
                && SpUtils.getInstant().getBoolean(Constant.AUTO_SYNC, true)) {
            EdgeUser edgeUser = BmobUser.getCurrentUser(EdgeUser.class);
            BmobQuery<EdgeAir> edgeAirQuery = new BmobQuery<>();
            edgeAirQuery.addWhereEqualTo(AUTHOR, edgeUser);
            edgeAirQuery.findObjects(new FindListener<EdgeAir>() {
                @Override
                public void done(final List<EdgeAir> list, final BmobException e) {
                    LogUtils.d(TAG, "fetch edge air list = [" + list + "], e = [" + e + "]");
                    CoreManager.getDefault().getImpl(ICoreExecutors.class).diskIOThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            if (BaseUtils.isNull(e)) {
                                if (!BaseUtils.isNull(list) && list.size() > 0) {
                                    EdgeAir edgeAir = list.get(0);
                                    if (!BaseUtils.isNull(edgeAir) && !BaseUtils.isNull(edgeAir.config)) {
                                        String objectId = edgeAir.getObjectId();
                                        SpUtils.getInstant().put(OBJECT_ID, objectId);

                                        EdgeBean bean = JsonHelper.fromJson(edgeAir.config, EdgeBean.class);
                                        if (!BaseUtils.isNull(bean)) {
                                            List<EdgeBean.Edge> edges = bean.edge;
                                            for (EdgeBean.Edge edge : edges) {
                                                if (Constant.DEBUG) {
                                                    LogUtils.d(TAG, "fetch edge " + edge.direction + " edge, postion = " + edge.postion + " , packageName = " + edge.packageName);
                                                }

                                                Edge config = new Edge();
                                                config.packageName = edge.packageName;
                                                config.direction = edge.direction.toLowerCase();
                                                if (EdgeDirection.LEFT.name().toLowerCase().equals(config.direction)) {
                                                    config.item = CoreManager.getDefault().getImpl(IEdgeProvider.class).encodeItem(EdgeDirection.LEFT, edge.postion);
                                                } else if (EdgeDirection.RIGHT.name().toLowerCase().equals(config.direction)) {
                                                    config.item = CoreManager.getDefault().getImpl(IEdgeProvider.class).encodeItem(EdgeDirection.RIGHT, edge.postion);
                                                } else if (EdgeDirection.UP.name().toLowerCase().equals(config.direction)) {
                                                    config.item = CoreManager.getDefault().getImpl(IEdgeProvider.class).encodeItem(EdgeDirection.UP, edge.postion);
                                                }

                                                CoreManager.getDefault().getImpl(IEdgeProvider.class).insertOrUpdateConfig(config);
                                            }
                                        }
                                    }

                                }
                            }
                        }
                    });
                }
            });
        }
    }

}
