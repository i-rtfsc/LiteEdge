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

package com.journeyOS.base.barrage;

import android.content.Context;
import android.os.Handler;
import android.widget.RelativeLayout;

import com.journeyOS.base.utils.LogUtils;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BarrageDispatcher implements Runnable {
    private static final String TAG = BarrageDispatcher.class.getSimpleName();

    private static List<OnBarrageStateChangeListener> mListeners = new CopyOnWriteArrayList<OnBarrageStateChangeListener>();

    private Context mContext;
    private RelativeLayout mSrcLayout;

    private static int BARRAGE_NUM = 100;
    private BarrageView[] queue = new BarrageView[BARRAGE_NUM];
    private boolean running = false;

    private Handler mHandler = new Handler();

    private int mBarrageNum = 0;
    private int mBarrageIndex = 0;

    public BarrageDispatcher(Context context, RelativeLayout srcLayout) {
        mContext = context;
        mSrcLayout = srcLayout;

        for (int i = 0; i < BARRAGE_NUM; i++) {
            queue[i] = new BarrageView(context);
        }
    }

    public void send(BarrageModel model) {
        try {
            if (model.title != null && model.content != null) {
                if (!queue[mBarrageIndex].availiable) {
                    mSrcLayout.addView(queue[mBarrageIndex].getView());
                    mBarrageNum++;
                }
                queue[mBarrageIndex].load(model);
                mBarrageIndex++;
                if (mBarrageIndex == BARRAGE_NUM) {
                    mBarrageIndex = 0;
                }
                if (!running) {
                    for (OnBarrageStateChangeListener listener : mListeners) {
                        listener.onBarrageAttachedToWindow();
                    }
                    mHandler.post(this);
                }
            }
        } catch (Exception e) {
            LogUtils.i(TAG, "send barrage error = " + e);
        }
    }

    public void clear() {
        for (int i = 0; i < BARRAGE_NUM; i++) {
            if (queue[i].availiable) {
                queue[i].clear();
            }
        }
    }

    @Override
    public void run() {
        running = true;
        for (int i = 0; i < BARRAGE_NUM; i++) {
            if (queue[i].availiable) {
                if (!queue[i].move()) {
                    mBarrageNum--;
                    mSrcLayout.removeView(queue[i].getView());
                }
            }
        }
        if (mBarrageNum > 0) {
            mHandler.postDelayed(this, 10);
        } else {
            running = false;
            for (OnBarrageStateChangeListener listener : mListeners) {
                listener.onBarrageDetachedFromWindow();
            }
        }
    }

    public void registerChangedListener(OnBarrageStateChangeListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener should not be null");
        }

        if (!mListeners.contains(listener)) {
            mListeners.add(listener);
        }
    }

    public void unregisterChangedListener(OnBarrageStateChangeListener listener) {
        if (listener == null) {
            throw new IllegalArgumentException("listener should not be null");
        }

        if (mListeners.contains(listener)) {
            mListeners.remove(listener);
        }
    }

}
