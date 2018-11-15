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
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
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
import com.journeyOS.core.type.EdgeDirection;
import com.journeyOS.edge.utils.NotificationUtils;
import com.journeyOS.edge.wm.BallManager;

public class EdgeService extends Service {
    private static final String TAG = EdgeService.class.getSimpleName();

    private Context mContext;

    private static Object mLock = new Object();
    private static EdgeDirection mEd = EdgeDirection.LEFT;

    public static void setEdgeDirection(EdgeDirection direction) {
        synchronized (mLock) {
            mEd = direction;
        }
    }

    public static EdgeDirection getEdgeDirection() {
        synchronized (mLock) {
            return mEd;
        }
    }

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
        LogUtils.d(TAG, "edge service create!");
        prepraJob();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.e(TAG, "edge service destroy!");
        ScreenObserver.getDefault().stopScreenStateUpdate(mContext);
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
        Intent intent = new Intent();
        intent.setPackage(getPackageName());
        intent.setAction("com.journeyOS.edge.action.FakeService");
        mContext.bindService(intent, mFakeConnection, Context.BIND_AUTO_CREATE);

        CoreManager.getDefault().getImpl(ICoreExecutors.class).diskIOThread().execute(new Runnable() {
            @Override
            public void run() {
                CoreManager.getDefault().getImpl(IEdgeProvider.class).initConfig();
            }
        });
        boolean daemon = SpUtils.getInstant().getBoolean(Constant.DAEMON, true);
        if (daemon) {
            ScreenObserver.getDefault().startScreenBroadcastReceiver(getApplicationContext());
            ScreenObserver.getDefault().setOnScreenStateListener(new ScreenObserver.ScreenStateListener() {
                @Override
                public void onScreenChanged(boolean isScreenOn) {
                    if (Constant.DEBUG) {
                        LogUtils.d(TAG, "edge service listener screen changed = " + isScreenOn);
                    }
                    if (isScreenOn) {
                        CoreManager.getDefault().getImpl(IAlive.class).destroy();
                    } else {
                        CoreManager.getDefault().getImpl(IAlive.class).keepAlive(mContext);
                    }
                }
            });
        }
    }

    private ServiceConnection mFakeConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtils.d(TAG, "fake service connected!");
//            Service fakeService = ((FakeService.LocalBinder) service).getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtils.i(TAG, "fake service disconnected!");
        }
    };

}
