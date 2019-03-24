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

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

import com.journeyOS.base.Constant;
import com.journeyOS.base.persistence.SpUtils;
import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.base.utils.Singleton;
import com.journeyOS.core.CoreManager;

public class EdgeServiceManager {
    private static final String TAG = EdgeServiceManager.class.getSimpleName();

    private static final String EDGE_PACKAGE = "com.journeyOS.edge";
    private static final String EDGE_SERVICE = "com.journeyOS.edge.EdgeService";
    private static final String EDGE_SERVICE_AIDL = "com.journeyOS.edge.action.EdgeService";

    private Context mContext;
    private boolean mBound = false;

    private IEdgeInterface asInterface = null;

    private EdgeServiceManager() {
        mContext = CoreManager.getDefault().getContext();
        bindEgdeService();
    }

    private static final Singleton<EdgeServiceManager> gDefault = new Singleton<EdgeServiceManager>() {
        @Override
        protected EdgeServiceManager create() {
            return new EdgeServiceManager();
        }
    };

    public static EdgeServiceManager getDefault() {
        return gDefault.get();
    }


    public void bindEgdeService() {
        NotificationManager.getDefault().startNotificationService();

        if (mBound) return;

        Intent intent = new Intent();
        intent.setPackage(EDGE_PACKAGE);
        intent.setAction(EDGE_SERVICE_AIDL);
        mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection mConnection = new ServiceConnection() {
        // Called when the connection with the service is established
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // Following the example above for an AIDL interface,
            // this gets an instance of the IRemoteInterface, which we can use to call on the service
            asInterface = IEdgeInterface.Stub.asInterface(service);
            LogUtils.i(TAG, "onServiceConnected = " + asInterface);
            mBound = true;

            boolean ball = SpUtils.getInstant().getBoolean(Constant.BALL, Constant.BALL_DEFAULT);
            if (ball) {
                showingOrHidingBall(true);
            }
        }

        // Called when the connection with the service disconnects unexpectedly
        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtils.i(TAG, "Service has unexpectedly disconnected");
            asInterface = null;
            mBound = false;
        }
    };

    public void showingOrHidingBall(boolean isShowing) {
        try {
            if (asInterface != null) asInterface.showingBall(isShowing);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void showingEdge(int direction) {
        try {
            if (asInterface != null) asInterface.showingEdge(direction);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void hidingEdge() {
        try {
            if (asInterface != null) asInterface.hidingEdge();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public boolean isEdgeRunning() {
        return mBound;
    }
}
