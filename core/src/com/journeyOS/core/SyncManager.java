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
import com.journeyOS.core.api.edgeprovider.IGestureProvider;
import com.journeyOS.core.api.thread.ICoreExecutors;
import com.journeyOS.core.database.edge.Edge;
import com.journeyOS.core.database.edge.EdgeAir;
import com.journeyOS.core.database.entity.EdgeBean;
import com.journeyOS.core.database.entity.GestureBean;
import com.journeyOS.core.database.gesture.Gesture;
import com.journeyOS.core.database.user.EdgeUser;
import com.journeyOS.core.type.EdgeDirection;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
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
        if (AccountManager.getDefault().isLogin()
                && SpUtils.getInstant().getBoolean(Constant.AUTO_SYNC, Constant.AUTO_SYNC_DEFAULT)) {
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
            EdgeUser edgeUser = AccountManager.getDefault().getCurrentUser();
            edgeAir.author = edgeUser;
            edgeAir.config = JsonHelper.toJson(bean);
            if (Constant.DEBUG) LogUtils.d(TAG, "sync config = " + edgeAir.config);

            String objectId = SpUtils.getInstant().getString(OBJECT_ID, null);
            if (Constant.DEBUG) LogUtils.d(TAG, "sync objectId = " + objectId);
            if (!BaseUtils.isNull(objectId)) {
                edgeAir.update(objectId, new UpdateListener() {
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
        if (AccountManager.getDefault().isLogin()
                && SpUtils.getInstant().getBoolean(Constant.AUTO_SYNC, Constant.AUTO_SYNC_DEFAULT)) {
            EdgeUser edgeUser = AccountManager.getDefault().getCurrentUser();
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
                                    if (!BaseUtils.isNull(edgeAir)) {
                                        String objectId = edgeAir.getObjectId();
                                        SpUtils.getInstant().put(OBJECT_ID, objectId);

                                        if (!BaseUtils.isNull(edgeAir.config)) {
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

                                        if (!BaseUtils.isNull(edgeAir.gestures)) {
                                            GestureBean bean = JsonHelper.fromJson(edgeAir.gestures, GestureBean.class);
                                            if (!BaseUtils.isNull(bean)) {
                                                List<GestureBean.Gesture> gestures = bean.gestures;
                                                for (GestureBean.Gesture gesture : gestures) {
                                                    if (Constant.DEBUG) {
                                                        LogUtils.d(TAG, "sync " + gesture.gestureDirection +
                                                                ", type = " + gesture.type +
                                                                " , action = " + gesture.action +
                                                                " , comment = " + gesture.comment);
                                                    }

                                                    Gesture config = new Gesture();
                                                    config.gestureDirection = gesture.gestureDirection;
                                                    config.orientation = CoreManager.getDefault().getImpl(IGestureProvider.class).getOrientation(gesture.gestureDirection);
                                                    config.type = gesture.type;
                                                    config.action = gesture.action;
                                                    config.comment = gesture.comment;

                                                    CoreManager.getDefault().getImpl(IGestureProvider.class).insertOrUpdateConfig(config);
                                                }
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

    public void syncGesture() {
        if (AccountManager.getDefault().isLogin()
                && SpUtils.getInstant().getBoolean(Constant.AUTO_SYNC, Constant.AUTO_SYNC_DEFAULT)) {
            GestureBean gestureBean = new GestureBean();
            List<GestureBean.Gesture> gestures = new ArrayList<>();

            List<Gesture> configs = CoreManager.getDefault().getImpl(IGestureProvider.class).getConfigs();
            LogUtils.d(TAG, "configs size = " + configs.size());
            for (Gesture config : configs) {
                GestureBean.Gesture gesture = new GestureBean.Gesture();
                gesture.gestureDirection = config.gestureDirection;
                gesture.type = config.type;
                gesture.action = config.action;
                gesture.comment = config.comment;

                gestures.add(gesture);

                if (Constant.DEBUG) {
                    LogUtils.d(TAG, "sync " + gesture.gestureDirection +
                            ", type = " + gesture.type +
                            " , action = " + gesture.action +
                            " , comment = " + gesture.comment);
                }
            }

            gestureBean.gestures = gestures;

            EdgeAir edgeAir = new EdgeAir();
            EdgeUser edgeUser = AccountManager.getDefault().getCurrentUser();
            edgeAir.author = edgeUser;
            edgeAir.gestures = JsonHelper.toJson(gestureBean);
            if (Constant.DEBUG) LogUtils.d(TAG, "sync config = " + edgeAir.config);

            String objectId = SpUtils.getInstant().getString(OBJECT_ID, null);
            if (Constant.DEBUG) LogUtils.d(TAG, "sync objectId = " + objectId);
            if (!BaseUtils.isNull(objectId)) {
                edgeAir.update(objectId, new UpdateListener() {
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

}
