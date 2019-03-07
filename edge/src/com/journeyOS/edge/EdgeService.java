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

package com.journeyOS.edge;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

import com.journeyOS.base.Constant;
import com.journeyOS.base.persistence.SpUtils;
import com.journeyOS.base.receiver.ScreenObserver;
import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.api.daemon.IAlive;
import com.journeyOS.core.api.edge.IEdge;
import com.journeyOS.core.api.edgeprovider.IEdgeProvider;
import com.journeyOS.core.api.thread.ICoreExecutors;
import com.journeyOS.core.type.Direction;
import com.journeyOS.edge.utils.NotificationUtils;
import com.journeyOS.edge.wm.BallManager;
import com.journeyOS.i007Service.DataResource.FACTORY;
import com.journeyOS.i007Service.I007Manager;
import com.journeyOS.i007Service.interfaces.II007Listener;

public class EdgeService extends Service {
    private static final String TAG = EdgeService.class.getSimpleName();

    private Context mContext;

    private II007Listener mII007Listener = new II007Listener.Stub() {
        @Override
        public void onSceneChanged(long l, String s, String s1) throws RemoteException {
            handleSceneChanged(l, s, s1);
        }
    };

    final IEdgeInterface.Stub mBinder = new IEdgeInterface.Stub() {
        @Override
        public void showingBall(final boolean isShowing) throws RemoteException {
            LogUtils.d(TAG, "showing ball = " + isShowing);
            CoreManager.getDefault().getImpl(ICoreExecutors.class).mainThread().execute(new Runnable() {
                @Override
                public void run() {
                    if (isShowing) {
                        BallManager.getDefault().showing();
                        BallManager.getDefault().setOnBallViewListener(new BallManager.OnBallViewListener() {
                            @Override
                            public void onGesture(Direction direction) {
                                LogUtils.d(TAG, "on ball direction = " + direction.name());
                                Dispatcher.getDefault().handleGestureDirection(direction);
                            }
                        });
                    } else {
                        BallManager.getDefault().Hiding();
                    }
                }
            });
        }

        @Override
        public void showingEdge(int direction) throws RemoteException {
            CoreManager.getDefault().getImpl(IEdge.class).showingEdge(direction);
        }

        @Override
        public void showingEdgeDelayed(int direction, long delayMillis) throws RemoteException {
            CoreManager.getDefault().getImpl(IEdge.class).showingEdge(direction, delayMillis);
        }

        @Override
        public void hidingEdge() throws RemoteException {
            CoreManager.getDefault().getImpl(IEdge.class).hidingEdge();
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = CoreManager.getDefault().getContext();
        CoreManager.getDefault().setRunning(true);
        LogUtils.d(TAG, "edge service create!");
        prepraJob();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.e(TAG, "edge service destroy!");
        ScreenObserver.getDefault().stopScreenStateUpdate(mContext);
        CoreManager.getDefault().setRunning(false);
        I007Manager.unregisterListener(mII007Listener);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        BallManager.getDefault().updateViewLayout();
        CoreManager.getDefault().getImpl(IEdge.class).hidingEdge();
    }

    void prepraJob() {
        this.startForeground(NotificationUtils.NOTIFICATION_ID, NotificationUtils.getNotification(mContext));
//        this.stopForeground(true);

        final long factors = I007Manager.SCENE_FACTOR_LCD;
        I007Manager.registerListener(factors, mII007Listener);

        CoreManager.getDefault().getImpl(ICoreExecutors.class).diskIOThread().execute(new Runnable() {
            @Override
            public void run() {
                CoreManager.getDefault().getImpl(IEdgeProvider.class).initConfig();
            }
        });
    }

    void handleSceneChanged(long factorId, String status, String packageName) {
        FACTORY factory = I007Manager.getFactory(factorId);
        LogUtils.d(TAG, "handle scene changed factorId = [" + factorId + "], status = [" + status + "], packageName = [" + packageName + "]");
        switch (factory) {
            case LCD:
                boolean isScreenOn = I007Manager.isScreenOn(status);
                handleScreen(isScreenOn);
                break;
        }
    }

    void handleScreen(boolean isScreenOn) {
        boolean daemon = SpUtils.getInstant().getBoolean(Constant.DAEMON, true);
        if (daemon) {
            if (Constant.DEBUG) {
                LogUtils.d(TAG, "edge service listener screen changed = " + isScreenOn);
            }
            if (isScreenOn) {
                CoreManager.getDefault().getImpl(IAlive.class).destroy();
            } else {
                CoreManager.getDefault().getImpl(IAlive.class).keepAlive(mContext);
                I007Manager.keepAlive();
            }
        }
    }

}
