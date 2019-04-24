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
import com.journeyOS.base.utils.Singleton;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.ImageEngine;
import com.journeyOS.core.database.user.EdgeUser;
import com.journeyOS.core.permission.IPermission;
import com.journeyOS.edge.ui.activity.EdgeActivity;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;

public class SlidingDrawer implements DrawerAdapter.OnItemSelectedListener {

    private Activity mContext;
    private String[] screenTitles;
    private Drawable[] screenIcons;
    private DrawerAdapter adapter;
    private SlidingRootNav slidingRootNav;

    private String mUser = Constant.USER;
    private String mContact = Constant.EMAIL;
    private String mPhone = null;
    private String mAvatar = null;

    private final H mHandler = H.getDefault().getHandler();


    private SlidingDrawer() {
        if (BmobUser.isLogin()) {
            EdgeUser edgeUser = BmobUser.getCurrentUser(EdgeUser.class);
            mUser = edgeUser.getNickname();
            mContact = edgeUser.getEmail();
            mPhone = edgeUser.getMobilePhoneNumber();
            if (mContact == null || mContact == "") {
                mContact = edgeUser.getMobilePhoneNumber();
            }
            mAvatar = edgeUser.getIcon();
            LogUtils.d(EdgeActivity.TAG, " user phone = " + mPhone);
            LogUtils.d(EdgeActivity.TAG, " user contact = " + mContact);
            LogUtils.d(EdgeActivity.TAG, " user avatar = " + mAvatar);
        }
    }

    private static final Singleton<SlidingDrawer> gDefault = new Singleton<SlidingDrawer>() {
        @Override
        protected SlidingDrawer create() {
            return new SlidingDrawer();
        }
    };

    public static SlidingDrawer getDefault() {
        return gDefault.get();
    }

    public void initDrawer(Activity context, Bundle bundle, Toolbar toolbar) {
        if (!CoreManager.getDefault().getImpl(IPermission.class).isAdminActive(context)) {
            releaseDrawer();
        }
        mContext = context;
        slidingRootNav = new SlidingRootNavBuilder(mContext)
                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withMenuLayout(R.layout.menu_left_drawer)
//                .withSavedState(bundle)
                .inject();

        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();

        List<DrawerItem> items = new ArrayList<>();
        items.add(createItemFor(Constant.MENU_USER));
        items.add(createItemFor(Constant.MENU_PERMISSION));
        items.add(createItemFor(Constant.MENU_SETTINGS).setChecked(true));
        items.add(createItemFor(Constant.MENU_BARRAGE));
        items.add(createItemFor(Constant.MENU_LAB));
        items.add(createItemFor(Constant.MENU_ABOUT));
        items.add(createItemFor(Constant.MENU_LEARN));
        //新增管理员选项
        //手机号Constant.PHONE、Constant.PHONE_TEST或者DEBUG版本都认为是管理员
        if (Constant.PHONE.equals(mPhone)
                || Constant.PHONE_TEST.equals(mPhone)
                || BuildConfig.DEBUG) {
            items.add(createItemFor(Constant.MENU_ADMIN));
        }

        adapter = new DrawerAdapter(items);
        adapter.setListener(this);

        RecyclerView list = mContext.findViewById(R.id.list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(mContext));

        list.setAdapter(adapter);

        ((TextView) mContext.findViewById(R.id.user)).setText(mUser);
        ((TextView) mContext.findViewById(R.id.email)).setText(mContact);
        ImageView icon = ((ImageView) mContext.findViewById(R.id.user_avatar));
        if (mAvatar != null) {
            ImageEngine.load(CoreManager.getDefault().getContext(), mAvatar, icon, R.mipmap.user);
        }

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

    public void releaseDrawer() {
        closeMenu(false);
        slidingRootNav = null;
        mContext = null;
        screenTitles = null;
        screenIcons = null;
        adapter = null;
        slidingRootNav = null;
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
        closeMenu(false);
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
        if (slidingRootNav != null) {
            slidingRootNav.openMenu(true);
        }
    }

    public void closeMenu(boolean isAnimator) {
        if (slidingRootNav != null) {
            slidingRootNav.closeMenu(isAnimator);
        }
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
