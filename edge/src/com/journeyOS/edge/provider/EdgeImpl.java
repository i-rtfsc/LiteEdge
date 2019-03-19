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

import com.journeyOS.core.api.edge.IEdge;
import com.journeyOS.core.type.EdgeDirection;
import com.journeyOS.edge.EdgeServiceManager;
import com.journeyOS.edge.wm.EdgeManager;
import com.journeyOS.literouter.annotation.ARouterInject;


@ARouterInject(api = IEdge.class)
public class EdgeImpl implements IEdge {
    //    private final H mHandler = new Handler(Looper.getMainLooper());
    private final H mHandler = new H();

    private static final long DELAY_TIME = 25;
    private static final int MSG_BALL_SHOWING = 0x01;
    private static final int MSG_BALL_HIDING = 0x02;
    private static final int MSG_EDGE_SHOWING = 0x04;
    private static final int MSG_EDGE_HIDING = 0x08;

    @Override
    public void onCreate() {
    }

    @Override
    public void showingOrHidingBall(boolean isShowing) {
        //EdgeServiceManager.getDefault().showingOrHidingBall(isShowing);
        if (isShowing) {
            if (mHandler.hasMessages(MSG_BALL_SHOWING)) {
                mHandler.removeMessages(MSG_BALL_SHOWING);
            }
        } else {
            if (mHandler.hasMessages(MSG_BALL_HIDING)) {
                mHandler.removeMessages(MSG_BALL_HIDING);
            }
        }
        Message message = Message.obtain();
        message.what = isShowing ? MSG_BALL_SHOWING : MSG_BALL_HIDING;
        message.obj = isShowing;
        mHandler.sendMessageDelayed(message, DELAY_TIME);
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
        if (mHandler.hasMessages(MSG_EDGE_SHOWING)) {
            mHandler.removeMessages(MSG_EDGE_SHOWING);
        }
        Message message = Message.obtain();
        message.what = MSG_EDGE_SHOWING;
        message.obj = direction;
        mHandler.sendMessageDelayed(message, delayMillis);
    }

    private void senHiding(long delayMillis) {
        if (mHandler.hasMessages(MSG_EDGE_HIDING)) {
            mHandler.removeMessages(MSG_EDGE_HIDING);
        }
        mHandler.sendEmptyMessageDelayed(MSG_EDGE_HIDING, delayMillis);
    }

    private final class H extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_EDGE_SHOWING:
                    EdgeDirection direction = (EdgeDirection) msg.obj;
                    EdgeManager.getDefault().showEdge(direction);
                    break;
                case MSG_EDGE_HIDING:
                    EdgeManager.getDefault().hideEdge();
                    break;
                case MSG_BALL_SHOWING:
                    EdgeServiceManager.getDefault().showingOrHidingBall(true);
                    break;
                case MSG_BALL_HIDING:
                    EdgeServiceManager.getDefault().showingOrHidingBall(false);
                    break;
                default:
                    break;
            }
        }
    }
}
