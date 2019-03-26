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

package com.journeyOS.barrage.model.painter;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;

import com.journeyOS.barrage.model.BarrageModel;
import com.journeyOS.barrage.model.channel.BarrageChannel;
import com.journeyOS.barrage.model.utils.PaintUtils;


public class BarragePainter extends IBarragePainter {

    protected static TextPaint sPaint;
    protected static RectF sRectF;

    private boolean hide;

    private boolean hideAll;

    static {
        sPaint = PaintUtils.getPaint();
        sRectF = new RectF();
    }

    public BarragePainter() {
    }

    protected void layout(BarrageModel model, BarrageChannel channel) {
    }

    private void onLayout(BarrageModel model, BarrageChannel channel) {
        if (model.isMoving()) {
            layout(model, channel);
        }
    }

    protected void draw(Canvas canvas, BarrageModel model, BarrageChannel channel) {
        if (model.textBackground != null) {
            drawTextBackground(model, canvas, channel);
        }

        if (model.avatar != null) {
            drawAvatar(model, canvas, channel);
        }

        if (model.avatarStrokes) {
            drawAvatarStrokes(model, canvas, channel);
        }

        if (model.levelBitmap != null) {
            drawLevel(model, canvas, channel);
        }

        if (!TextUtils.isEmpty(model.levelText)) {
            drawLevelText(model, canvas, channel);
        }

        if (!TextUtils.isEmpty(model.text)) {
            drawText(model, canvas, channel);
        }
    }

    protected void drawAvatar(BarrageModel model, Canvas canvas, BarrageChannel channel) {
        float top = (int) (model.getY()) + channel.height / 2 - model.avatarHeight / 2;
        float x = model.getX() + model.marginLeft;

        sRectF.set((int) x, top,
                (int) (x + model.avatarWidth),
                top + model.avatarHeight);
        canvas.drawBitmap(model.avatar, null, sRectF, sPaint);
    }

    protected void drawAvatarStrokes(BarrageModel model, Canvas canvas, BarrageChannel channel) {
        float x = model.getX() + model.marginLeft + model.avatarWidth / 2;
        float top = model.getY() + channel.height / 2;

        sPaint.setColor(Color.WHITE);
        sPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle((int) x, (int) top, model.avatarHeight / 2, sPaint);
    }

    protected void drawLevel(BarrageModel model, Canvas canvas, BarrageChannel channel) {
        float top = (int) (model.getY()) + channel.height / 2 - model.levelBitmapHeight / 2;

        float x = model.getX()
                + model.marginLeft
                + model.avatarWidth
                + model.levelMarginLeft;

        sRectF.set((int) x, top,
                (int) (x + model.levelBitmapWidth),
                top + model.levelBitmapHeight);
        canvas.drawBitmap(model.levelBitmap, null, sRectF, sPaint);
    }

    protected void drawLevelText(BarrageModel model, Canvas canvas, BarrageChannel channel) {
        if (TextUtils.isEmpty(model.levelText)) {
            return;
        }

        sPaint.setTextSize(model.levelTextSize);
        sPaint.setColor(model.levelTextColor);
        sPaint.setStyle(Paint.Style.FILL);

        float top = (int) model.getY()
                + channel.height / 2
                - sPaint.ascent() / 2
                - sPaint.descent() / 2;

        float x = model.getX()
                + model.marginLeft
                + model.avatarWidth
                + model.levelMarginLeft
                + model.levelBitmapWidth / 2;

        canvas.drawText(model.levelText.toString(), (int) x, top, sPaint);
    }

    protected void drawText(BarrageModel model, Canvas canvas, BarrageChannel channel) {
        if (TextUtils.isEmpty(model.text)) {
            return;
        }

        sPaint.setTextSize(model.textSize);
        sPaint.setColor(model.textColor);
        sPaint.setStyle(Paint.Style.FILL);

        CharSequence text = model.text;
        StaticLayout staticLayout = new StaticLayout(text,
                sPaint,
                (int) Math.ceil(StaticLayout.getDesiredWidth(text, sPaint)),
                Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);

        float x = model.getX()
                + model.marginLeft
                + model.avatarWidth
                + model.levelMarginLeft
                + model.levelBitmapWidth
                + model.textMarginLeft;

        float top = (int) (model.getY())
                + channel.height / 2
                - staticLayout.getHeight() / 2;

        canvas.save();
        canvas.translate((int) x, top);
        staticLayout.draw(canvas);
        canvas.restore();
    }

    protected void drawTextBackground(BarrageModel model, Canvas canvas, BarrageChannel channel) {
        CharSequence text = model.text;
        StaticLayout staticLayout = new StaticLayout(text,
                sPaint,
                (int) Math.ceil(StaticLayout.getDesiredWidth(text, sPaint)),
                Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, true);

        int textBackgroundHeight = staticLayout.getHeight()
                + model.textBackgroundPaddingTop
                + model.textBackgroundPaddingBottom;

        float top = model.getY()
                + (channel.height - textBackgroundHeight) / 2;

        float x = model.getX()
                + model.marginLeft
                + model.avatarWidth
                - model.textBackgroundMarginLeft;

        Rect rectF = new Rect((int) x,
                (int) top,
                (int) (x + model.levelMarginLeft
                        + model.levelBitmapWidth
                        + model.textMarginLeft
                        + model.textBackgroundMarginLeft
                        + staticLayout.getWidth()
                        + model.textBackgroundPaddingRight),
                (int) (top + textBackgroundHeight));
        model.textBackground.setBounds(rectF);
        model.textBackground.draw(canvas);
    }

    @Override
    public void requestLayout() {
    }

    @Override
    public void setAlpha(int alpha) {
    }

    @Override
    public void hideNormal(boolean hide) {
        this.hide = hide;
    }

    @Override
    public void hideAll(boolean hideAll) {
        this.hideAll = hideAll;
    }

    @Override
    public void execute(Canvas canvas, BarrageModel model, BarrageChannel channel) {
        if ((int) model.getSpeed() == 0) {
            model.setAlive(false);
        }

        onLayout(model, channel);

        if (hideAll) {
            return;
        }

        if (model.getPriority() == BarrageModel.NORMAL && hide) {
            return;
        }

        draw(canvas, model, channel);
    }

}
