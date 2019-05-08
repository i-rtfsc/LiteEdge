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

package com.journeyOS.edge.barrage;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;

import com.journeyOS.barrage.model.BarrageModel;
import com.journeyOS.barrage.model.utils.DimensionUtil;
import com.journeyOS.barrage.view.IBarrageParent;
import com.journeyOS.barrage.view.OnBarrageTouchListener;
import com.journeyOS.base.Constant;
import com.journeyOS.base.persistence.SpUtils;
import com.journeyOS.base.utils.AppUtils;
import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.edge.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public final class BarrageHelper {
    private static final String TAG = BarrageHelper.class.getSimpleName();
    private ArrayList<WeakReference<IBarrageParent>> mBarrageParentViews;
    private Context mContext;

    public BarrageHelper(Context context) {
        this.mContext = context.getApplicationContext();
        this.mBarrageParentViews = new ArrayList<>();
    }

    public void release() {
        if (mBarrageParentViews != null) {
            for (WeakReference<IBarrageParent> parentsRef : mBarrageParentViews) {
                if (parentsRef != null) {
                    IBarrageParent danMuParent = parentsRef.get();
                    if (danMuParent != null)
                        danMuParent.release();
                }
            }
            mBarrageParentViews.clear();
            mBarrageParentViews = null;
        }

        mContext = null;
    }

    public void add(final IBarrageParent parent) {
        if (parent != null) {
            parent.clear();
        }

        if (mBarrageParentViews != null) {
            mBarrageParentViews.add(new WeakReference<>(parent));
        }
    }

    public void addBarrage(BarrageEntity entity, boolean broadcast) {
        if (mBarrageParentViews != null) {
            WeakReference<IBarrageParent> parentsRef = mBarrageParentViews.get(0);
            if (!broadcast) {
                parentsRef = mBarrageParentViews.get(1);
            }

            BarrageModel model = createBarrageView(entity);
            if (parentsRef != null && model != null && parentsRef.get() != null) {
                parentsRef.get().add(model);
            }
        }
    }

    private BarrageModel createBarrageView(final BarrageEntity entity) {
        final BarrageModel barrageModel = new BarrageModel();
        barrageModel.setDisplayType(BarrageModel.RIGHT_TO_LEFT);
        barrageModel.setPriority(BarrageModel.NORMAL);
        barrageModel.marginLeft = DimensionUtil.dpToPx(mContext, 30);

        if (entity.type == BarrageEntity.BARRAGE_TYPE_USERCHAT) {
            // 图像
            if (entity.avatar != null) {
                int avatarSize = DimensionUtil.dpToPx(mContext, SpUtils.getInstant().getInt(Constant.BARRAGE_AVATAR_SIZE, Constant.BARRAGE_AVATAR_SIZE_DEFAULT));
                barrageModel.avatarWidth = avatarSize;
                barrageModel.avatarHeight = avatarSize;
                barrageModel.avatar = entity.avatar;
            }

            if (entity.name != null && entity.text != null) {
                // 显示的文本内容
                String name = entity.name;
                String content = entity.text;
                SpannableString spannableString = new SpannableString(name + "：" + content);
                int nameColor = SpUtils.getInstant().getInt(Constant.BARRAGE_TITLE_COLOR, Constant.BARRAGE_TITLE_COLOR_DEFAULT);
                if (nameColor == 0) {
                    nameColor = ContextCompat.getColor(mContext, R.color.hotpink);
                }
                spannableString.setSpan(new ForegroundColorSpan(nameColor), 0, name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                barrageModel.textSize = DimensionUtil.spToPx(mContext, SpUtils.getInstant().getInt(Constant.BARRAGE_TEXT_SIZE, Constant.BARRAGE_TEXT_SIZE_DEFAULT));
                int textColor = SpUtils.getInstant().getInt(Constant.BARRAGE_SUMMARY_COLOR, Constant.BARRAGE_SUMMARY_COLOR_DEFAULT);
                if (textColor == 0) {
                    textColor = ContextCompat.getColor(mContext, R.color.lavender);
                }
                barrageModel.textColor = textColor;
                barrageModel.textMarginLeft = DimensionUtil.dpToPx(mContext, 5);
                barrageModel.text = spannableString;

                // 弹幕文本背景
                int backgroundColor = SpUtils.getInstant().getInt(Constant.BARRAGE_BACKGROUND_COLOR, Constant.BARRAGE_BACKGROUND_COLOR_DEFAULT);
                if (backgroundColor == 0) {
                    backgroundColor = ContextCompat.getColor(mContext, R.color.divider_dark);
                }

                barrageModel.textBackground = ContextCompat.getDrawable(mContext, R.drawable.corners);
                barrageModel.textBackground.setColorFilter(backgroundColor, PorterDuff.Mode.SRC);
                barrageModel.textBackgroundMarginLeft = DimensionUtil.dpToPx(mContext, 10);
                barrageModel.textBackgroundPaddingTop = DimensionUtil.dpToPx(mContext, 2);
                barrageModel.textBackgroundPaddingBottom = DimensionUtil.dpToPx(mContext, 2);
                barrageModel.textBackgroundPaddingRight = DimensionUtil.dpToPx(mContext, 12);

                barrageModel.enableTouch(true);
                barrageModel.setOnTouchCallBackListener(new OnBarrageTouchListener() {

                    @Override
                    public void callBack(BarrageModel model) {
                        LogUtils.d(TAG, "on barrage view touch, packageName = " + entity.packageName);
                        int item = SpUtils.getInstant().getInt(Constant.BARRAGE_CLICK, Constant.BARRAGE_CLICK_DEFAULT);
                        LogUtils.d(TAG, "on barrage view touch, item = " + item);
                        if (item == 2) {
                            AppUtils.startApp(mContext, entity.packageName);
                        }
                    }
                });
            }
        } else {
            // 显示的文本内容
            barrageModel.textSize = DimensionUtil.spToPx(mContext, 14);
            barrageModel.textColor = ContextCompat.getColor(mContext, R.color.lavender);
            barrageModel.textMarginLeft = DimensionUtil.dpToPx(mContext, 5);

            if (entity.richText != null) {
                barrageModel.text = RichTextParse.parse(mContext, entity.richText, DimensionUtil.spToPx(mContext, 18), false);
            } else {
                barrageModel.text = entity.text;
            }

            // 弹幕文本背景
            barrageModel.textBackground = ContextCompat.getDrawable(mContext, R.drawable.corners);
            barrageModel.textBackgroundMarginLeft = DimensionUtil.dpToPx(mContext, 15);
            barrageModel.textBackgroundPaddingTop = DimensionUtil.dpToPx(mContext, 3);
            barrageModel.textBackgroundPaddingBottom = DimensionUtil.dpToPx(mContext, 3);
            barrageModel.textBackgroundPaddingRight = DimensionUtil.dpToPx(mContext, 15);

            barrageModel.enableTouch(false);
        }

        return barrageModel;
    }
}