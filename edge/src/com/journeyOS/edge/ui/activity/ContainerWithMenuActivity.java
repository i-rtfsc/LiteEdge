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

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.journeyOS.base.utils.AppUtils;
import com.journeyOS.base.utils.UIUtils;
import com.journeyOS.core.base.BaseActivity;
import com.journeyOS.core.base.BaseFragment;
import com.journeyOS.edge.R;

import java.util.List;

import butterknife.BindView;

public class ContainerWithMenuActivity extends BaseActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    private static Fragment mFragment = null;
    private static String mTitle;

    public static void show(Context context, Fragment fragment, String title) {
        Intent intent = new Intent(context, ContainerWithMenuActivity.class);
        mFragment = fragment;
        mTitle = title;
        AppUtils.startIntent(context, intent);
    }

    @Override
    public int attachLayoutRes() {
        return R.layout.activity_container;
    }

    @Override
    public void initViews() {
        UIUtils.setStatusBarColor(this, this.getResources().getColor(R.color.colorPrimary));
        if (mFragment != null) {
            loadFragment(mFragment, mTitle);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        for (Fragment f : fragmentList) {
            if (f instanceof BaseFragment) {
                ((BaseFragment) f).onFragmentResume();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.container, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        super.onOptionsItemSelected(menuItem);
        switch (menuItem.getItemId()) {
            case R.id.add:
                List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
                for (Fragment f : fragmentList) {
                    if (f instanceof BaseFragment) {
                        ((BaseFragment) f).onMenuAdd();
                    }
                }
                break;
            default:
                break;

        }
        return true;
    }

    void loadFragment(Fragment fragment, String title) {
        mToolbar.setTitle(title);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_container, fragment)
                .commitAllowingStateLoss();
    }
}
