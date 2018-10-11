/*
 * Copyright (c) 2018 anqi.huang@outlook.com
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

package com.journeyOS.base.widget;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.RadialGradient;
import android.graphics.Shader;

public abstract class Ball {

    private static final int mInnerColor = 0xFF00FF;
    private static final int mOutterColor = 0xaaffffff;
    //竖直方向半径
    protected int mRadius = 100;
    protected int mOriginRadius;
    //水平方向左半径
    protected int mLeftRadius = 100;
    //水平方向右半径
    protected int mRightRadius = 100;
    //绘制时画板需要旋转的角度
    protected float mAngle = 0;
    //椭圆的中心点
    protected Point mCenter;
    protected Point mOriginCenter;
    //颜色渐变的颜色值组
    protected int colors[] = {mInnerColor, mOutterColor};
    //颜色渐变的颜色位置组
    protected float stops[] = {0.5f, 1.0f};
    //控件的中心点
    protected Point mParentCenter;
    //控件的半径
    protected int mParentRadius;
    //静止时椭圆大小与控件大小的比值
    protected float mSizeRate;
    protected Paint mPaint;
    private RadialGradient radialGradient;

    public Ball(int centerX, int centerY, int parentRadius, float sizeRate) {

        this.mParentCenter = new Point(centerX, centerY);
        this.mParentRadius = parentRadius;
        this.mSizeRate = sizeRate;
        init();
    }

    private void init() {
        mCenter = new Point(mParentCenter.x, mParentCenter.y);
        mOriginCenter = new Point(mCenter.x, mCenter.y);
        mRadius = (int) (mParentRadius * mSizeRate);
        mOriginRadius = mRadius;
        mLeftRadius = mRadius;
        mRightRadius = mRadius;
        mAngle = 0;
        radialGradient = new RadialGradient(0, 0, mRadius, colors, stops, Shader.TileMode.CLAMP);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setShader(radialGradient);
    }

    public void draw(Canvas canvas) {

        int x = mCenter.x - mParentCenter.x;
        int y = mCenter.y - mParentCenter.y;

        canvas.save();
        canvas.translate(mParentCenter.x, mParentCenter.y);
        canvas.rotate(mAngle);
        canvas.translate(x, y);

        //绘制左半圆
        canvas.save();
        canvas.scale(mLeftRadius * 1.0f / mOriginRadius, 1.0f);
        canvas.drawArc(-mLeftRadius, -mRadius, mLeftRadius, mRadius, 90, 180, false, mPaint);
        canvas.restore();

        //绘制右半圆
        canvas.save();
        canvas.scale(mRightRadius * 1.0f / mOriginRadius, 1.0f);
        canvas.drawArc(-mRightRadius, -mRadius, mRightRadius, mRadius, -90, 180, false, mPaint);
        canvas.restore();

        canvas.restore();
    }

    public void setAngle(float mAngle) {
        this.mAngle = mAngle;
    }

    public void setColorsAndPosition(int[] colors, float[] stops) {
        this.colors = colors;
        this.stops = stops;
        radialGradient = new RadialGradient(0, 0, mRadius, colors, stops, Shader.TileMode.CLAMP);
        mPaint.setShader(radialGradient);
    }

    public void setOffset(int offset) {
        float fraction = offset * 1.0f / mParentRadius;
        fraction = Math.min(fraction, 1.0f);
        calculateRadius(fraction);
        calculateLeftRadius(fraction);
        calculateRightRadius(fraction);
        calculateCenter(fraction);
    }

    public void setAllRadius(int radius) {
        mRadius = radius;
        mOriginRadius = radius;
        mLeftRadius = radius;
        mRightRadius = radius;
    }

    protected abstract void calculateCenter(float fraction);

    protected abstract void calculateRightRadius(float fraction);

    protected abstract void calculateLeftRadius(float fraction);

    protected abstract void calculateRadius(float fraction);
}
