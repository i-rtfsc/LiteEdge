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
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;

import com.journeyOS.edge.R;

import java.util.ArrayList;


public class RichTextParse {

    public static SpannableStringBuilder parse(final Context context, ArrayList<RichMessage> richText,
                                               int textSize, boolean isChatList) {
        final SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
        if (isChatList) {
            String name = "直播消息：";
            spannableStringBuilder.append(name);

            int nameColor = ContextCompat.getColor(context, R.color.yellow);
            spannableStringBuilder.setSpan(new ForegroundColorSpan(nameColor),
                    0,
                    name.length(),
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        for (RichMessage message : richText) {
            final int length = spannableStringBuilder.length();
            if ("text".equals(message.type)) {
                String content = message.content;
                spannableStringBuilder.append(content);

                String textColor = message.color;
                if (TextUtils.isEmpty(textColor)) {
                    textColor = "FFFFFF";
                }

                spannableStringBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#" + textColor)),
                        length,
                        length + content.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else if ("icon_gift".equals(message.type)) {
                // 这里仅用于测试
                spannableStringBuilder.append("中奖礼物");
                final int imgSize = (int) (textSize * 1.5);
                Bitmap bitmap = ((BitmapDrawable) ContextCompat.getDrawable(context, R.drawable.svg_menu_user)).getBitmap();
                LHImageSpan imageSpan = new LHImageSpan(context, bitmap, imgSize);
                spannableStringBuilder.setSpan(imageSpan,
                        length,
                        length + 4,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


            } else {
                String content = message.content;
                spannableStringBuilder.append(content);

                spannableStringBuilder.setSpan(
                        new ForegroundColorSpan(ContextCompat.getColor(context,
                                R.color.lavender)),
                        length,
                        length + content.length(),
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        return spannableStringBuilder;
    }

}