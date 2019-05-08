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

package com.journeyOS.edge.music;

import android.app.Notification;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.journeyOS.base.BuildConfig;
import com.journeyOS.base.Constant;
import com.journeyOS.base.persistence.SpUtils;
import com.journeyOS.base.utils.AppUtils;
import com.journeyOS.base.utils.JsonHelper;
import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.base.utils.Singleton;
import com.journeyOS.base.utils.UIUtils;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.api.edgeprovider.IMusicProvider;
import com.journeyOS.core.api.thread.ICoreExecutors;
import com.journeyOS.core.database.music.Music;
import com.journeyOS.core.database.music.MusicAir;
import com.journeyOS.core.database.music.MusicConfig;
import com.journeyOS.edge.R;
import com.journeyOS.edge.wm.BarrageManager;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

@Deprecated
public class XiamiMusic {
    private static final String TAG = XiamiMusic.class.getSimpleName();

    MusicInfo mMusicInfo = new MusicInfo();

    Context mContext = null;

    Resources mResources = null;

    ViewGroup mNotificationRoot;

    Music mMusic;

    static boolean sShowBarrage = false;

    private static final String XIAMI_OBJECT_ID = "fd65456140";

//    //version = 8.0.0
//    //上一首
//    private static String XIAMI_NATIVE_BTN_PRE = "lyric_poster_tab_lyric";
//    //播放
//    private static String XIAMI_NATIVE_BTN_PLAY = "lyric_poster_tab_barcode";
//    //下一首
//    private static String XIAMI_NATIVE_BTN_NEXT = "lyric_poster_tab_background";

    //version = 8.0.1.6
    //上一首
    private static String XIAMI_NATIVE_BTN_PRE = "main_desk_plus_listen_rec";
    //播放
    private static String XIAMI_NATIVE_BTN_PLAY = "main_desk_plus_img_scan_dark";
    //下一首
    private static String XIAMI_NATIVE_BTN_NEXT = "main_desk_plus_img_scan";

