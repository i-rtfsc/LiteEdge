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
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.journeyOS.base.R;
import com.journeyOS.base.utils.AnimationUtils;
import com.journeyOS.base.utils.UIUtils;

public class BarrageView {
    private static final String TAG = BarrageView.class.getSimpleName();
    private View mLayout;
    private ImageView mImageViewIcon;
    private TextView mTextViewTitle;
    private TextView mTextViewContent;

    private Context mContext;

    private int mScreenWidth;
    private int mScreenHeight;

    private AlphaAnimation mAnimation;
    private GradientDrawable mBackground;

    public boolean availiable = false;
    boolean isCleaning = false;

    private long mStartTime = 0;

    private float mSpeed;

    public BarrageView(Context context) {
        mContext = context;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        mScreenWidth = UIUtils.getScreenWidth(mContext);
        mScreenHeight = UIUtils.getScreenHeight(mContext);

        mLayout = mInflater.inflate(R.layout.barrage_layout, null);
        mImageViewIcon = (ImageView) mLayout.findViewById(R.id.barrage_icon);
        mTextViewTitle = (TextView) mLayout.findViewById(R.id.barrage_title);
        mTextViewContent = (TextView) mLayout.findViewById(R.id.barrage_content);
        mBackground = (GradientDrawable) mLayout.getBackground();

        mAnimation = new AlphaAnimation(0, 1f);
        mAnimation.setDuration(800);
        mAnimation.setInterpolator(AnimationUtils.getEaseInterpolator());
        mAnimation.setFillAfter(true);
        mAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                availiable = false;//something wrong
                isCleaning = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    public void clear() {
        if (!isCleaning) {
            isCleaning = true;
            mLayout.startAnimation(mAnimation);
        }
    }

    public void load(BarrageModel model) {
        if (model == null) {
            return;
        }
        if (model.title == null || model.content == null) {
            return;
        }

        if (!isCleaning) {
            if (model.avatar != null) {
                mImageViewIcon.setVisibility(View.VISIBLE);
                mImageViewIcon.setImageBitmap(model.avatar);
            } else {
                mImageViewIcon.setVisibility(View.GONE);
            }
            mTextViewTitle.setText(model.title + "：");
            mTextViewContent.setText(model.content);

            BarrageController controller = model.controller;

            mLayout.setX(mScreenWidth);

            switch (controller.getPostion()) {
                case BarrageModel.POSTION_FULL:
                    mLayout.setY((int) (Math.random() * 19) / 20f * mScreenHeight);
                    break;
                case BarrageModel.POSTION_TOP:
                    mLayout.setY((int) (Math.random() * 6) / 20f * mScreenHeight);
                    break;
                case BarrageModel.POSTION_MIDDLE:
                    mLayout.setY((int) (Math.random() * 5 + 7) / 20f * mScreenHeight);
                    break;
                case BarrageModel.POSTION_BOTTOM:
                    mLayout.setY((int) (Math.random() * 6 + 13) / 20f * mScreenHeight);
                    break;
            }

            mSpeed = controller.getSpeed();

            mBackground.setColor(controller.getBackgroundColor());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(controller.getAvatarSize(), controller.getAvatarSize());
            layoutParams.gravity = Gravity.CENTER_VERTICAL;
            mImageViewIcon.setLayoutParams(layoutParams);
            mTextViewTitle.setTextColor(controller.getTextTitleColor());
            mTextViewContent.setTextColor(controller.getTextContentColor());
            mTextViewTitle.setTextSize(controller.getTextSize());
            mTextViewContent.setTextSize(controller.getTextSize());

            mStartTime = System.currentTimeMillis();
            availiable = true;
        }
    }

    public boolean move() {
        if (availiable) {
            long time = (System.currentTimeMillis() - mStartTime) / 1;
            float posX = mScreenWidth - time * mSpeed / 200f;

            mLayout.setX(posX);
            if (mLayout.getWidth() > 0 && posX < -mLayout.getWidth()) {
                availiable = false;
            }
        }
        return availiable;
    }

    public View getView() {
        return mLayout;
    }
}
