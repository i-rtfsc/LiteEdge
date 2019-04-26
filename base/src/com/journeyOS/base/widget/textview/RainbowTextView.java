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

package com.journeyOS.base.widget.textview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Shader;
import android.util.AttributeSet;

import com.journeyOS.base.R;
import com.journeyOS.base.widget.textview.base.AnimationListener;
import com.journeyOS.base.widget.textview.base.DisplayUtils;
import com.journeyOS.base.widget.textview.base.HTextView;

/**
 * RainbowTextView
 */
public class RainbowTextView extends HTextView {

    private Matrix mMatrix;
    private float mTranslate;
    private float colorSpeed;
    private float colorSpace;
    private int[] colors = {0xFFFF2B22, 0xFFFF7F22, 0xFFEDFF22, 0xFF22FF22, 0xFF22F4FF, 0xFF2239FF, 0xFF5400F7};
    private LinearGradient mLinearGradient;

    public RainbowTextView(Context context) {
        this(context, null);
    }

    public RainbowTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RainbowTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs, defStyleAttr);
    }

    @Override
    public void setAnimationListener(AnimationListener listener) {
        throw new UnsupportedOperationException("Invalid operation for rainbow");
    }

    private void init(AttributeSet attrs, int defStyleAttr) {

        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.RainbowTextView);
        colorSpace = typedArray.getDimension(R.styleable.RainbowTextView_colorSpace, DisplayUtils.dp2px(150));
        colorSpeed = typedArray.getDimension(R.styleable.RainbowTextView_colorSpeed, DisplayUtils.dp2px(4));
        typedArray.recycle();

        mMatrix = new Matrix();
        initPaint();
    }

    public float getColorSpace() {
        return colorSpace;
    }

    public void setColorSpace(float colorSpace) {
        this.colorSpace = colorSpace;
    }

    public float getColorSpeed() {
        return colorSpeed;
    }

    public void setColorSpeed(float colorSpeed) {
        this.colorSpeed = colorSpeed;
    }

    public void setColors(int... colors) {
        this.colors = colors;
        initPaint();
    }

    private void initPaint() {
        mLinearGradient = new LinearGradient(0, 0, colorSpace, 0, colors, null, Shader.TileMode.MIRROR);
        getPaint().setShader(mLinearGradient);
    }

    @Override
    public void setProgress(float progress) {
    }

    @Override
    public void animateText(CharSequence text) {
        setText(text);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mMatrix == null) {
            mMatrix = new Matrix();
        }
        mTranslate += colorSpeed;
        mMatrix.setTranslate(mTranslate, 0);
        mLinearGradient.setLocalMatrix(mMatrix);
        super.onDraw(canvas);
        postInvalidateDelayed(100);
    }
}
