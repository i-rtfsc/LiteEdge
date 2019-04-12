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

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.journeyOS.base.Constant;
import com.journeyOS.base.guide.LiteGuide;
import com.journeyOS.base.guide.OnGuideClickListener;
import com.journeyOS.base.persistence.SpUtils;
import com.journeyOS.base.utils.BaseUtils;
import com.journeyOS.base.utils.BitmapUtils;
import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.base.utils.UIUtils;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.SyncMarket;
import com.journeyOS.core.api.edgeprovider.IAppProvider;
import com.journeyOS.core.api.edgeprovider.ICityProvider;
import com.journeyOS.core.api.plugins.IPlugins;
import com.journeyOS.core.api.thread.ICoreExecutors;
import com.journeyOS.core.base.BaseActivity;
import com.journeyOS.core.database.user.EdgeUser;
import com.journeyOS.core.permission.IPermission;
import com.journeyOS.edge.EdgeServiceManager;
import com.journeyOS.edge.H;
import com.journeyOS.edge.R;
import com.journeyOS.edge.SlidingDrawer;
import com.journeyOS.i007Service.core.notification.NotificationListenerService;

import java.util.List;

import butterknife.BindView;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadBatchListener;

public class EdgeActivity extends BaseActivity implements SlidingDrawer.OnItemSelectedListener {
    public static final String TAG = EdgeActivity.class.getSimpleName();

    private static final int ALBUM_REQUEST_CODE = 0x0000bacd;

    private final H mHandler = H.getDefault().getHandler();

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
                CoreManager.getDefault().getImpl(IAppProvider.class).loadApps();
            }
        });

        boolean barrage = SpUtils.getInstant().getBoolean(Constant.BARRAGE, Constant.BARRAGE_DEFAULT);
//        if (barrage) {
        Intent intent = new Intent(this, NotificationListenerService.class);
        startService(intent);
