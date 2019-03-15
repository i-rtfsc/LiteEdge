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

package com.journeyOS.edge.ui.activity;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.journeyOS.base.Constant;
import com.journeyOS.base.guide.LiteGuide;
import com.journeyOS.base.guide.OnGuideClickListener;
import com.journeyOS.base.persistence.SpUtils;
import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.base.utils.UIUtils;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.api.edgeprovider.ICityProvider;
import com.journeyOS.core.api.plugins.IPlugins;
import com.journeyOS.core.api.thread.ICoreExecutors;
import com.journeyOS.core.base.BaseActivity;
import com.journeyOS.core.database.user.EdgeUser;
import com.journeyOS.edge.EdgeServiceManager;
import com.journeyOS.edge.R;
import com.journeyOS.edge.SlidingDrawer;

import butterknife.BindView;

public class EdgeActivity extends BaseActivity implements SlidingDrawer.OnItemSelectedListener {
    private static final String TAG = EdgeActivity.class.getSimpleName();

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    //@BindView(R.id.fragment_container)
    FrameLayout mContainer;

    Activity mContext;
    Bundle mBundle;

    LiteGuide mLiteGuide = null;

    @Override
    public int attachLayoutRes() {
        return R.layout.activity_edge;
    }

    @Override
    public void initBeforeView() {
        super.initBeforeView();
        mContext = this;

        CoreManager.getDefault().getImpl(ICoreExecutors.class).diskIOThread().execute(new Runnable() {
            @Override
            public void run() {
                CoreManager.getDefault().getImpl(ICityProvider.class).loadCitys();
            }
        });
    }

    @Override
    public void initViews() {
        UIUtils.setStatusBarColor(this, this.getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(mToolbar);
        mContainer = findViewById(R.id.container);

        EdgeServiceManager.getDefault().bindEgdeService();
    }

    @Override
    protected void initDataObserver(Bundle savedInstanceState) {
        super.initDataObserver(savedInstanceState);
        mBundle = savedInstanceState;
        handleEdgeUserStatusObserver(null);
    }

    @Override
    public void onItemSelected(int position) {
        handleItemSelected(position);
    }

    void loadFragment(Fragment fragment) {
        mContainer.setVisibility(View.VISIBLE);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }

    void handleEdgeUserStatusObserver(EdgeUser authUser) {
        SlidingDrawer.getInstance(this).initDrawer(mBundle, mToolbar);
        SlidingDrawer.getInstance(this).setListener(this);
        mToolbar.setTitle(R.string.app_name);
    }

    void handleItemSelected(int position) {
        initGuideView();
        LogUtils.d(TAG, "handle item selected, position = [" + position + "]");
        switch (position) {
            case Constant.MENU_USER:
                mToolbar.setTitle(R.string.menu_account);
                loadFragment(CoreManager.getDefault().getImpl(IPlugins.class).provideLoginFragment(mContext));
                break;
            case Constant.MENU_PERMISSION:
                mToolbar.setTitle(R.string.menu_permission);
                loadFragment(CoreManager.getDefault().getImpl(IPlugins.class).providePermissionFragment(mContext));

                break;
            case Constant.MENU_SETTINGS:
                mToolbar.setTitle(R.string.menu_settings);
                loadFragment(CoreManager.getDefault().getImpl(IPlugins.class).provideSettingsFragment(mContext));
                break;
            case Constant.MENU_ABOUT:
                mToolbar.setTitle(R.string.menu_about);
                loadFragment(CoreManager.getDefault().getImpl(IPlugins.class).provideAboutFragment(mContext));
                break;
            case Constant.MENU_LEARN:
//                mToolbar.setTitle(R.string.menu_learn);
                CoreManager.getDefault().getImpl(IPlugins.class).navigationLearnActivity(mContext);
                break;
        }
    }


    void initGuideView() {
        boolean inited = SpUtils.getInstant().getBoolean(Constant.GUIDE_INITED, false);
        if (inited) {
            return;
        }

        if (mLiteGuide == null) {
            mLiteGuide = new LiteGuide(this);
            mLiteGuide.addNextTarget(mToolbar,
                    mContext.getResources().getString(R.string.guide_menu_open),
                    50, 100);

            mLiteGuide.addNextTarget(SlidingDrawer.getInstance(this).getView(0),
                    mContext.getResources().getString(R.string.guide_user),
                    350, -20);

            mLiteGuide.addNextTarget(SlidingDrawer.getInstance(this).getView(1),
                    mContext.getResources().getString(R.string.guide_permission),
                    350, -20, 350, ViewGroup.LayoutParams.WRAP_CONTENT);

            mLiteGuide.addNextTarget(SlidingDrawer.getInstance(this).getView(2),
                    mContext.getResources().getString(R.string.guide_settings),
                    350, -20);

            mLiteGuide.addNextTarget(
                    SlidingDrawer.getInstance(this).getView(4),
                    mContext.getResources().getString(R.string.guide_learn),
                    280, 20, 500, ViewGroup.LayoutParams.WRAP_CONTENT);

            mLiteGuide.prepare();

            mLiteGuide.setMaskMoveDuration(500);
            mLiteGuide.setExpandDuration(500);
            mLiteGuide.setMaskRefreshTime(30);
            mLiteGuide.setMaskColor(Color.argb(99, 200, 100, 99));

            mLiteGuide.setOnGuiderListener(new GuideObserver());
            mLiteGuide.startGuide();
        }
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
            SpUtils.getInstant().put(Constant.GUIDE_INITED, true);
        }

        @Override
        public void onGuideStart() {
            LogUtils.d(TAG, "guide start");
        }

        @Override
        public void onGuideNext(int nextStep) {
            LogUtils.d(TAG, "user click guide next " + nextStep);
            if (nextStep == 1) {
                SlidingDrawer.getInstance(mContext).openMenu();
            }
        }

        @Override
        public void onGuideFinished() {
            LogUtils.d(TAG, "guide finished");
            SpUtils.getInstant().put(Constant.GUIDE_INITED, true);
            handleItemSelected(4);
            SlidingDrawer.getInstance(mContext).closeMenu();
        }

        @Override
        public void onTarget(int index) {
            handleItemSelected(index - 1);
        }
    }
}
