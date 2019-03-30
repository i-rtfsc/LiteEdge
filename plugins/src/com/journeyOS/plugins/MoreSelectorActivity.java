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

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;

import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.base.utils.UIUtils;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.api.edgeprovider.IGestureProvider;
import com.journeyOS.core.api.thread.ICoreExecutors;
import com.journeyOS.core.base.BaseActivity;
import com.journeyOS.core.base.MainPageAdapter;
import com.journeyOS.core.database.gesture.Gesture;
import com.journeyOS.core.type.FingerDirection;
import com.journeyOS.plugins.key.KeySelectorFragment;
import com.journeyOS.plugins.music.MusicSelectorFragment;
import com.journeyOS.plugins.pay.PaySelectorFragment;

import butterknife.BindView;

public class MoreSelectorActivity extends BaseActivity {
    private static final String TAG = MoreSelectorActivity.class.getSimpleName();


    private static final String EXTRA_ROTATION = "extra_rotation";
    private static final String EXTRA_DIRECTION = "extra_direction";

    @BindView(R2.id.toolbar_layout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @BindView(R2.id.toolbar)
    Toolbar mToolbar;
    @BindView(R2.id.frame_container)
    FrameLayout mFragmentContainer;
    @BindView(R2.id.tab_layout)
    TabLayout mTabLayout;
    @BindView(R2.id.view_pager)
    ViewPager mViewPager;

    MainPageAdapter mAdapter;

    Activity mContext;
    static int sRotation;
    static FingerDirection sDirection;

    public static void navigationActivity(Context from, int rotation, FingerDirection direction) {
        try {
            Intent intent = new Intent(from, MoreSelectorActivity.class);
            intent.putExtra(EXTRA_ROTATION, rotation);
            intent.putExtra(EXTRA_DIRECTION, direction);
            from.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            LogUtils.d(TAG, e);
        }

    }

    public static void navigationFromApplication(Context from, int rotation, FingerDirection direction) {
        Intent intent = new Intent(from, MoreSelectorActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(EXTRA_ROTATION, rotation);
        intent.putExtra(EXTRA_DIRECTION, direction);
        from.startActivity(intent);
    }


    private void finishActivity() {
        this.finish();
    }

    @Override
    public void initBeforeView() {
        super.initBeforeView();
        mContext = this;
        sRotation = getIntent().getIntExtra(EXTRA_ROTATION, -1);
        sDirection = (FingerDirection) getIntent().getSerializableExtra(EXTRA_DIRECTION);
    }

    @Override
    public int attachLayoutRes() {
        return R.layout.activity_more_selector;
    }

    @Override
    public void initViews() {
        mCollapsingToolbarLayout.setTitleEnabled(false);
        UIUtils.setStatusBarColor(this, this.getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(mToolbar);
        mAdapter = new MainPageAdapter(this, getSupportFragmentManager());
        setupProfileViewPager();
    }

    @Override
    protected void initDataObserver(Bundle savedInstanceState) {
        super.initDataObserver(savedInstanceState);
        LogUtils.d(TAG, "data observer has been called!");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_more_selector, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        super.onOptionsItemSelected(menuItem);
//        switch (menuItem.getItemId()) {
//            case R2.id.gesture_none:
//                break;
//            default:
//                break;
//
//        }
        CoreManager.getDefault().getImpl(ICoreExecutors.class).diskIOThread().execute(new Runnable() {
            @Override
            public void run() {
                if (sDirection != null || sRotation != -1) {
                    String item = CoreManager.getDefault().getImpl(IGestureProvider.class).encodeItem(sDirection, sRotation);
                    Gesture gesture = CoreManager.getDefault().getImpl(IGestureProvider.class).getConfig(item);
                    CoreManager.getDefault().getImpl(IGestureProvider.class).deleteConfig(gesture);
                    finishActivity();
                }
            }
        });
        return true;
    }

    void setupProfileViewPager() {
        mTabLayout.setVisibility(View.VISIBLE);
        mViewPager.setVisibility(View.VISIBLE);
        mFragmentContainer.setVisibility(View.GONE);

//        Pair<Fragment, Integer> appPair = new Pair<>(AppSelectorFragment.newInstance(this, sRotation, sDirection), R.string.selector_app);
//        mAdapter.addFrag(appPair);

        Pair<Fragment, Integer> payPair = new Pair<>(PaySelectorFragment.newInstance(this, sRotation, sDirection), R.string.selector_pay);
        mAdapter.addFrag(payPair);

        Pair<Fragment, Integer> keyPair = new Pair<>(KeySelectorFragment.newInstance(this, sRotation, sDirection), R.string.selector_action);
        mAdapter.addFrag(keyPair);

        Pair<Fragment, Integer> musicPair = new Pair<>(MusicSelectorFragment.newInstance(this, sRotation, sDirection), R.string.selector_music);
        mAdapter.addFrag(musicPair);

        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        for (int index = 0; index < mAdapter.getCount(); index++) {
            mTabLayout.getTabAt(index).setCustomView(mAdapter.getTabView(index, mTabLayout));
        }

        mViewPager.setOffscreenPageLimit(mAdapter.getCount());
        mViewPager.setCurrentItem(0);
    }
}
