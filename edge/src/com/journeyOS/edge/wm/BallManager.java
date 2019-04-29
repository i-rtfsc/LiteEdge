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
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Toast;

import com.journeyOS.base.Constant;
import com.journeyOS.base.persistence.SpUtils;
import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.base.utils.Singleton;
import com.journeyOS.base.utils.UIUtils;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.StateMachine;
import com.journeyOS.core.api.edgeprovider.IBallProvider;
import com.journeyOS.core.api.thread.ICoreExecutors;
import com.journeyOS.core.database.ball.Ball;
import com.journeyOS.core.permission.IPermission;
import com.journeyOS.core.type.BallState;
import com.journeyOS.core.type.FingerDirection;
import com.journeyOS.edge.R;
import com.journeyOS.edge.view.InnerView;
import com.journeyOS.edge.view.OutterView;

import es.dmoral.toasty.Toasty;

public class BallManager {
    private static final String TAG = BallManager.class.getSimpleName();

    private Context mContext;
    private WindowManager mWm;

    private static Object mLock = new Object();

    private OutterView mOv;

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
                    final LayoutParams params = getLayoutParams(SpUtils.getInstant().getInt(Constant.BALL_SIZE, Constant.BALL_SIZE_DEFAULT));
                    CoreManager.getDefault().getImpl(ICoreExecutors.class).mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mOv = new OutterView(mContext);
                                mOv.setParams(params);
                                mWm.addView(mOv, params);
                                mOv.setVisibility(View.VISIBLE);
                                LogUtils.d(TAG, "add ball to windows manager");
                                mOv.setOnGestureListener(mGestureListener);
                            } catch (Exception e) {
                                LogUtils.e(TAG, "create ball exception = " + e);
                            }
                        }
                    });
                }
            });
        }
    }

    public void updateViewLayout(final int ballSize) {
//        hiding();
//        showing();
        if (mWm != null && mOv != null) {
            CoreManager.getDefault().getImpl(ICoreExecutors.class).diskIOThread().execute(new Runnable() {
                @Override
                public void run() {
                    final LayoutParams params = getLayoutParams(ballSize);
                    CoreManager.getDefault().getImpl(ICoreExecutors.class).mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                mWm.updateViewLayout(mOv, params);
                            } catch (Exception e) {
                                LogUtils.e(TAG, "update ball exception = " + e);
                            }
                        }
                    });
                }
            });
        }
    }

    public void showing() {
        synchronized (mLock) {
            LogUtils.d(TAG, "wanna showing ball");
            if (!CoreManager.getDefault().getImpl(IPermission.class).canDrawOverlays(mContext)) {
                String message = mContext.getString(R.string.hasnot_permission) + mContext.getString(R.string.overflow) + mContext.getString(R.string.auto_close_ball);
                Toasty.warning(mContext, message, Toast.LENGTH_SHORT).show();
                SpUtils.getInstant().put(Constant.BALL, false);
                return;
            }
            if (mOv == null && StateMachine.getBallState() == BallState.HIDE) {
                createBall();
            } else {
                LogUtils.d(TAG, "ball state was shown");
            }
        }
    }

    public void hiding() {
        synchronized (mLock) {
            LogUtils.d(TAG, "wanna hiding");
            if (mOv != null) {
                mOv.setVisibility(View.GONE);
                mWm.removeView(mOv);
                mOv = null;
            }
        }
    }

    public void updateInnerBall(int color) {
        if (mOv != null) {
            InnerView innerView = mOv.getInnerBall();
            if (innerView != null) {
                innerView.updateInnerBallColor(color);
            }
        }
    }

    LayoutParams getLayoutParams(int ballSize) {
        int orientation = mContext.getResources().getConfiguration().orientation;
        Ball ball = CoreManager.getDefault().getImpl(IBallProvider.class).getConfig(orientation);

        LayoutParams params = WindowUitls.getBaseLayoutParams();
        params.gravity = Gravity.LEFT | Gravity.TOP;
        params.width = ballSize;
        params.height = ballSize;
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
        void onGesture(FingerDirection fingerDirection);
    }

    private OutterView.OnGestureListener mGestureListener = new OutterView.OnGestureListener() {
        @Override
        public void onGesture(FingerDirection fingerDirection) {
            if (mListener != null) {
                mListener.onGesture(fingerDirection);
            }
        }

        @Override
        public void onViewAttachedToWindow() {
            StateMachine.setBallState(BallState.SHOW);
        }

        @Override
        public void onViewDetachedFromWindow() {
            StateMachine.setBallState(BallState.HIDE);
        }
    };

}
