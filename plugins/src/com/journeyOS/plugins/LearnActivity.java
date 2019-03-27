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

package com.journeyOS.plugins;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.journeyOS.base.Constant;
import com.journeyOS.base.guide.LiteGuide;
import com.journeyOS.base.guide.OnGuideClickListener;
import com.journeyOS.base.utils.AppUtils;
import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.api.edge.IEdge;
import com.journeyOS.core.base.BaseActivity;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

public class LearnActivity extends BaseActivity {
    private static final String TAG = LearnActivity.class.getSimpleName();

    @BindView(R2.id.layout_groups)
    View mLayoutEdge;

    @BindView(R2.id.layout_statusbar)
    View mLayoutStatus;

    @BindView(R2.id.layout1)
    View mLayout1;
    @BindView(R2.id.layout2)
    View mLayout2;
    @BindView(R2.id.layout3)
    View mLayout3;
    @BindView(R2.id.layout4)
    View mLayout4;
    @BindView(R2.id.layout5)
    View mLayout5;
    @BindView(R2.id.layout6)
    View mLayout6;

    @BindView(R2.id.icon1)
    CircleImageView mIcon1;
    @BindView(R2.id.icon2)
    CircleImageView mIcon2;
    @BindView(R2.id.icon3)
    CircleImageView mIcon3;
    @BindView(R2.id.icon4)
    CircleImageView mIcon4;
    @BindView(R2.id.icon5)
    CircleImageView mIcon5;
    @BindView(R2.id.icon6)
    CircleImageView mIcon6;

    @BindView(R2.id.text1)
    TextView mText1;
    @BindView(R2.id.text2)
    TextView mText2;
    @BindView(R2.id.text3)
    TextView mText3;
    @BindView(R2.id.text4)
    TextView mText4;
    @BindView(R2.id.text5)
    TextView mText5;
    @BindView(R2.id.text6)
    TextView mText6;

    LiteGuide mLiteGuide = null;

    public static void navigationActivity(Context from) {
        try {
            Intent intent = new Intent(from, LearnActivity.class);
            from.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            LogUtils.d(TAG, e);
        }

    }

    public static void navigationFromApplication(Context from) {
        Intent intent = new Intent(from, LearnActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        from.startActivity(intent);
        CoreManager.getDefault().getImpl(IEdge.class).hidingEdge(true);
    }

    private void finishActivity() {
        this.finish();
    }

    @Override
    public int attachLayoutRes() {
        return R.layout.fragment_learn_edge;
    }

    @Override
    public void initBeforeView() {
        super.initBeforeView();
    }

    @Override
    public void initViews() {
        mLayoutStatus.setPivotX(480f);
        mLayoutStatus.setPivotY(48f);
        mLayoutStatus.setRotation(-90f);

        mLayout1.setRotation(-22f);
        mLayout2.setRotation(-13.2f);
        mLayout3.setRotation(-4.4f);
        mLayout4.setRotation(4.4f);
        mLayout5.setRotation(13.2f);
        mLayout6.setRotation(22f);

        Drawable drawable = AppUtils.getAppIcon(this, this.getPackageName());
        String name = AppUtils.getAppName(this, this.getPackageName(), Constant.LENGTH);
        mIcon4.setImageDrawable(drawable);
        mText4.setText(name);

        initGuideView();
    }

    void initGuideView() {
        mLiteGuide = new LiteGuide(this);

        mLiteGuide.addNextTarget(new RectF(300, 305, 300, 305),
                this.getResources().getString(R.string.guide_edge_welcome),
                30, 20);

        mLiteGuide.addNextTarget(mIcon4,
                this.getResources().getString(R.string.guide_edge_add),
                80, 20);

        mLiteGuide.addNextTarget(mIcon3,
                this.getResources().getString(R.string.guide_edge_not_add),
                80, 20);

//        mLiteGuide.addNextTarget(mLayoutStatus,
//                this.getResources().getString(R.string.guide_edge_not_add),
//                200, -100);

        mLiteGuide.addNextTarget(
                new RectF(300, 305, 300, 305),
                this.getResources().getString(R.string.guide_edge_done),
                80, 20, 500, ViewGroup.LayoutParams.WRAP_CONTENT,
                "", this.getResources().getString(R.string.guide_done));

        mLiteGuide.prepare();

        mLiteGuide.setMaskMoveDuration(500);
        mLiteGuide.setExpandDuration(500);
        mLiteGuide.setMaskRefreshTime(30);
        mLiteGuide.setMaskColor(Color.argb(99, 200, 100, 99));

        mLiteGuide.setOnGuiderListener(new GuideObserver());
        mLiteGuide.startGuide();

    }

    class GuideObserver implements OnGuideClickListener {
        @Override
        public void onMask() {
            LogUtils.d(TAG, "user click mask view.");
        }

        @Override
        public void onNext(int nextStep) {
            LogUtils.d(TAG, "user click next step" + nextStep);
        }

        @Override
        public void onJump() {
            LogUtils.d(TAG, "user jump guide");
        }

        @Override
        public void onGuideStart() {
            LogUtils.d(TAG, "guide start");
        }

        @Override
        public void onGuideNext(int nextStep) {
            LogUtils.d(TAG, "user click guide next " + nextStep);
        }

        @Override
        public void onGuideFinished() {
            LogUtils.d(TAG, "guide finished");
            finishActivity();
        }

        @Override
        public void onTarget(int index) {
        }
    }

}