//        }
    }

    @Override
    public void initViews() {
        UIUtils.setStatusBarColor(this, this.getResources().getColor(R.color.colorPrimary));
        setSupportActionBar(mToolbar);
        mContainer = findViewById(R.id.container);

        EdgeServiceManager.getDefault().bindEgdeService();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean exclude = SpUtils.getInstant().getBoolean(Constant.EXCLUDE, Constant.EXCLUDE_DEFAULT);
        if (exclude) {
            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            if (am != null) {
                List<ActivityManager.AppTask> tasks = am.getAppTasks();
                if (tasks != null && tasks.size() > 0) {
                    ActivityManager.AppTask task = tasks.get(0);
                    if (task != null) {
                        task.setExcludeFromRecents(true);
                    }
                }
            }
        }

        SyncMarket.getDefault().get(new SyncMarket.onVersionObservable() {
            @Override
            public void onResult(boolean needUpdate, final String version, final String description) {
                LogUtils.d(TAG, "sync market,  needUpdate = [" + needUpdate + "], version = [" + version + "], description = [" + description + "]");
                if (needUpdate) {
                    CoreManager.getDefault().getImpl(ICoreExecutors.class).mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            final AlertDialog dialog = new AlertDialog.Builder(mContext, com.journeyOS.plugins.R.style.CornersAlertDialog)
                                    .setTitle(mContext.getString(R.string.update_dialog_title) + version)
                                    .setMessage(mContext.getString(R.string.update_dialog_message) + description)
                                    .setPositiveButton(mContext.getString(R.string.update_dialog_download), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int i) {
                                            boolean success = BaseUtils.openInMarket(mContext, SyncMarket.MARKET);
                                            if (!success) {
                                                BaseUtils.openBrowser(mContext, SyncMarket.getDefault().getMarketUrl());
                                            }
                                        }
                                    })
                                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int i) {
                                            dialog.dismiss();
                                        }
                                    })
                                    .create();
                            dialog.show();
                        }
                    });
                }
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        getPermission();
        if (!CoreManager.getDefault().getImpl(IPermission.class).isAdminActive(mContext)) {
            SlidingDrawer.getDefault().initDrawer(this, mBundle, mToolbar);
            SlidingDrawer.getDefault().setListener(this);
        }
        mToolbar.setTitle(R.string.app_name);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!CoreManager.getDefault().getImpl(IPermission.class).isAdminActive(mContext)) {
            mHandler.sendEmptyMessageDelayed(H.MSG_DRAWER_RELEASE, 0);
        }
    }

    @Override
    protected void initDataObserver(Bundle savedInstanceState) {
        super.initDataObserver(savedInstanceState);
        mBundle = savedInstanceState;
        if (CoreManager.getDefault().getImpl(IPermission.class).isAdminActive(mContext)) {
            SlidingDrawer.getDefault().initDrawer(this, mBundle, mToolbar);
            SlidingDrawer.getDefault().setListener(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (ALBUM_REQUEST_CODE == requestCode) {
                final Uri uri = data.getData();
                String photoPath = null;
                if (DocumentsContract.isDocumentUri(getBaseContext(), uri)) {
                    photoPath = BitmapUtils.getPicturePath(getBaseContext(), uri);// photos
                } else {
                    photoPath = BitmapUtils.getPhotoImage(getBaseContext(), data);// gallery
                }
                final String[] filePaths = new String[1];
                filePaths[0] = photoPath;
                BmobFile.uploadBatch(filePaths, new UploadBatchListener() {
                    @Override
                    public void onSuccess(List<BmobFile> files, List<String> urls) {
                        LogUtils.d(TAG, "success, urls = [" + urls + "]");
                        if (urls != null && urls.size() > 0) {
                            if (BmobUser.isLogin()) {
                                EdgeUser edgeUser = BmobUser.getCurrentUser(EdgeUser.class);
                                edgeUser.setIcon(urls.get(0));
                                edgeUser.update(edgeUser.getObjectId(), new UpdateListener() {
                                    @Override
                                    public void done(BmobException e) {
                                        LogUtils.d(TAG, "upload user icon url");
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onProgress(int i, int i1, int i2, int i3) {
                        LogUtils.d(TAG, "on progress, current index = [" + i + "]," +
                                " current percent = [" + i1 + "]," +
                                " total = [" + i2 + "]," +
                                " total percent = [" + i3 + "]");
                    }

                    @Override
                    public void onError(int i, String s) {
                        LogUtils.d(TAG, "error, status code = [" + i + "], error mssage = [" + s + "]");
                    }
                });
            }
        }
    }

    @Override
    public void onItemSelected(int position) {
        handleItemSelected(position);
    }

    @Override
    public void onUpdateUserIcon() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != 0x02) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);
                return;
            }
        }

        Intent albumIntent = new Intent(Intent.ACTION_PICK, null);
        albumIntent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(albumIntent, ALBUM_REQUEST_CODE);
    }

    void loadFragment(Fragment fragment) {
        //SlidingDrawer.getDefault().closeMenu(true);
        mContainer.setVisibility(View.VISIBLE);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragment)
                .commitAllowingStateLoss();
    }

    void handleItemSelected(int position) {
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
            case Constant.MENU_BARRAGE:
                mToolbar.setTitle(R.string.menu_barrage);
                loadFragment(CoreManager.getDefault().getImpl(IPlugins.class).provideBarrageFragment(mContext));
                break;
            case Constant.MENU_LAB:
                mToolbar.setTitle(R.string.menu_lab);
                loadFragment(CoreManager.getDefault().getImpl(IPlugins.class).provideLabFragment(mContext));
                break;
            case Constant.MENU_ABOUT:
                mToolbar.setTitle(R.string.menu_about);
                loadFragment(CoreManager.getDefault().getImpl(IPlugins.class).provideAboutFragment(mContext));
                break;
            case Constant.MENU_LEARN:
                CoreManager.getDefault().getImpl(IPlugins.class).navigationLearnActivity(mContext);
                break;
        }

        for (int i = 0; i < 10000; i++) {
        }
        initGuideView();
    }

    void getPermission() {
        boolean inited = SpUtils.getInstant().getBoolean(Constant.GUIDE_INITED, false);
        if (inited) {
            if (!CoreManager.getDefault().getImpl(IPermission.class).canDrawOverlays(mContext)) {
                CoreManager.getDefault().getImpl(IPermission.class).drawOverlays(mContext, true);
            } else {
                if (!CoreManager.getDefault().getImpl(IPermission.class).hasListenerNotification(mContext)) {
                    CoreManager.getDefault().getImpl(IPermission.class).listenerNotification(mContext, true);
                }
            }
        }
    }

    void initGuideView() {
        boolean inited = SpUtils.getInstant().getBoolean(Constant.GUIDE_INITED, false);
        if (inited) {
            return;
        }

        if (mLiteGuide == null) {
            mLiteGuide = new LiteGuide(this);
            if (mToolbar != null) {
                mLiteGuide.addNextTarget(mToolbar,
                        mContext.getResources().getString(R.string.guide_menu_open),
                        50, 100);
            }

            View user = SlidingDrawer.getDefault().getView(Constant.MENU_USER);
            if (user != null) {
                mLiteGuide.addNextTarget(user,
                        mContext.getResources().getString(R.string.guide_user),
                        350, 5);
            }
            View permission = SlidingDrawer.getDefault().getView(Constant.MENU_PERMISSION);
            if (permission != null) {
                mLiteGuide.addNextTarget(permission,
                        mContext.getResources().getString(R.string.guide_permission),
                        350, -5, 350, ViewGroup.LayoutParams.WRAP_CONTENT);
            }
            View settings = SlidingDrawer.getDefault().getView(Constant.MENU_SETTINGS);
            if (settings != null) {
                mLiteGuide.addNextTarget(settings,
                        mContext.getResources().getString(R.string.guide_settings),
                        350, -20);
            }
            View learn = SlidingDrawer.getDefault().getView(Constant.MENU_LEARN);
            if (learn != null) {
                mLiteGuide.addNextTarget(learn,
                        mContext.getResources().getString(R.string.guide_learn),
                        280, 20, 500, ViewGroup.LayoutParams.WRAP_CONTENT);
            }

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
                SlidingDrawer.getDefault().openMenu();
            }
        }

        @Override
        public void onGuideFinished() {
            LogUtils.d(TAG, "guide finished");
            SpUtils.getInstant().put(Constant.GUIDE_INITED, true);
            handleItemSelected(Constant.MENU_LEARN);
            SlidingDrawer.getDefault().closeMenu(false);
        }

        @Override
        public void onTarget(int index) {
            handleItemSelected(index - 1);
        }
    }
}
