/*
 * Copyright (c) 2019 anqi.huang@outlook.com
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

package com.journeyOS.barrage;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.journeyOS.barrage.control.BarrageController;
import com.journeyOS.barrage.control.speed.SpeedController;
import com.journeyOS.barrage.model.BarrageModel;
import com.journeyOS.barrage.view.IBarrageParent;
import com.journeyOS.barrage.view.OnBarrageParentViewTouchListener;
import com.journeyOS.barrage.view.OnBarrageViewTouchListener;

import java.util.ArrayList;
import java.util.List;


public class BarrageView extends View implements IBarrageParent {

    private BarrageController mController;
    private volatile ArrayList<OnBarrageViewTouchListener> mTouchListeners;
    private OnBarrageParentViewTouchListener mParentTouchListener;
    private boolean drawFinished = false;

    private Object lock = new Object();

    private final H mHandler = new H();

    private static final long DELAY_TIME = 100;
    private static final int MSG_SHOWING = 0x01;
    private static final int MSG_HIDING = 0x02;

    public BarrageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    @Override
    public void jumpQueue(List<BarrageModel> models) {
        mController.jumpQueue(models);
    }

    @Override
    public void addAllTouchListener(List<BarrageModel> models) {
        this.mTouchListeners.addAll(models);
    }

    public BarrageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mTouchListeners = new ArrayList<>();
        if (mController == null) {
            mController = new BarrageController(this);
        }
    }

    public void prepare() {
        prepare(null);
    }

    public void prepare(SpeedController speedController) {
        if (mController != null) {
            mController.setSpeedController(speedController);
            mController.prepare();
        }
    }

    public void release() {
        onDetectHasCanTouchedDanMusListener = null;
        mParentTouchListener = null;
        clear();
        if (mController != null) {
            mController.release();
        }
        mController = null;
    }

    private void addBarrageView(final BarrageModel model) {
        if (model == null) {
            return;
        }
        if (mController != null) {
            if (model.enableTouch()) {
                mTouchListeners.add(model);
            }
            mController.addBarrageView(-1, model);
        }
    }

    public void setParentTouchListener(OnBarrageParentViewTouchListener listener) {
        this.mParentTouchListener = listener;
    }

    @Override
    public boolean hasCanTouch() {
        return mTouchListeners.size() > 0;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (hasCanTouch()) {
            getParent().requestDisallowInterceptTouchEvent(true);
        }
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                int size = mTouchListeners.size();
                for (int i = 0; i < size; i++) {
                    OnBarrageViewTouchListener onDanMuViewTouchListener = mTouchListeners.get(i);
                    boolean onTouched = onDanMuViewTouchListener.onTouch(event.getX(), event.getY());
                    if (((BarrageModel) onDanMuViewTouchListener).getOnTouchCallBackListener() != null && onTouched) {
                        ((BarrageModel) onDanMuViewTouchListener).getOnTouchCallBackListener().callBack((BarrageModel) onDanMuViewTouchListener);
                        return true;
                    }
                }
                if (!hasCanTouch()) {
                    if (mParentTouchListener != null) {
                        mParentTouchListener.callBack();
                    }
                } else {
                    if (mParentTouchListener != null) {
                        mParentTouchListener.hideControlPanel();
                    }
                }
                break;
        }
        return true;
    }

    @Override
    public void add(BarrageModel model) {
        model.enableMoving(true);
        addBarrageView(model);
    }

    public void lockDraw() {
        if (!mController.isChannelCreated()) {
            return;
        }
        synchronized (lock) {
            if (Build.VERSION.SDK_INT >= 16) {
                this.postInvalidateOnAnimation();
            } else {
                this.postInvalidate();
            }
            if ((!drawFinished)) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                }
            }
            drawFinished = false;
            sendHiding();
        }
    }

    @Override
    public void forceSleep() {
        mController.forceSleep();
    }

    @Override
    public void forceWake() {
        mController.forceWake();
    }

    private void unLockDraw() {
        synchronized (lock) {
            drawFinished = true;
            lock.notifyAll();
        }
    }

    @Override
    public void clear() {
        mTouchListeners.clear();
    }

    @Override
    public void remove(BarrageModel model) {
        mTouchListeners.remove(model);
    }

    public void detectHasCanTouchedDanMus() {
        for (int i = 0; i < mTouchListeners.size(); i++) {
            if (!((BarrageModel) mTouchListeners.get(i)).isAlive()) {
                mTouchListeners.remove(i);
                i--;
            }
        }
        if (mTouchListeners.size() == 0) {
            if (onDetectHasCanTouchedDanMusListener != null) {
                onDetectHasCanTouchedDanMusListener.hasNoCanTouchedDanMus(false);
            }
        } else {
            if (onDetectHasCanTouchedDanMusListener != null) {
                onDetectHasCanTouchedDanMusListener.hasNoCanTouchedDanMus(true);
            }
        }
    }

    @Override
    public void hideNormalBarrageView(boolean hide) {
        mController.hide(hide);
    }

    @Override
    public void hideAllBarrageView(boolean hideAll) {
        mController.hideAll(hideAll);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        detectHasCanTouchedDanMus();
        if (mController != null) {
            mController.initChannels(canvas);
            mController.draw(canvas);
        }
        unLockDraw();
    }

    @Override
    public void add(int index, BarrageModel model) {
        mController.addBarrageView(index, model);
    }

    public OnDetectHasCanTouchedDanMusListener onDetectHasCanTouchedDanMusListener;

    public void setOnBarrageExistListener(OnDetectHasCanTouchedDanMusListener onDetectHasCanTouchedDanMusListener) {
        this.onDetectHasCanTouchedDanMusListener = onDetectHasCanTouchedDanMusListener;
    }

    public interface OnDetectHasCanTouchedDanMusListener {
        void hasNoCanTouchedDanMus(boolean hasDanMus);
    }

    public OnBarrageAttachStateChangeListenerListener mAttachStateListener;

    public void setOnBarrageAttachStateChangeListenerListener(OnBarrageAttachStateChangeListenerListener listener) {
        this.mAttachStateListener = listener;
    }

    public interface OnBarrageAttachStateChangeListenerListener {
        void onShowing();

        void onHiding();
    }

    private void sendShowing() {
        if (mHandler.hasMessages(MSG_SHOWING)) {
            mHandler.removeMessages(MSG_SHOWING);
        }
        Message message = Message.obtain();
        message.what = MSG_SHOWING;
        mHandler.sendMessageDelayed(message, DELAY_TIME);
    }

    private void sendHiding() {
        if (mHandler.hasMessages(MSG_HIDING)) {
            mHandler.removeMessages(MSG_HIDING);
        }
        Message message = Message.obtain();
        message.what = MSG_HIDING;
        mHandler.sendMessageDelayed(message, DELAY_TIME);
    }

    private final class H extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SHOWING:
                    if (mAttachStateListener != null) {
                        mAttachStateListener.onShowing();
                    }
                    break;
                case MSG_HIDING:
                    if (mAttachStateListener != null) {
                        mAttachStateListener.onHiding();
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
