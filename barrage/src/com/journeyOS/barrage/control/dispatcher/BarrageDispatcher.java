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

package com.journeyOS.barrage.control.dispatcher;

import android.content.Context;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;

import com.journeyOS.barrage.model.BarrageModel;
import com.journeyOS.barrage.model.channel.BarrageChannel;
import com.journeyOS.barrage.model.utils.PaintUtils;

import java.util.Random;

public class BarrageDispatcher implements IBarrageDispatcher {

    private Context mContext;
    protected TextPaint mPaint;
    private Random mRandom = new Random();

    public BarrageDispatcher(Context context) {
        this.mContext = context;
        mPaint = PaintUtils.getPaint();
    }

    @Override
    public synchronized void dispatch(BarrageModel model, BarrageChannel[] channels) {
        if (!model.isAttached() && channels != null) {
            int index = selectChannelRandomly(channels);
            model.selectChannel(index);
            BarrageChannel danMuChannel = channels[index];
            if (danMuChannel == null) {
                return;
            }

            measure(model, danMuChannel);
        }
    }

    private int selectChannelRandomly(BarrageChannel[] danMuChannels) {
        return mRandom.nextInt(danMuChannels.length);
    }

    private void measure(BarrageModel model, BarrageChannel danMuChannel) {
        if (model.isMeasured()) {
            return;
        }

        CharSequence text = model.text;
        if (!TextUtils.isEmpty(text)) {
            mPaint.setTextSize(model.textSize);
            StaticLayout staticLayout = new StaticLayout(text,
                    mPaint,
                    (int) Math.ceil(StaticLayout.getDesiredWidth(text, mPaint)),
                    Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);

            float textWidth = model.getX()
                    + model.marginLeft
                    + model.avatarWidth
                    + model.levelMarginLeft
                    + model.levelBitmapWidth
                    + model.textMarginLeft
                    + staticLayout.getWidth()
                    + model.textBackgroundPaddingRight;
            model.setWidth((int) textWidth);

            float textHeight = staticLayout.getHeight()
                    + model.textBackgroundPaddingTop
                    + model.textBackgroundPaddingBottom;
            if (model.avatar != null && model.avatarHeight > textHeight) {
                model.setHeight((int) (model.getY() + model.avatarHeight));
            } else {
                model.setHeight((int) (model.getY() + textHeight));
            }
        }

        if (model.getDisplayType() == BarrageModel.RIGHT_TO_LEFT) {
            model.setStartPositionX(danMuChannel.width);
        } else if (model.getDisplayType() == BarrageModel.LEFT_TO_RIGHT) {
            model.setStartPositionX(-model.getWidth());
        }

        model.setMeasured(true);
        model.setStartPositionY(danMuChannel.topY);
        model.setAlive(true);
    }

    public void release() {
        mContext = null;
    }
}
