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

package com.journeyOS.edge.wm;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.journeyOS.base.Constant;
import com.journeyOS.base.utils.AppUtils;
import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.base.utils.Singleton;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.StateMachine;
import com.journeyOS.core.api.edgeprovider.ICityProvider;
import com.journeyOS.core.api.edgeprovider.IEdgeLabProvider;
import com.journeyOS.core.api.edgeprovider.IEdgeProvider;
import com.journeyOS.core.api.plugins.IPlugins;
import com.journeyOS.core.api.thread.ICoreExecutors;
import com.journeyOS.core.api.weather.IFetchWeather;
import com.journeyOS.core.database.edge.Edge;
import com.journeyOS.core.database.edgelab.EdgeLab;
import com.journeyOS.core.type.EdgeDirection;
import com.journeyOS.core.weather.Air;
import com.journeyOS.core.weather.Weather;
import com.journeyOS.edge.R;
import com.journeyOS.edge.utils.ComparatorDesc;
import com.journeyOS.edge.view.EdgeView;
import com.journeyOS.edge.view.EdgeView.OnEdgeViewListener;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EdgeManager {
    private static final String TAG = EdgeManager.class.getSimpleName();

    private Context mContext;
    private WindowManager mWm;

    private EdgeView mLeftEdgeView;
    private EdgeView mRightEdgeView;
    private EdgeView mUpEdgeView;
    private EdgeView mEdgeView;
    private EdgeView mLastEdgeView;

    private static final Map<Integer, Edge> sCache = new HashMap<Integer, Edge>(6);

    private EdgeManager() {
        mContext = CoreManager.getDefault().getContext();
        mWm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
    }

    private static final Singleton<EdgeManager> gDefault = new Singleton<EdgeManager>() {
        @Override
        protected EdgeManager create() {
            return new EdgeManager();
        }
    };

    public static EdgeManager getDefault() {
        return gDefault.get();
    }

    void createEdgeView() {
        int orientation = mContext.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (mUpEdgeView == null) {
                mUpEdgeView = (EdgeView) View.inflate(mContext, R.layout.layout_edge_up, null);
            }
        } else {
            if (mLeftEdgeView == null) {
                mLeftEdgeView = (EdgeView) View.inflate(mContext, R.layout.layout_edge_left, null);
            }
            if (mRightEdgeView == null) {
                mRightEdgeView = (EdgeView) View.inflate(mContext, R.layout.layout_edge_right, null);
            }
        }
    }

    public void showEdge(EdgeDirection direction) {
        StateMachine.setEdgeDirection(direction);

        getView(direction);

        if (mEdgeView == null) {
            createEdgeView();
            getView(direction);
        }
        if (mLastEdgeView != mEdgeView || (mEdgeView != null && !mEdgeView.isShown())) {
            if (!mEdgeView.isAttachedToWindow()) {
                mWm.addView(mEdgeView, getLayoutParams());
            }
            if (mLastEdgeView != null && mLastEdgeView.isShown()) {
                mLastEdgeView.hideEdgeView();
            }
            mEdgeView.setVisibility(View.VISIBLE);

            mEdgeView.setOnEdgeViewListener(new OnEdgeViewListener() {
                @Override
                public void onViewAttachedToWindow() {
                }

                @Override
                public void onViewDetachedFromWindow() {
                    LogUtils.d(TAG, "edge view has been hidden!");
                    StateMachine.setEdgeDirection(EdgeDirection.NONE);
                    removeEdge();
                }

                @Override
                public List<Edge> getConfigs(EdgeDirection direction) {
                    if (sCache != null) {
                        sCache.clear();
                    }
                    List<Edge> configs = CoreManager.getDefault().getImpl(IEdgeProvider.class).getConfigs(direction.name().toLowerCase());
                    if (Constant.DEBUG) {
                        LogUtils.d(TAG, "get " + direction.name().toLowerCase() + " configs " + configs);
                    }
                    for (Edge config : configs) {
                        int postion = CoreManager.getDefault().getImpl(IEdgeProvider.class).getPostion(config.item);
                        String packageName = config.packageName;
                        if (Constant.DEBUG) {
                            LogUtils.d(TAG, "get " + direction.name().toLowerCase() + " edge, postion = " + postion + " , packageName = " + packageName);
                        }
                        if (postion != -1) sCache.put(postion, config);
                    }
                    Collections.sort(configs, new ComparatorDesc());
                    return configs;
                }

                @Override
                public EdgeLab getLabConfig(EdgeDirection direction) {
                    EdgeLab edgeLab = CoreManager.getDefault().getImpl(IEdgeLabProvider.class).getConfig(direction.name().toLowerCase());
                    return edgeLab;
                }

                @Override
                public void onItemClick(int postion) {
                    LogUtils.d(TAG, "on item click = " + postion);
                    if (sCache != null) {
                        Edge config = sCache.get(postion);
                        if (config != null) {
                            boolean isAppExisted = AppUtils.isPackageExisted(mContext, config.packageName);
                            if (isAppExisted) {
                                AppUtils.startApp(mContext, config.packageName);
                                if (mEdgeView != null) mEdgeView.hideEdgeView();
                            } else {
                                CoreManager.getDefault().getImpl(IPlugins.class).navigationSelectorActivity(mContext, postion, StateMachine.getEdgeDirection());
                            }
                        } else {
                            CoreManager.getDefault().getImpl(IPlugins.class).navigationSelectorActivity(mContext, postion, StateMachine.getEdgeDirection());
                        }
                    }
                }

                @Override
                public void onItemLongClick(int postion) {
                    LogUtils.d(TAG, "on item long click = " + postion);
                    CoreManager.getDefault().getImpl(IPlugins.class).navigationSelectorActivity(mContext, postion, StateMachine.getEdgeDirection());
                }

                @Override
                public void onLongClickStatusbar() {
                    boolean appExisted = CoreManager.getDefault().getImpl(IPlugins.class).isWeatherAppExisted(mContext);
                    if (appExisted) {
                        CoreManager.getDefault().getImpl(IPlugins.class).navigationWeatherApp(mContext);
                    } else {
                        CoreManager.getDefault().getImpl(IPlugins.class).navigationSearchActivity(mContext);
                    }
                }

                @Override
                public Weather getWeather() {
                    String city = CoreManager.getDefault().getImpl(ICityProvider.class).getCity();
                    return CoreManager.getDefault().getImpl(IFetchWeather.class).queryWeather(city, false);
                }

                @Override
                public Air getAir() {
                    String city = CoreManager.getDefault().getImpl(ICityProvider.class).getCity();
                    return CoreManager.getDefault().getImpl(IFetchWeather.class).queryAir(city, false);
                }

                @Override
                public void saveRadius(final EdgeDirection direction, final int radius) {
                    CoreManager.getDefault().getImpl(ICoreExecutors.class).diskIOThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            EdgeLab edgeLab = CoreManager.getDefault().getImpl(IEdgeLabProvider.class).getConfig(direction.name().toLowerCase());
                            if (edgeLab != null) {
                                edgeLab.radius = radius;
                                CoreManager.getDefault().getImpl(IEdgeLabProvider.class).insertOrUpdateConfig(edgeLab);
                            }
                        }
                    });
                }

                @Override
                public void savePeek(final EdgeDirection direction, final int peek) {
                    CoreManager.getDefault().getImpl(ICoreExecutors.class).diskIOThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            EdgeLab edgeLab = CoreManager.getDefault().getImpl(IEdgeLabProvider.class).getConfig(direction.name().toLowerCase());
                            if (edgeLab != null) {
                                edgeLab.peek = peek;
                                CoreManager.getDefault().getImpl(IEdgeLabProvider.class).insertOrUpdateConfig(edgeLab);
                            }
                        }
                    });
                }

            });

            //show after set listener
            mEdgeView.showEdgeView();
            mLastEdgeView = mEdgeView;
        }
    }

    public void hideEdge() {
        if (mEdgeView != null) {
            mEdgeView.hideEdgeView();
        }
    }

    public void removeEdge() {
        if (mEdgeView != null) {
            mEdgeView.setVisibility(View.GONE);
            mWm.removeView(mEdgeView);
        }
        mLeftEdgeView = mRightEdgeView = mUpEdgeView = mLastEdgeView = mEdgeView = null;
    }

    void getView(EdgeDirection direction) {
        switch (direction) {
            case RIGHT:
                mEdgeView = mRightEdgeView;
                break;
            case LEFT:
                mEdgeView = mLeftEdgeView;
                break;
            case UP:
                mEdgeView = mUpEdgeView;
                break;
        }
    }

    LayoutParams getLayoutParams() {
        LayoutParams params = new LayoutParams();
        if (Build.VERSION.SDK_INT >= 26) {
            params.type = LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            params.type = LayoutParams.TYPE_TOAST;
        }
        params.format = PixelFormat.TRANSPARENT;
        params.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
//                | LayoutParams.FLAG_NOT_FOCUSABLE
                | LayoutParams.FLAG_LAYOUT_IN_SCREEN
                | LayoutParams.FLAG_SPLIT_TOUCH
                | LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        params.gravity = Gravity.LEFT | Gravity.TOP;
        params.width = LayoutParams.MATCH_PARENT;
        params.height = LayoutParams.MATCH_PARENT;
        return params;
    }
}
