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

package com.journeyOS.edge.provider;

import android.os.Handler;
import android.os.Message;

import com.journeyOS.core.api.edge.IEdgeApi;
import com.journeyOS.core.type.EdgeDirection;
import com.journeyOS.edge.wm.EdgeManager;
import com.journeyOS.literouter.annotation.ARouterInject;
import com.journeyOS.litetask.TaskScheduler;


@ARouterInject(api = IEdgeApi.class)
public class EdgeImpl implements IEdgeApi {
    private final Handler mHandler = TaskScheduler.getInstance().getMainHandler();
    private static final long DELAY_TIME = 25;
    private static final int MSG_SHOWING = 1;
    private static final int MSG_HIDING = 2;

    @Override
    public void onCreate() {
        TaskScheduler.getInstance().setOnMessageListener(mHandler, new TaskScheduler.OnMessageListener() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_SHOWING:
                        EdgeDirection direction = (EdgeDirection) msg.obj;
                        EdgeManager.getDefault().showEdge(direction);
                        break;
                    case MSG_HIDING:
                        EdgeManager.getDefault().hideEdge();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void showingEdge(int direction) {
        EdgeDirection ed = EdgeDirection.valueOf(direction);
        sendShowing(ed, DELAY_TIME);
    }

    @Override
    public void showingEdge(int direction, long delayMillis) {
        EdgeDirection ed = EdgeDirection.valueOf(direction);
        sendShowing(ed, delayMillis);
    }

    @Override
    public void showingEdge(final EdgeDirection direction) {
        sendShowing(direction, DELAY_TIME);
    }

    @Override
    public void showingEdge(EdgeDirection direction, long delayMillis) {
        sendShowing(direction, delayMillis);
    }

    @Override
    public void hidingEdge() {
        senHiding(DELAY_TIME);
    }

    @Override
    public void hidingEdge(long delayMillis) {
        senHiding(delayMillis);
    }

    private void sendShowing(EdgeDirection direction, long delayMillis) {
        if (mHandler.hasMessages(MSG_SHOWING)) {
            mHandler.removeMessages(MSG_SHOWING);
        }
        Message message = Message.obtain();
        message.what = MSG_SHOWING;
        message.obj = direction;
        mHandler.sendMessageDelayed(message, delayMillis);
    }

    private void senHiding(long delayMillis) {
        if (mHandler.hasMessages(MSG_HIDING)) {
            mHandler.removeMessages(MSG_HIDING);
        }
        mHandler.sendEmptyMessageDelayed(MSG_HIDING, delayMillis);
    }
}
