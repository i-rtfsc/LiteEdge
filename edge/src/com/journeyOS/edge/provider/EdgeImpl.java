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

import android.os.Message;

import com.journeyOS.core.api.edge.IEdge;
import com.journeyOS.core.type.EdgeDirection;
import com.journeyOS.edge.H;
import com.journeyOS.edge.wm.BallManager;
import com.journeyOS.literouter.annotation.ARouterInject;


@ARouterInject(api = IEdge.class)
public class EdgeImpl implements IEdge {
    private final H mHandler = H.getDefault().getHandler();

    @Override
    public void onCreate() {
    }

    @Override
    public void showingOrHidingBall(boolean isShowing) {
        //EdgeServiceManager.getDefault().showingOrHidingBall(isShowing);
        if (isShowing) {
            if (mHandler.hasMessages(H.MSG_BALL_SHOWING)) {
                mHandler.removeMessages(H.MSG_BALL_SHOWING);
            }
        } else {
            if (mHandler.hasMessages(H.MSG_BALL_HIDING)) {
                mHandler.removeMessages(H.MSG_BALL_HIDING);
            }
        }
        Message message = Message.obtain();
        message.what = isShowing ? H.MSG_BALL_SHOWING : H.MSG_BALL_HIDING;
        message.obj = isShowing;
        mHandler.sendMessageDelayed(message, H.EDGE_DELAY_TIME);
    }

    @Override
    public void updateInnerBall(int color) {
        BallManager.getDefault().updateInnerBall(color);
    }

    @Override
    public void showingEdge(int direction) {
        EdgeDirection ed = EdgeDirection.valueOf(direction);
        sendShowing(ed, H.EDGE_DELAY_TIME);
    }

    @Override
    public void showingEdge(int direction, long delayMillis) {
        EdgeDirection ed = EdgeDirection.valueOf(direction);
        sendShowing(ed, delayMillis);
    }

    @Override
    public void showingEdge(final EdgeDirection direction) {
        sendShowing(direction, H.EDGE_DELAY_TIME);
    }

    @Override
    public void showingEdge(EdgeDirection direction, long delayMillis) {
        sendShowing(direction, delayMillis);
    }

    @Override
    public void hidingEdge(boolean isAnimator) {
        senHiding(isAnimator, H.EDGE_DELAY_TIME);
    }

    @Override
    public void hidingEdge(boolean isAnimator, long delayMillis) {
        senHiding(isAnimator, delayMillis);
    }

    private void sendShowing(EdgeDirection direction, long delayMillis) {
        if (mHandler.hasMessages(H.MSG_EDGE_SHOWING)) {
            mHandler.removeMessages(H.MSG_EDGE_SHOWING);
        }
        Message message = Message.obtain();
        message.what = H.MSG_EDGE_SHOWING;
        message.obj = direction;
        mHandler.sendMessageDelayed(message, delayMillis);
    }

    private void senHiding(boolean isAnimator, long delayMillis) {
        if (mHandler.hasMessages(H.MSG_EDGE_HIDING)) {
            mHandler.removeMessages(H.MSG_EDGE_HIDING);
        }
        Message message = Message.obtain();
        message.what = H.MSG_EDGE_HIDING;
        message.obj = isAnimator;
        mHandler.sendMessageDelayed(message, delayMillis);
    }


}
