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

package com.journeyOS.barrage.model.collection;

import android.content.Context;
import android.graphics.Canvas;

import com.journeyOS.barrage.control.speed.SpeedController;
import com.journeyOS.barrage.model.BarrageModel;
import com.journeyOS.barrage.model.channel.BarrageChannel;
import com.journeyOS.barrage.model.painter.BarragePainter;
import com.journeyOS.barrage.model.painter.L2RPainter;
import com.journeyOS.barrage.model.painter.R2LPainter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;


public final class BarrageConsumedPool {

    private final static int MAX_COUNT_IN_SCREEN = 30;

    private final static int DEFAULT_SINGLE_CHANNEL_HEIGHT = 40;

    private HashMap<Integer, BarragePainter> mPainters = new HashMap<>();

    private volatile ArrayList<BarrageModel> mixedModelQueue = new ArrayList<>();

    private boolean isDrawing;

    private BarrageChannel[] mChannels;

    private SpeedController mSpeedController;

    private Context context;

    public BarrageConsumedPool(Context c) {
        context = c.getApplicationContext();
        initDefaultPainters();
        hide(false);
    }

    public void setSpeedController(SpeedController mSpeedController) {
        this.mSpeedController = mSpeedController;
    }

    public void addPainter(BarragePainter painter, int key) {
        if (painter == null) {
            return;
        }
        if (!mPainters.containsKey(key)) {
            mPainters.put(key, painter);
        } else {
            throw new IllegalArgumentException("Already has the key of painter");
        }
    }

    public void hide(boolean hide) {
        Set<Integer> danMuPainters = mPainters.keySet();
        Iterator<Integer> iterator = danMuPainters.iterator();
        while (iterator.hasNext()) {
            Integer key = iterator.next();
            mPainters.get(key).hideNormal(hide);
        }
    }

    public void hideAll(boolean hide) {
        Set<Integer> danMuPainters = mPainters.keySet();
        Iterator<Integer> iterator = danMuPainters.iterator();
        while (iterator.hasNext()) {
            Integer key = iterator.next();
            mPainters.get(key).hideAll(hide);
        }
    }

    public boolean isDrawnQueueEmpty() {
        if (mixedModelQueue == null || mixedModelQueue.size() == 0) {
            isDrawing = false;
            return true;
        }
        return false;
    }

    public void put(ArrayList<BarrageModel> danMuViews) {
//        if (!isDrawing) { // 这里的判断是为了控制弹幕的发送数量
        if (danMuViews != null && danMuViews.size() > 0) {
            mixedModelQueue.addAll(danMuViews);
        }
//        }
    }

    public void draw(Canvas canvas) {
        drawEveryElement(mixedModelQueue, canvas);
    }

    private synchronized void drawEveryElement(ArrayList<BarrageModel> danMuViewQueue, Canvas canvas) {
        isDrawing = true;
        if (danMuViewQueue == null || danMuViewQueue.size() == 0) {
            return;
        }

        for (int i = 0; i < (danMuViewQueue.size() > MAX_COUNT_IN_SCREEN ? MAX_COUNT_IN_SCREEN : danMuViewQueue.size()); i++) {
            BarrageModel danMuView = danMuViewQueue.get(i);
            if (danMuView.isAlive()) {
                BarragePainter danMuPainter = getPainter(danMuView);
                BarrageChannel danMuChannel = mChannels[danMuView.getChannelIndex()];
                danMuChannel.dispatch(danMuView);
                if (danMuView.isAttached()) {
                    performDraw(danMuView, danMuPainter, canvas, danMuChannel);
                }
            } else {
                danMuViewQueue.remove(i);
                i--;
            }
        }
        isDrawing = false;
    }


    private void initDefaultPainters() {
        R2LPainter r2LPainter = new R2LPainter();
        L2RPainter l2RPainter = new L2RPainter();
        mPainters.put(BarrageModel.LEFT_TO_RIGHT, l2RPainter);
        mPainters.put(BarrageModel.RIGHT_TO_LEFT, r2LPainter);
    }

    private BarragePainter getPainter(BarrageModel danMuView) {
        int painterType = danMuView.getDisplayType();
        return mPainters.get(painterType);
    }

    private void performDraw(BarrageModel danMuView, BarragePainter danMuPainter, Canvas canvas, BarrageChannel danMuChannel) {
        danMuPainter.execute(canvas, danMuView, danMuChannel);
    }

    public void divide(int width, int height) {
//        int singleHeight = DimensionUtil.dpToPx(mContext, DEFAULT_SINGLE_CHANNEL_HEIGHT);
//        int count = height / singleHeight;
        int singleHeight = 105;
        int count = 1;

        mChannels = new BarrageChannel[count];
        for (int i = 0; i < count; i++) {
            BarrageChannel danMuChannel = new BarrageChannel();
            danMuChannel.width = width;
            danMuChannel.height = singleHeight;
//            danMuChannel.speed = mSpeedController.getSpeed();

            danMuChannel.topY = i * singleHeight;
//            danMuChannel.space = selectSpaceRandomly();
            mChannels[i] = danMuChannel;
        }
    }

    private int selectSpaceRandomly() {
        return (int) (Math.random() * 20 + 15);
    }

}
