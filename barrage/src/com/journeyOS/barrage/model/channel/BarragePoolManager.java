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

package com.journeyOS.barrage.model.channel;

import android.content.Context;
import android.graphics.Canvas;

import com.journeyOS.barrage.control.dispatcher.IBarrageDispatcher;
import com.journeyOS.barrage.control.speed.ISpeedController;
import com.journeyOS.barrage.model.BarrageModel;
import com.journeyOS.barrage.model.collection.BarrageConsumedPool;
import com.journeyOS.barrage.model.collection.BarrageConsumer;
import com.journeyOS.barrage.model.collection.BarrageProducedPool;
import com.journeyOS.barrage.model.collection.BarrageProducer;
import com.journeyOS.barrage.model.painter.BarragePainter;
import com.journeyOS.barrage.view.IBarrageParent;

import java.util.List;


public class BarragePoolManager implements IBarragePoolManager {

    private BarrageConsumer mConsumer;
    private BarrageProducer mProducer;

    private BarrageConsumedPool mConsumedPool;
    private BarrageProducedPool mProducedPool;

    private boolean isStart;

    public BarragePoolManager(Context context, IBarrageParent parent) {
        mConsumedPool = new BarrageConsumedPool(context);
        mProducedPool = new BarrageProducedPool(context);
        mConsumer = new BarrageConsumer(mConsumedPool, parent);
        mProducer = new BarrageProducer(mProducedPool, mConsumedPool);
    }

    public void forceSleep() {
        mConsumer.forceSleep();
    }

    public void releaseForce() {
        mConsumer.releaseForce();
    }

    @Override
    public void hide(boolean hide) {
        mConsumedPool.hide(hide);
    }

    @Override
    public void hideAll(boolean hideAll) {
        mConsumedPool.hideAll(hideAll);
    }

    @Override
    public void startEngine() {
        if (!isStart) {
            isStart = true;
            mConsumer.start();
            mProducer.start();
        }
    }

    @Override
    public void setDispatcher(IBarrageDispatcher dispatcher) {
        mProducedPool.setBarrageDispatcher(dispatcher);
    }

    @Override
    public void setSpeedController(ISpeedController ISpeedController) {
        mConsumedPool.setSpeedController(ISpeedController);
    }

    @Override
    public void divide(int width, int height) {
        mProducedPool.divide(width, height);
        mConsumedPool.divide(width, height);
    }

    @Override
    public void addBarrageView(int index, BarrageModel model) {
        mProducer.produce(index, model);
    }

    @Override
    public void jumpQueue(List<BarrageModel> models) {
        mProducer.jumpQueue(models);
    }

    public void release() {
        isStart = false;
        mConsumer.release();
        mProducer.release();
        mConsumedPool = null;
    }

    /**
     * drawing entrance
     *
     * @param canvas
     */
    public void drawBarrages(Canvas canvas) {
        mConsumer.consume(canvas);
    }

    public void addPainter(BarragePainter danMuPainter, int key) {
        mConsumedPool.addPainter(danMuPainter, key);
    }

}
