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

package com.journeyOS.edge;

import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.journeyOS.base.Constant;
import com.journeyOS.base.menu.DrawerAdapter;
import com.journeyOS.base.menu.DrawerItem;
import com.journeyOS.base.menu.SimpleItem;
import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.ImageEngine;
import com.journeyOS.core.database.user.EdgeUser;
import com.journeyOS.edge.ui.activity.EdgeActivity;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.Arrays;

import cn.bmob.v3.BmobUser;

public class SlidingDrawer implements DrawerAdapter.OnItemSelectedListener {

    private Activity mContext;
    private String[] screenTitles;
    private Drawable[] screenIcons;
    private DrawerAdapter adapter;
    private SlidingRootNav slidingRootNav;

    private final H mHandler = H.getDefault().getHandler();
    private volatile static SlidingDrawer mSlidingDrawer;

    public static SlidingDrawer getInstance(Activity context) {
        if (mSlidingDrawer == null) {
            synchronized (SlidingDrawer.class) {
                if (mSlidingDrawer == null) {
                    mSlidingDrawer = new SlidingDrawer(context);
                }
            }
        }
        return mSlidingDrawer;
    }

    private SlidingDrawer(Activity context) {
        mContext = context;
    }

    public void initDrawer(Bundle savedInstanceState, Toolbar toolbar) {
        slidingRootNav = new SlidingRootNavBuilder(mContext)
                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withSavedState(savedInstanceState)
                .withMenuLayout(R.layout.menu_left_drawer)
                .inject();

        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();

        adapter = new DrawerAdapter(Arrays.asList(
                createItemFor(Constant.MENU_USER),
                createItemFor(Constant.MENU_PERMISSION),
                createItemFor(Constant.MENU_SETTINGS).setChecked(true),
                createItemFor(Constant.MENU_BARRAGE),
                createItemFor(Constant.MENU_ABOUT),
                createItemFor(Constant.MENU_LEARN)));
        adapter.setListener(this);

        RecyclerView list = mContext.findViewById(R.id.list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(mContext));
        list.setAdapter(adapter);

        ImageView icon = ((ImageView) mContext.findViewById(R.id.user_avatar));

        String user = Constant.USER;
        String contact = Constant.EMAIL;
        if (BmobUser.isLogin()) {
            EdgeUser edgeUser = BmobUser.getCurrentUser(EdgeUser.class);
            user = edgeUser.getNickname();
            contact = edgeUser.getEmail();
            if (contact == null || contact == "") {
                contact = edgeUser.getMobilePhoneNumber();
            }
            String avatar = edgeUser.getIcon();
            LogUtils.d(EdgeActivity.TAG, " user avatar = " + avatar);
            if (avatar != null) {
                ImageEngine.load(CoreManager.getDefault().getContext(), avatar, icon, R.mipmap.user);
            }
        }

        ((TextView) mContext.findViewById(R.id.user)).setText(user);
        ((TextView) mContext.findViewById(R.id.email)).setText(contact);
        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onUpdateUserIcon();
                }
            }
        });

        adapter.setSelected(Constant.MENU_SETTINGS);
    }

    private String[] loadScreenTitles() {
        return mContext.getResources().getStringArray(R.array.slidingDrawerTitles);
    }

    private Drawable[] loadScreenIcons() {
        TypedArray ta = mContext.getResources().obtainTypedArray(R.array.slidingDrawerIcons);
        Drawable[] icons = new Drawable[ta.length()];
        for (int i = 0; i < ta.length(); i++) {
            int id = ta.getResourceId(i, 0);
            if (id != 0) {
                icons[i] = ContextCompat.getDrawable(mContext, id);
            }
        }
        ta.recycle();
        return icons;
    }

    private DrawerItem createItemFor(int position) {
        return new SimpleItem(screenIcons[position], screenTitles[position])
//                .withIconTint(R.color.colorPrimary90)
                .withTextTint(R.color.icon)
//                .withSelectedIconTint(R.color.icon)
                .withSelectedTextTint(R.color.red);
    }

    @Override
    public void onItemSelected(int position) {
        slidingRootNav.closeMenu();
        Message msg = Message.obtain();
        msg.what = H.MSG_DLIDE_CLICK;
        msg.arg1 = position;
        mHandler.sendMessageDelayed(msg, H.EDGE_DELAY_TIME * 2);
    }

    public void onItemClick(int position) {
        if (listener != null) {
            listener.onItemSelected(position);
        }
    }

    public View getView(int postion) {
        return adapter.getView(postion);
    }

    public void openMenu() {
        slidingRootNav.openMenu(true);
    }

    public void closeMenu() {
        slidingRootNav.closeMenu(true);
    }

    private OnItemSelectedListener listener;

    public void setListener(OnItemSelectedListener listener) {
        this.listener = listener;
    }

    public interface OnItemSelectedListener {
        void onItemSelected(int position);

        void onUpdateUserIcon();
    }
}
