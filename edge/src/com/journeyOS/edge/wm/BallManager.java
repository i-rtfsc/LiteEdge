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
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.base.utils.Singleton;
import com.journeyOS.base.utils.UIUtils;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.api.edgeprovider.IBallProvider;
import com.journeyOS.core.api.thread.ICoreExecutors;
import com.journeyOS.core.database.ball.Ball;
import com.journeyOS.core.type.Direction;
import com.journeyOS.edge.view.OutterView;

public class BallManager {
    private static final String TAG = BallManager.class.getSimpleName();

    private static final int BALL_SIEZ = 175;

    private Context mContext;
    private WindowManager mWm;

    private OutterView mOv;

    private BallState mBallState = BallState.HIDE;

    private BallManager() {
        mContext = CoreManager.getDefault().getContext();
        mWm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        //createBall();
    }

    private static final Singleton<BallManager> gDefault = new Singleton<BallManager>() {
        @Override
        protected BallManager create() {
            return new BallManager();
        }
    };

    public static BallManager getDefault() {
        return gDefault.get();
    }


    void createBall() {
        if (mOv == null) {
            CoreManager.getDefault().getImpl(ICoreExecutors.class).diskIOThread().execute(new Runnable() {
                @Override
                public void run() {
                    final LayoutParams params = getLayoutParams();
                    CoreManager.getDefault().getImpl(ICoreExecutors.class).mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            mOv = new OutterView(mContext);
                            mOv.setParams(params);
                            mWm.addView(mOv, params);
                            mOv.setVisibility(View.VISIBLE);
                            LogUtils.d(TAG, "add ball to windows manager");
                            mOv.setOnGestureListener(mGestureListener);
                        }
                    });
                }
            });
        }
    }

    public void updateViewLayout() {
        if (mWm != null && mOv != null) {
            CoreManager.getDefault().getImpl(ICoreExecutors.class).diskIOThread().execute(new Runnable() {
                @Override
                public void run() {
                    final LayoutParams params = getLayoutParams();
                    CoreManager.getDefault().getImpl(ICoreExecutors.class).mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            mWm.updateViewLayout(mOv, params);
                        }
                    });
                }
            });
        }
    }

    public void showing() {
        LogUtils.d(TAG, "wanna showing");
        if (mOv == null) {
            createBall();
        }
    }

    public void Hiding() {
        LogUtils.d(TAG, "wanna hiding");
        if (mOv != null) {
            mOv.setVisibility(View.GONE);
            mWm.removeView(mOv);
            mOv = null;
        }
    }

    public boolean isBallShowing() {
        return mBallState == BallState.SHOW;
    }

    LayoutParams getLayoutParams() {
        int orientation = mContext.getResources().getConfiguration().orientation;
        Ball ball = CoreManager.getDefault().getImpl(IBallProvider.class).getConfig(orientation);
        LayoutParams params = new WindowManager.LayoutParams();
        params.type = LayoutParams.TYPE_APPLICATION_OVERLAY;
        params.format = PixelFormat.TRANSPARENT;
        params.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL | LayoutParams.FLAG_NOT_FOCUSABLE;
        params.gravity = Gravity.LEFT | Gravity.TOP;
        params.width = BALL_SIEZ;
        params.height = BALL_SIEZ;
        if (ball != null) {
            params.x = ball.layoutX;
            params.y = ball.layoutY;
        } else {
            params.x = UIUtils.getScreenWidth(mContext) / 2;
            params.y = UIUtils.getScreenHeight(mContext) / 2;
        }
        return params;
    }

    private static OnBallViewListener mListener;

    public void setOnBallViewListener(OnBallViewListener inf) {
        mListener = inf;
    }

    public interface OnBallViewListener {
        void onGesture(Direction direction);
    }

    private OutterView.OnGestureListener mGestureListener = new OutterView.OnGestureListener() {
        @Override
        public void onGesture(Direction direction) {
            if (mListener != null) {
                mListener.onGesture(direction);
            }
        }

        @Override
        public void onViewAttachedToWindow() {
            mBallState = BallState.SHOW;

        }

        @Override
        public void onViewDetachedFromWindow() {
            mBallState = BallState.HIDE;
        }
    };

    public enum BallState {
        SHOW,
        HIDE
    }
}
