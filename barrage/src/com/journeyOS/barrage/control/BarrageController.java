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

package com.journeyOS.barrage.control;

import android.graphics.Canvas;
import android.view.View;

import com.journeyOS.barrage.control.dispatcher.BarrageDispatcher;
import com.journeyOS.barrage.control.speed.SpeedController;
import com.journeyOS.barrage.control.speed.ISpeedController;
import com.journeyOS.barrage.model.BarrageModel;
import com.journeyOS.barrage.model.channel.BarragePoolManager;
import com.journeyOS.barrage.model.painter.BarragePainter;
import com.journeyOS.barrage.view.IBarrageParent;

import java.util.List;


public final class BarrageController {

    private BarragePoolManager mManager;
    private BarrageDispatcher mDispatcher;
    private ISpeedController mISpeedController;
    private boolean mChannelCreated = false;

    public BarrageController(View view) {
        if (mISpeedController == null) {
            mISpeedController = new SpeedController();
        }
        if (mManager == null) {
            mManager = new BarragePoolManager(view.getContext(), (IBarrageParent) view);
        }
        if (mDispatcher == null) {
            mDispatcher = new BarrageDispatcher(view.getContext());
        }
        mManager.setDispatcher(mDispatcher);
    }

    public void forceSleep() {
        mManager.forceSleep();
    }

    public void forceWake() {
        if (mManager != null) {
            mManager.releaseForce();
        }
    }

    public void setSpeedController(ISpeedController ISpeedController) {
        if (ISpeedController != null) {
            this.mISpeedController = ISpeedController;
        }
    }

    public void prepare() {
        mManager.startEngine();
    }

    public void addBarrageView(int index, BarrageModel danMuView) {
        mManager.addBarrageView(index, danMuView);
    }

    public void jumpQueue(List<BarrageModel> danMuViews) {
        mManager.jumpQueue(danMuViews);
    }

    public void addPainter(BarragePainter danMuPainter, int key) {
        mManager.addPainter(danMuPainter, key);
    }

    public boolean isChannelCreated() {
        return mChannelCreated;
    }

    public void hide(boolean hide) {
        if (mManager != null) {
            mManager.hide(hide);
        }
    }

    public void hideAll(boolean hideAll) {
        if (mManager != null) {
            mManager.hideAll(hideAll);
        }
    }

    public void initChannels(Canvas canvas) {
        if (!mChannelCreated) {
            mISpeedController.setWidthPixels(canvas.getWidth());
            mManager.setSpeedController(mISpeedController);
            mManager.divide(canvas.getWidth(), canvas.getHeight());
            mChannelCreated = true;
        }
    }

    public void draw(Canvas canvas) {
        mManager.drawBarrages(canvas);
    }

    public void release() {
        if (mManager != null) {
            mManager.release();
            mManager = null;
        }
        if (mDispatcher != null) {
            mDispatcher.release();
        }
    }
}
