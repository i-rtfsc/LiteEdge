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

import com.journeyOS.barrage.control.dispatcher.IBarrageDispatcher;
import com.journeyOS.barrage.model.BarrageModel;
import com.journeyOS.barrage.model.channel.BarrageChannel;
import com.journeyOS.barrage.model.utils.DimensionUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;


public class BarrageProducedPool {

    private final static int MAX_COUNT_IN_SCREEN = 30;

    private final static int DEFAULT_SINGLE_CHANNEL_HEIGHT = 40;

    private IBarrageDispatcher mDispatcher;

    private volatile ArrayList<BarrageModel> vMixedPendingQueue = new ArrayList<>();

    private volatile ArrayList<BarrageModel> vFastPendingQueue = new ArrayList<>();

    private ReentrantLock mReentrantLock = new ReentrantLock();

    private BarrageChannel[] mChannels;

    private Context mContext;

    public BarrageProducedPool(Context context) {
        this.mContext = context.getApplicationContext();
    }

    public void setBarrageDispatcher(IBarrageDispatcher dispatcher) {
        this.mDispatcher = dispatcher;
    }

    public void addBarrageView(int index, BarrageModel model) {
        mReentrantLock.lock();
        try {
            if (index > -1) {
                vMixedPendingQueue.add(index, model);
            } else {
                vMixedPendingQueue.add(model);
            }
        } finally {
            mReentrantLock.unlock();
        }
    }

    public void jumpQueue(List<BarrageModel> models) {
        mReentrantLock.lock();
        try {
            vFastPendingQueue.addAll(models);
        } finally {
            mReentrantLock.unlock();
        }
    }

    public synchronized ArrayList<BarrageModel> dispatch() {
        if (isEmpty()) {
            return null;
        }
        ArrayList<BarrageModel> models = vFastPendingQueue.size() > 0 ? vFastPendingQueue : vMixedPendingQueue;
        ArrayList<BarrageModel> validateModels = new ArrayList<>();
        for (int i = 0; i < (models.size() > MAX_COUNT_IN_SCREEN ? MAX_COUNT_IN_SCREEN : models.size()); i++) {
            BarrageModel model = models.get(i);
            mDispatcher.dispatch(model, mChannels);
            validateModels.add(model);
            models.remove(i);
            i--;
        }

        if (validateModels.size() > 0) {
            return validateModels;
        }
        return null;
    }

    public boolean isEmpty() {
        return vFastPendingQueue.size() == 0 && vMixedPendingQueue.size() == 0;
    }

    public void divide(int width, int height) {
//        int singleHeight = DimensionUtil.dpToPx(mContext, DEFAULT_SINGLE_CHANNEL_HEIGHT);
//        int count = height / singleHeight;
        int singleHeight = 105;
        int count = 1;

        mChannels = new BarrageChannel[count];
        for (int i = 0; i < count; i++) {
            BarrageChannel channel = new BarrageChannel();
            channel.width = width;
            channel.height = singleHeight;
            channel.topY = i * singleHeight;
            mChannels[i] = channel;
        }
    }

    public void clear() {
        vFastPendingQueue.clear();
        vMixedPendingQueue.clear();
        mContext = null;
    }
}