    private XiamiMusic() {
        mContext = CoreManager.getDefault().getContext();
        try {
            mResources = mContext.getPackageManager().getResourcesForApplication(MusicManager.MUSIC_QQ);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        getConfigs();
    }

    private static final Singleton<XiamiMusic> gDefault = new Singleton<XiamiMusic>() {
        @Override
        protected XiamiMusic create() {
            return new XiamiMusic();
        }
    };

    public static XiamiMusic getDefault() {
        return gDefault.get();
    }

    public MusicInfo xiami(Notification notification, String packageName) {
        mMusicInfo.setPackageName(packageName);
        String version = AppUtils.getAppVersionName(mContext, packageName);
        LogUtils.d(TAG, " packageName = [" + packageName + "], version = [" + version + "]");

        if (notification.extras != null && notification.extras.containsKey(NotificationCompat.EXTRA_MEDIA_SESSION)) {
            if (BuildConfig.DEBUG) LogUtils.d(TAG, packageName + " 采用系统样式");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (notification.getLargeIcon() != null) {
                    Drawable drawable = notification.getLargeIcon().loadDrawable(mContext);
                    mMusicInfo.setAlbumCover(drawable);
                }
            }

            String text = notification.extras.getString("android.Text");
            if (text == null) text = "";

            String[] texts = text.split(" - ");
            if (texts.length == 2) {
                mMusicInfo.setSinger(texts[0]);
                mMusicInfo.setAlbum(texts[1]);

            }
//            mMusicInfo.setName(notification.tickerText.toString());

            int actionCount = NotificationCompat.getActionCount(notification);
            if (BuildConfig.DEBUG) LogUtils.d(TAG, "action count = " + actionCount);
            for (int i = 0; i < actionCount; i++) {
                final NotificationCompat.Action action = NotificationCompat.getAction(notification, i);
                if (mResources != null) {
                    try {
                        String iconId = mResources.getResourceEntryName(action.getIcon());
                        LogUtils.d(TAG, "iconId = " + iconId);

                        if (XIAMI_NATIVE_BTN_PLAY.equals(iconId)) {
                            MusicAction play = new MusicAction() {
                                @Override
                                public void run() throws Exception {
                                    action.actionIntent.send();
                                }
                            };
                            mMusicInfo.setClick(play);
                            mMusicInfo.setPlaying(true);
                        } else if (XIAMI_NATIVE_BTN_NEXT.equals(iconId)) {

                            MusicAction next = new MusicAction() {
                                @Override
                                public void run() throws Exception {
                                    action.actionIntent.send();
                                    sShowBarrage = true;
                                }
                            };
                            mMusicInfo.setNext(next);
                        } else if (XIAMI_NATIVE_BTN_PRE.equals(iconId)) {
                            MusicAction pre = new MusicAction() {
                                @Override
                                public void run() throws Exception {
                                    action.actionIntent.send();
                                    sShowBarrage = true;
                                }
                            };
                            mMusicInfo.setLast(pre);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            try {
                if (BuildConfig.DEBUG) LogUtils.d(TAG, packageName + " 采用普通样式");
                mNotificationRoot = (ViewGroup) notification.bigContentView.apply(mContext, new FrameLayout(mContext));
                if (BuildConfig.DEBUG)
                    LogUtils.d(TAG, "notification view root was null = " + mNotificationRoot == null);
                if (mNotificationRoot == null) {
                    return null;
                }

                if (BuildConfig.DEBUG)
                    LogUtils.d(TAG, "child count = " + mNotificationRoot.getChildCount());
                for (int i = 0; i < mNotificationRoot.getChildCount(); i++) {
                    if (BuildConfig.DEBUG) LogUtils.d(TAG, "child view " + i + " , = "
                            + mNotificationRoot.getChildAt(i)
                            + " id = " + mNotificationRoot.getChildAt(i).getId());
                    if (mNotificationRoot.getChildAt(i) instanceof LinearLayout) {
                        LinearLayout rootLinearLayout = (LinearLayout) mNotificationRoot.getChildAt(i);

                        if (rootLinearLayout.getChildAt(i) instanceof LinearLayout) {
                            LinearLayout linearLayout = (LinearLayout) rootLinearLayout.getChildAt(i);

                            if (linearLayout.getChildCount() == 5) {
                                //上一首
                                final View vLast = linearLayout.getChildAt(1);
                                MusicAction pre = new MusicAction() {
                                    @Override
                                    public void run() throws Exception {
                                        vLast.performClick();
                                    }
                                };
                                mMusicInfo.setLast(pre);

                                //播放按钮
                                final ImageView vPlay = (ImageView) linearLayout.getChildAt(2);
                                //这里通过拿到View的图片资源Bitmap 判断中间像素点颜色来判断是否播放
                                Bitmap mp = UIUtils.drawableToBitmap(vPlay.getDrawable());
                                int color = mp.getPixel(mp.getWidth() / 2, mp.getHeight() / 2);
                                MusicAction play = new MusicAction() {
                                    @Override
                                    public void run() throws Exception {
                                        vPlay.performClick();
                                    }
                                };
                                mMusicInfo.setClick(play);
                                if (color == 0) {
                                    mMusicInfo.setPlaying(true);
                                } else {
                                    mMusicInfo.setPlaying(false);
                                }

                                //下一首按钮
                                final View vNext = linearLayout.getChildAt(3);
                                MusicAction next = new MusicAction() {
                                    @Override
                                    public void run() throws Exception {
                                        vNext.performClick();
                                    }
                                };
                                mMusicInfo.setNext(next);
                            }
                            if (linearLayout.getChildCount() == 2) {
                                if (linearLayout.getChildAt(0) instanceof LinearLayout) {
                                    if (((LinearLayout) linearLayout.getChildAt(0)).getChildAt(0) instanceof TextView) {
                                        TextView tv = (TextView) ((LinearLayout) linearLayout.getChildAt(0)).getChildAt(0);
                                        mMusicInfo.setName(tv.getText().toString());
                                    }
                                }
                                if (linearLayout.getChildAt(1) instanceof TextView) {
                                    TextView tv = (TextView) linearLayout.getChildAt(1);
                                    mMusicInfo.setSinger(tv.getText().toString());
                                }

                            }
                        }


                    } else if (mNotificationRoot.getChildAt(i) instanceof ImageView) {
                        Drawable drawable = ((ImageView) (mNotificationRoot.getChildAt(i))).getDrawable();
                        mMusicInfo.setAlbumCover(drawable);
                    }
                }
            } catch (Exception e) {
                LogUtils.d(TAG, "error = " + e.toString());
            }
        }
        if (SpUtils.getInstant().getBoolean(Constant.MUSIC_CONTROL_SHOW_BARRAGE, Constant.MUSIC_CONTROL_SHOW_BARRAGE_DEFAULT)
                && sShowBarrage && mMusicInfo != null) {
            sShowBarrage = false;
            Bitmap circleBitmap = null;
            Drawable drawable = mMusicInfo.getAlbumCover();
            if (drawable != null) {
                Bitmap bitmap = UIUtils.drawableToBitmap(drawable);
                circleBitmap = UIUtils.getCircularBitmap(bitmap);
            }

            BarrageManager.getDefault().sendBarrage(circleBitmap, mMusicInfo.getSinger(), mMusicInfo.getName() + mContext.getString(R.string.gesture_control_music));
        }
        return mMusicInfo;
    }

    private void getConfigs() {
        CoreManager.getDefault().getImpl(ICoreExecutors.class).diskIOThread().execute(new Runnable() {
            @Override
            public void run() {
                mMusic = CoreManager.getDefault().getImpl(IMusicProvider.class).getConfig(MusicManager.MUSIC_XIAMI);
                boolean needSync = initConfigs();

                if (!needSync) {
                    syncConfigs();
                }
            }
        });
    }

    private void syncConfigs() {
        BmobQuery<MusicAir> bmobQuery = new BmobQuery<>();
        bmobQuery.getObject(XIAMI_OBJECT_ID, new QueryListener<MusicAir>() {
            @Override
            public void done(final MusicAir musicAir, BmobException e) {
                if (e == null) {
                    if (musicAir != null) {
                        CoreManager.getDefault().getImpl(ICoreExecutors.class).diskIOThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                mMusic = new Music();
                                mMusic.packageName = MusicManager.MUSIC_XIAMI;
                                mMusic.config = musicAir.config;
                                initConfigs();
                                CoreManager.getDefault().getImpl(IMusicProvider.class).insertOrUpdateMusic(mMusic);
                            }
                        });
                    }
                }
            }
        });
    }

    private boolean initConfigs() {
        boolean init = false;
        if (mMusic != null && mMusic.config != null) {
            MusicConfig musicConfig = JsonHelper.fromJson(mMusic.config, MusicConfig.class);
            List<MusicConfig.Config> configs = musicConfig.configs;
            for (MusicConfig.Config config : configs) {
                LogUtils.d(TAG, " version = [" + config.version + "], config = [" + config.config + "]");
                Config localConfig = JsonHelper.fromJson(config.config, Config.class);
                if (AppUtils.getAppVersionName(mContext, MusicManager.MUSIC_XIAMI).equals(config.version)) {
                    XIAMI_NATIVE_BTN_PRE = localConfig.last;
                    XIAMI_NATIVE_BTN_PLAY = localConfig.play;
                    XIAMI_NATIVE_BTN_NEXT = localConfig.next;
                    init = true;
                }
            }

        }

        return init;
    }

    public static class Config {
        public String last;
        public String play;
        public String next;
    }
}
