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
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.os.SystemClock;
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
import com.journeyOS.base.persistence.SpUtils;
import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.base.utils.Singleton;
import com.journeyOS.base.utils.UIUtils;
import com.journeyOS.core.AccountManager;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.ImageEngine;
import com.journeyOS.core.api.thread.ICoreExecutors;
import com.journeyOS.core.database.user.EdgeUser;
import com.journeyOS.edge.ui.activity.EdgeActivity;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;

import java.util.ArrayList;
import java.util.List;

public class SlidingDrawer implements DrawerAdapter.OnItemSelectedListener {

    private Activity mContext;
    private String[] screenTitles;
    private Drawable[] screenIcons;
    private DrawerAdapter adapter;
    private SlidingRootNav slidingRootNav;

    private String mUser;
    private String mContact;
    private String mPhone;
    private String mAvatar;

    private boolean isAdded = false;
    private final static int COUNTS = 5;
    long[] mHints = new long[COUNTS];
    private final static long DURATION = 3 * 1000;

    private final H mHandler = H.getDefault().getHandler();

    private SlidingDrawer() {
        fetchUserInfo();
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
        mContext = context;
        slidingRootNav = new SlidingRootNavBuilder(mContext)
                .withToolbarMenuToggle(toolbar)
                .withMenuOpened(false)
                .withContentClickableWhenMenuOpened(false)
                .withMenuLayout(R.layout.menu_left_drawer)
                .withSavedState(bundle)
                .inject();

        screenIcons = loadScreenIcons();
        screenTitles = loadScreenTitles();

        List<DrawerItem> items = initItems();
        //新增管理员选项
        EdgeUser edgeUser = AccountManager.getDefault().getCurrentUser();
        boolean isManager = (edgeUser != null ? edgeUser.manager : false);
        LogUtils.d(EdgeActivity.TAG, "user is manager = [" + isManager + "], has been added = [" + isAdded + "]");
        if (isManager || BuildConfig.DEBUG) {
            items.add(createItemFor(Constant.MENU_ADMIN));
            isAdded = true;
        }
        adapter = new DrawerAdapter(items);
        adapter.setListener(this);

        RecyclerView list = mContext.findViewById(R.id.list);
        list.setNestedScrollingEnabled(false);
        list.setLayoutManager(new LinearLayoutManager(mContext));
        list.setAdapter(adapter);
        adapter.setSelected(Constant.MENU_SETTINGS);

        initUserInfo();
    }

    private List<DrawerItem> initItems() {
        List<DrawerItem> items = new ArrayList<>();
        items.add(createItemFor(Constant.MENU_USER));
        items.add(createItemFor(Constant.MENU_PERMISSION));
        items.add(createItemFor(Constant.MENU_SETTINGS).setChecked(true));
        items.add(createItemFor(Constant.MENU_BARRAGE));
        items.add(createItemFor(Constant.MENU_LAB));
        items.add(createItemFor(Constant.MENU_ABOUT));
        items.add(createItemFor(Constant.MENU_LEARN));

        return items;
    }

    private void fetchUserInfo() {
        mUser = mContact = mPhone = mAvatar = CoreManager.getDefault().getContext().getString(R.string.not_set);
        EdgeUser edgeUser = AccountManager.getDefault().getCurrentUser();
        if (edgeUser != null) {
            mUser = edgeUser.nickname;
            if (mUser == null || mUser == "") {
                mUser = CoreManager.getDefault().getContext().getString(R.string.not_set);
            }
            mContact = edgeUser.getEmail();
            mPhone = edgeUser.getMobilePhoneNumber();
            if (mContact == null || mContact == "") {
                mContact = edgeUser.getMobilePhoneNumber();
            }
            mAvatar = edgeUser.icon;
            LogUtils.d(EdgeActivity.TAG, " user phone = " + mPhone);
            LogUtils.d(EdgeActivity.TAG, " user contact = " + mContact);
            LogUtils.d(EdgeActivity.TAG, " user avatar = " + mAvatar);
        }
    }

