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

package com.journeyOS.base.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.journeyOS.base.R;

import java.util.ArrayList;
import java.util.List;


public class LabelTextView extends RelativeLayout {
    private int textColor = 0xff717171;
    private int textSize = 0;
    private int paddingVertical = 0;
    private int paddingHorizontal = 0;
    private int itemMargin = 0;
    private boolean deleteButton = false;

    private int deleteButtonImage = -1;
    private int labelBackground = -1;

    private boolean selectMode = false;
    private int selectBackground = -1;
    private int maxSelectNum = 0;

    private OnLabelClickListener onLabelClickListener;
    private Context context;
    private List<String> labels;

    public LabelTextView(Context context) {
        super(context);
        this.context = context;
    }

    public LabelTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        loadAttrs(context, attrs);
    }

    public LabelTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        loadAttrs(context, attrs);
    }

    private void loadAttrs(Context context, AttributeSet attrs) {
        try {
            //默认值
            textColor = Color.argb(230, 0, 0, 0);
            textSize = dp2px(12);
            paddingVertical = dp2px(8);
            paddingHorizontal = dp2px(12);
            itemMargin = dp2px(4);
            deleteButton = false;

            //加载值
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.Label);
            textColor = typedArray.getColor(R.styleable.Label_textColor, textColor);
            textSize = typedArray.getDimensionPixelOffset(R.styleable.Label_textSize, textSize);
            paddingVertical = typedArray.getDimensionPixelOffset(R.styleable.Label_paddingVertical, paddingVertical);
            paddingHorizontal = typedArray.getDimensionPixelOffset(R.styleable.Label_paddingHorizontal, paddingHorizontal);
            itemMargin = typedArray.getDimensionPixelOffset(R.styleable.Label_itemMargin, itemMargin);
            deleteButton = typedArray.getBoolean(R.styleable.Label_deleteButton, deleteButton);

            deleteButtonImage = typedArray.getResourceId(R.styleable.Label_deleteButtonImage, deleteButtonImage);
            labelBackground = typedArray.getResourceId(R.styleable.Label_labelBackground, labelBackground);

            selectMode = typedArray.getBoolean(R.styleable.Label_selectMode, selectMode);
            selectBackground = typedArray.getResourceId(R.styleable.Label_selectBackground, selectBackground);
            maxSelectNum = typedArray.getInt(R.styleable.Label_maxSelectNum, maxSelectNum);

            if (selectBackground == -1) selectBackground = R.drawable.label_bg_select_normal;
            if (labelBackground == -1) labelBackground = R.drawable.label_button;
            typedArray.recycle();
        } catch (Exception e) {
        }
    }

    private int dp2px(float dpValue) {
        return (int) (0.5f + dpValue * Resources.getSystem().getDisplayMetrics().density);
    }

    private float px2dp(int pxValue) {
        return (pxValue / Resources.getSystem().getDisplayMetrics().density);
    }

    private List<View> items;
    private int newHeight = 0;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        refreshViews();

        setMeasuredDimension(getMeasuredWidth(), newHeight);//设置宽高
    }

    private void refreshViews() {
        int maxWidth = getMeasuredWidth();

        if (labels != null && !labels.isEmpty()) {
            newHeight = 0;
            if (items != null && !items.isEmpty()) {
                for (int i = 0; i < items.size(); i++) {
                    View item = items.get(i);

                    int mWidth = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                    int mHeight = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                    item.measure(mWidth, mHeight);

                    int n_x = 0;
                    int n_y = 0;
                    int o_y = 0;

                    if (i != 0) {
                        n_x = (int) items.get(i - 1).getX() + items.get(i - 1).getMeasuredWidth();
                        n_y = (int) items.get(i - 1).getY() + items.get(i - 1).getMeasuredHeight();
                        o_y = (int) items.get(i - 1).getY();
                    }

                    if (n_x + item.getMeasuredWidth() > maxWidth) {
                        n_x = 0;
                        o_y = n_y;
                    }

                    item.setY(o_y);
                    item.setX(n_x);

                    newHeight = (int) (item.getY() + item.getMeasuredHeight());
                }
            }
        }
    }

    public List<String> getLabels() {
        return labels;
    }

    public LabelTextView setLabels(List<String> l) {
        labels = l;

        removeAllViews();
        items = new ArrayList<>();
        if (labels != null && !labels.isEmpty()) {

            newHeight = 0;
            for (int i = 0; i < labels.size(); i++) {
                View item = LayoutInflater.from(context).inflate(R.layout.layout_label, null, false);

                newHeight = item.getMeasuredHeight();

                addView(item);
                items.add(item);
            }

            initItem();
        }
        return this;
    }

    private List<Integer> selectIndexs = new ArrayList<>();

    private void initItem() {
        if (labels.size() != 0) {
            selectIndexs = new ArrayList<>();
            for (int i = 0; i < items.size(); i++) {
                View item = items.get(i);

                String s = labels.get(i);
                LinearLayout boxLabel = item.findViewById(R.id.box_label);
                TextView txtLabel = item.findViewById(R.id.txt_label);
                ImageView imgDelete = item.findViewById(R.id.img_delete);

                txtLabel.setText(s);
                txtLabel.setTextColor(textColor);
                txtLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);

                boxLabel.setPadding(paddingHorizontal, paddingVertical, paddingHorizontal, paddingVertical);
                ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) boxLabel.getLayoutParams();
                p.setMargins(itemMargin, itemMargin, itemMargin, itemMargin);
                boxLabel.requestLayout();

                if (deleteButton) {
                    imgDelete.setVisibility(VISIBLE);
                } else {
                    imgDelete.setVisibility(GONE);
                }
                if (deleteButtonImage != -1) imgDelete.setImageResource(deleteButtonImage);
                boxLabel.setBackgroundResource(labelBackground);

                int mWidth = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                int mHeight = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
                item.measure(mWidth, mHeight);

                final int index = i;
                boxLabel.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (selectMode) {
                            for (View item : items) {
                                LinearLayout boxLabel = item.findViewById(R.id.box_label);
                                boxLabel.setBackgroundResource(labelBackground);
                            }
                            if (selectIndexs.contains(index)) {
                                int ind = 0;
                                for (int i = 0; i < selectIndexs.size(); i++) {
                                    if (selectIndexs.get(i) == index) {
                                        ind = i;
                                        break;
                                    }
                                }
                                selectIndexs.remove(ind);
                            } else {
                                if (maxSelectNum == 1) selectIndexs.clear();
                                if (maxSelectNum <= 0 || (maxSelectNum > 0 && selectIndexs.size() < maxSelectNum)) {
                                    selectIndexs.add(index);
                                }
                            }
                            for (int index : selectIndexs) {
                                View item = items.get(index);
                                LinearLayout boxLabel = item.findViewById(R.id.box_label);
                                boxLabel.setBackgroundResource(selectBackground);
                            }
                        }
                        if (onLabelClickListener != null)
                            onLabelClickListener.onClick(index, v, labels.get(index));
                    }
                });
            }
        }
    }

    public OnLabelClickListener getOnLabelClickListener() {
        return onLabelClickListener;
    }

    public LabelTextView setOnLabelClickListener(OnLabelClickListener onLabelClickListener) {
        this.onLabelClickListener = onLabelClickListener;
        return this;
    }

    public boolean isDeleteButton() {
        return deleteButton;
    }

    public LabelTextView setDeleteButton(boolean deleteButton) {
        this.deleteButton = deleteButton;
        initItem();
        return this;
    }

    public boolean isSelectMode() {
        return selectMode;
    }

    public LabelTextView setSelectMode(boolean selectMode) {
        this.selectMode = selectMode;
        setLabels(labels);
        return this;
    }

    public int getSelectBackground() {
        return selectBackground;
    }

    public LabelTextView setSelectBackground(int selectBackground) {
        this.selectBackground = selectBackground;
        setLabels(labels);
        return this;
    }

    public int getMaxSelectNum() {
        return maxSelectNum;
    }

    public LabelTextView setMaxSelectNum(int maxSelectNum) {
        this.maxSelectNum = maxSelectNum;
        setLabels(labels);
        return this;
    }

    public List<Integer> getSelectIndexList() {
        return selectIndexs;
    }

    public int[] getSelectIndexArray() {
        int[] arrays = new int[selectIndexs.size()];
        for (int i = 0; i < selectIndexs.size(); i++) {
            arrays[i] = selectIndexs.get(i);
        }
        return arrays;
    }

    public interface OnLabelClickListener {
        void onClick(int index, View v, String s);
    }
}
