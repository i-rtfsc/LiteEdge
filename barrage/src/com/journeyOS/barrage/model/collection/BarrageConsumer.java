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

import android.graphics.Canvas;

import com.journeyOS.barrage.view.IBarrageParent;

import java.lang.ref.WeakReference;
import java.util.concurrent.locks.ReentrantLock;

public class BarrageConsumer extends Thread {

    private final static int SLEEP_TIME = 100;

    private boolean forceSleep = false;

    private boolean isStart;

    private volatile WeakReference<IBarrageParent> danMuViewParent;

    private BarrageConsumedPool mSharedPool;

    private ReentrantLock lock = new ReentrantLock();

    public BarrageConsumer(BarrageConsumedPool sharedPool, IBarrageParent parent) {
        this.mSharedPool = sharedPool;
        this.danMuViewParent = new WeakReference<>(parent);
        isStart = true;
    }

    public void consume(final Canvas canvas) {
        if (mSharedPool != null) {
            mSharedPool.draw(canvas);
        }
    }

    public void release() {
        isStart = false;
        danMuViewParent.clear();
        interrupt();
        mSharedPool = null;
    }

    @Override
    public void run() {
        super.run();
        while (isStart) {
            if (mSharedPool.isDrawnQueueEmpty() || forceSleep) {
                try {
                    Thread.sleep(SLEEP_TIME);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                lock.lock();
                try {
                    if (danMuViewParent != null && danMuViewParent.get() != null) {
                        danMuViewParent.get().lockDraw();
                    }
                } finally {
                    lock.unlock();
                }
            }
        }
    }

    public void forceSleep() {
        forceSleep = true;
    }

    public void releaseForce() {
        forceSleep = false;
    }
}