    public void initUserInfo() {
        fetchUserInfo();
        ((TextView) mContext.findViewById(R.id.user)).setText(mUser);
        ((TextView) mContext.findViewById(R.id.email)).setText(mContact);
        final ImageView icon = ((ImageView) mContext.findViewById(R.id.user_avatar));

        if (mAvatar != null) {
            ImageEngine.load(CoreManager.getDefault().getContext(), mAvatar, icon, R.drawable.svg_avatar);
        } else {
            if (Constant.LOCAL_ICON) {
                CoreManager.getDefault().getImpl(ICoreExecutors.class).diskIOThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        String base64 = SpUtils.getInstant(Constant.SP_BIG_FILE).getString(Constant.BIG_FILE_USER_ICON, null);
                        if (base64 != null) {
                            final Bitmap bitmap = UIUtils.Base64ToBitmap(base64);
                            CoreManager.getDefault().getImpl(ICoreExecutors.class).mainThread().execute(new Runnable() {
                                @Override
                                public void run() {
                                    icon.setImageBitmap(bitmap);
                                }
                            });
                        }
                    }
                });
            }
        }

        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onUpdateUserIcon();
                }
            }
        });

        mContext.findViewById(R.id.layout_user_info).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startManager();
            }
        });
    }

    public void initNoneUserInfo() {
        mUser = mContact = mPhone = mAvatar = CoreManager.getDefault().getContext().getString(R.string.not_set);
        ((TextView) mContext.findViewById(R.id.user)).setText(mUser);
        ((TextView) mContext.findViewById(R.id.email)).setText(mContact);
        ((ImageView) mContext.findViewById(R.id.user_avatar)).setBackgroundResource(R.drawable.svg_avatar);

        List<DrawerItem> items = initItems();
        if (adapter != null) {
            adapter.updateItems(items);
        }
    }

    public void setUserAvatar(Bitmap bitmap) {
        ImageView icon = ((ImageView) mContext.findViewById(R.id.user_avatar));
        icon.setImageBitmap(bitmap);
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
        closeMenu(true);
        Message msg = Message.obtain();
        msg.what = H.MSG_SLIDE_CLICK;
        msg.arg1 = position;
//        mHandler.sendMessageDelayed(msg, H.EDGE_DELAY_TIME * 2);
        mHandler.sendMessage(msg);
    }

    @Override
    public void onBindViewFinished() {
        if (listener != null) {
            listener.initViewFinished();
        }
    }

    public void onItemClick(int position) {
        if (listener != null) {
            listener.onItemSelected(position);
        }
    }

    public View getView(int postion) {
        if (adapter == null) {
            return null;
        }

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

    private void startManager() {
        if (!isAdded) {
            System.arraycopy(mHints, 1, mHints, 0, mHints.length - 1);
            mHints[mHints.length - 1] = SystemClock.uptimeMillis();
            if (mHints[0] >= (SystemClock.uptimeMillis() - DURATION)) {
                if (adapter != null) {
                    List<DrawerItem> items = adapter.getItems();
                    DrawerItem item = createItemFor(Constant.MENU_ADMIN);
                    if (!items.contains(item)) {//something wrong
                        //新增管理员选项
                        EdgeUser edgeUser = AccountManager.getDefault().getCurrentUser();
                        boolean isManager = (edgeUser != null ? edgeUser.manager : false);
                        LogUtils.d(EdgeActivity.TAG, "user is manager = [" + isManager + "], has been added = [" + isAdded + "]");
                        if (isManager || BuildConfig.DEBUG) {
                            adapter.updateItem(item);
                            isAdded = true;
                        }
                    }
                }
            }
        }
    }

    private OnItemSelectedListener listener;

    public void setListener(OnItemSelectedListener listener) {
        this.listener = listener;
    }

    public interface OnItemSelectedListener {
        void onItemSelected(int position);

        void onUpdateUserIcon();

        void initViewFinished();
    }
}
