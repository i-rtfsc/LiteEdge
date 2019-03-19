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

import android.os.Handler;
import android.os.Message;

import com.journeyOS.barrage.model.BarrageModel;

import java.util.ArrayList;
import java.util.List;


public class BarrageProducer {

    private BarrageConsumedPool mConsumedPool;

    private BarrageProducedPool mProducedPool;

    private ProducerHandler mProducerHandler;

    public BarrageProducer(BarrageProducedPool producedPool, BarrageConsumedPool consumedPool) {
        this.mConsumedPool = consumedPool;
        this.mProducedPool = producedPool;
    }

    public void start() {
        mProducerHandler = new ProducerHandler(this);
    }

    public void produce(int index, BarrageModel model) {
        if (mProducerHandler != null) {
            ProduceMessage produceMessage = new ProduceMessage();
            produceMessage.index = index;
            produceMessage.model = model;
            Message message = mProducerHandler.obtainMessage();
            message.obj = produceMessage;
            message.what = 2;
            mProducerHandler.sendMessage(message);
        }
    }

    public void jumpQueue(List<BarrageModel> danMuViews) {
        mProducedPool.jumpQueue(danMuViews);
    }

    public void release() {
        mConsumedPool = null;
        if (mProducerHandler != null) {
            mProducerHandler.removeMessages(1);
            mProducerHandler.release();
        }
    }

    static class ProducerHandler extends Handler {

        private final int SLEEP_TIME = 100;

        private BarrageProducer mProducer;

        ProducerHandler(BarrageProducer danMuProducer) {
            this.mProducer = danMuProducer;
            obtainMessage(1).sendToTarget();
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    if (mProducer != null && mProducer.mConsumedPool != null) {
                        if (mProducer.mProducedPool != null) {
                            ArrayList<BarrageModel> models = mProducer.mProducedPool.dispatch();
                            if (models != null) {
                                mProducer.mConsumedPool.put(models);
                            }
                        }
                        Message message = obtainMessage();
                        message.what = 1;
                        sendMessageDelayed(message, SLEEP_TIME);
                    }
                    break;
                case 2:
                    if (mProducer != null && msg.obj instanceof ProduceMessage) {
                        ProduceMessage produceMessage = (ProduceMessage) msg.obj;
                        mProducer.mProducedPool.addBarrageView(produceMessage.index, produceMessage.model);
                    }
                    break;
            }
        }

        public void release() {
            if (mProducer != null) {
                if (mProducer.mProducedPool != null) {
                    mProducer.mProducedPool.clear();
                }
                mProducer = null;
            }
        }

    }

    static class ProduceMessage {
        public int index;
        public BarrageModel model;
    }

}

