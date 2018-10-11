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
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.journeyOS.base.Constant;
import com.journeyOS.base.utils.AppUtils;
import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.base.utils.Singleton;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.api.edgeprovider.EdgeConfig;
import com.journeyOS.core.api.edgeprovider.IEdgeProvider;
import com.journeyOS.core.api.plugins.IPluginsApi;
import com.journeyOS.core.type.EdgeDirection;
import com.journeyOS.edge.EdgeService;
import com.journeyOS.edge.R;
import com.journeyOS.edge.view.EdgeView;
import com.journeyOS.edge.view.EdgeView.OnEdgeViewListener;

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

    private static final Map<Integer, EdgeConfig> sCache = new HashMap<Integer, EdgeConfig>(6);

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
        EdgeService.setEdgeDirection(direction);

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
                    EdgeService.setEdgeDirection(EdgeDirection.NONE);
                    hideEdge();
                }

                @Override
                public List<EdgeConfig> getConfigs(EdgeDirection direction) {
                    if (sCache != null) {
                        sCache.clear();
                    }

                    List<EdgeConfig> configs = CoreManager.getDefault().getImpl(IEdgeProvider.class).getConfigs(direction.name().toLowerCase());
                    LogUtils.d(TAG, "get " + direction.name().toLowerCase() + " configs " + configs);
                    for (EdgeConfig config : configs) {
                        int postion = -1;
                        String[] items = config.item.split(Constant.SEPARATOR);
                        if (items != null) {
                            postion = Integer.parseInt(items[1]);
                        }
                        String packageName = config.packageName;
                        LogUtils.d(TAG, "get " + direction.name().toLowerCase() + " edge, postion = " + postion + " , packageName = " + packageName);
                        if (postion != -1) sCache.put(postion, config);
                    }
                    return configs;
                }

                @Override
                public void onItemClick(int postion) {
                    LogUtils.d(TAG, "on item click = " + postion);
                    if (sCache != null) {
                        EdgeConfig config = sCache.get(postion);
                        if (config != null) {
                            AppUtils.startApp(mContext, config.packageName);
                            if (mEdgeView != null) mEdgeView.hideEdgeView();
                        } else {
                            CoreManager.getDefault().getImpl(IPluginsApi.class).navigationSelectorActivity(mContext, postion, EdgeService.getEdgeDirection());
                        }
                    }
                }

                @Override
                public void onItemLongClick(int postion) {
                    LogUtils.d(TAG, "on item long click = " + postion);
                    CoreManager.getDefault().getImpl(IPluginsApi.class).navigationSelectorActivity(mContext, postion, EdgeService.getEdgeDirection());
                }
            });

            //show after set listener
            mEdgeView.showEdgeView();
            mLastEdgeView = mEdgeView;
        }
    }

    public void hideEdge() {
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
        params.type = LayoutParams.TYPE_APPLICATION_OVERLAY;
        params.format = PixelFormat.TRANSPARENT;
        params.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
                | LayoutParams.FLAG_NOT_FOCUSABLE
                | LayoutParams.FLAG_SPLIT_TOUCH
                | LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH;
        params.gravity = Gravity.LEFT | Gravity.TOP;
        params.width = LayoutParams.MATCH_PARENT;
        params.height = LayoutParams.MATCH_PARENT;
        return params;
    }

}
