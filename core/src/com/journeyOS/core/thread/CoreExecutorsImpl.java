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

package com.journeyOS.core.thread;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.core.api.thread.ICoreExecutors;
import com.journeyOS.literouter.annotation.ARouterInject;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;


@ARouterInject(api = ICoreExecutors.class)
public class CoreExecutorsImpl implements ICoreExecutors {
    private static final String TAG = CoreExecutorsImpl.class.getSimpleName();
    CoreExecutors mCoreExecutors;

    private Map<String, Handler> mHandlerMap = new ConcurrentHashMap<>();

    @Override
    public void onCreate() {
        mCoreExecutors = new CoreExecutors();
    }

    @Override
    public Executor diskIOThread() {
        return mCoreExecutors.diskIO();
    }

    @Override
    public Executor networkIOThread() {
        return mCoreExecutors.networkIO();
    }

    @Override
    public Executor mainThread() {
        return mCoreExecutors.mainThread();
    }

    @Override
    public Handler handler() {
        return new Handler(Looper.getMainLooper());
    }

    @Override
    public Handler getHandle(String handlerName) {
        if ((mHandlerMap.size() != 0) && mHandlerMap.containsKey(handlerName)) {
            return mHandlerMap.get(handlerName);
        }

        HandlerThread handlerThread = new HandlerThread(handlerName, Process.THREAD_PRIORITY_BACKGROUND);
        handlerThread.start();
        SafeDispatchHandler handler = new SafeDispatchHandler(handlerThread.getLooper());
        mHandlerMap.put(handlerName, handler);
        return handler;
    }

    @Override
    public void setOnMessageListener(Handler handler, final OnMessageListener listener) {
        if (handler == null || listener == null) {
            LogUtils.w(TAG, "handler or listener was NULL!");
            return;
        }

        if (handler instanceof SafeDispatchHandler) {
            SafeDispatchHandler safeHandler = (SafeDispatchHandler) handler;
            safeHandler.setOnHandleMessageListener(new SafeDispatchHandler.OnHandleMessageListener() {
                @Override
                public void handleMessage(Message msg) {
                    listener.handleMessage(msg);
                }
            });
        } else {
            LogUtils.w(TAG, "this handler wasn't SafeDispatchHandler");
        }
    }


    private static class SafeDispatchHandler extends Handler {
        private static final String TAG = SafeDispatchHandler.class.getCanonicalName();

        public SafeDispatchHandler(Looper looper) {
            super(looper);
        }

        public SafeDispatchHandler(Looper looper, Callback callback) {
            super(looper, callback);
        }

        public SafeDispatchHandler() {
            super();
        }

        public SafeDispatchHandler(Callback callback) {
            super(callback);
        }

        @Override
        public void dispatchMessage(Message msg) {
            try {
                super.dispatchMessage(msg);
            } catch (Exception e) {
                Log.d(TAG, "dispatchMessage Exception " + msg + " , " + e);
            } catch (Error error) {
                Log.d(TAG, "dispatchMessage error " + msg + " , " + error);
            }
        }

        @Override
        public void handleMessage(Message msg) {
            try {
                super.handleMessage(msg);
                if (mListener != null) mListener.handleMessage(msg);
            } catch (Exception e) {
                Log.d(TAG, "handleMessage Exception " + msg + " , " + e);
            } catch (Error error) {
                Log.d(TAG, "handleMessage error " + msg + " , " + error);
            }
        }

        private static OnHandleMessageListener mListener;

        protected void setOnHandleMessageListener(OnHandleMessageListener listener) {
            mListener = listener;
        }

        protected interface OnHandleMessageListener {
            void handleMessage(Message msg);
        }
    }

    public interface OnMessageListener {
        void handleMessage(Message msg);
    }

}
