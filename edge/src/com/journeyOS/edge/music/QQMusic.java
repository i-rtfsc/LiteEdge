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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.journeyOS.base.BuildConfig;
import com.journeyOS.base.Constant;
import com.journeyOS.base.persistence.SpUtils;
import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.base.utils.Singleton;
import com.journeyOS.base.utils.UIUtils;
import com.journeyOS.core.CoreManager;
import com.journeyOS.edge.R;
import com.journeyOS.edge.wm.BarrageManager;

@Deprecated
public class QQMusic {
    private static final String TAG = QQMusic.class.getSimpleName();

    MusicInfo mMusicInfo = new MusicInfo();

    Context mContext = null;

    Resources mResources = null;

    ViewGroup mNotificationRoot;

    static boolean sShowBarrage = false;

    //以下为原生样式
    //上一首
    private static final String QQ_NATIVE_BTN_PRE = "player_notification_pre";
    //暂停
    private static final String QQ_NATIVE_BTN_PAUSE = "player_notification_pause";
    //播放
    private static final String QQ_NATIVE_BTN_PLAY = "player_notification_play";
    //下一首
    private static final String QQ_NATIVE_BTN_NEXT = "player_notification_next";

    private QQMusic() {
        mContext = CoreManager.getDefault().getContext();
        try {
            mResources = mContext.getPackageManager().getResourcesForApplication(MusicManager.MUSIC_QQ);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static final Singleton<QQMusic> gDefault = new Singleton<QQMusic>() {
        @Override
        protected QQMusic create() {
            return new QQMusic();
        }
    };

    public static QQMusic getDefault() {
        return gDefault.get();
    }

    public MusicInfo qq(Notification notification, String packageName) {
        mMusicInfo.setPackageName(packageName);
//        if (notification.extras != null &&
//                notification.extras.containsKey("android.template") &&
//                notification.extras.getString("android.template").contains("android.app.Notification$MediaStyle")) {
        if (notification.extras != null &&
                notification.extras.containsKey(NotificationCompat.EXTRA_MEDIA_SESSION)) {
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
            mMusicInfo.setName(notification.tickerText.toString());

            int actionCount = NotificationCompat.getActionCount(notification);
            if (BuildConfig.DEBUG) LogUtils.d(TAG, "action count = " + actionCount);
            for (int i = 0; i < actionCount; i++) {
                final NotificationCompat.Action action = NotificationCompat.getAction(notification, i);
                if (mResources != null) {
                    try {
                        String iconId = mResources.getResourceEntryName(action.getIcon());
                        LogUtils.d(TAG, "iconId = " + iconId);
                        switch (iconId) {
                            case QQ_NATIVE_BTN_PAUSE:
                                MusicAction pause = new MusicAction() {
                                    @Override
                                    public void run() throws Exception {
                                        action.actionIntent.send();
                                    }
                                };
                                mMusicInfo.setClick(pause);
                                mMusicInfo.setPlaying(true);
                                break;
                            case QQ_NATIVE_BTN_PLAY:
                                MusicAction play = new MusicAction() {
                                    @Override
                                    public void run() throws Exception {
                                        action.actionIntent.send();
                                    }
                                };
                                mMusicInfo.setClick(play);
                                mMusicInfo.setPlaying(false);
                                break;
                            case QQ_NATIVE_BTN_NEXT:
                                MusicAction next = new MusicAction() {
                                    @Override
                                    public void run() throws Exception {
                                        action.actionIntent.send();
                                    }
                                };
                                mMusicInfo.setNext(next);
                                break;
                            case QQ_NATIVE_BTN_PRE:
                                MusicAction pre = new MusicAction() {
                                    @Override
                                    public void run() throws Exception {
                                        action.actionIntent.send();
                                    }
                                };
                                mMusicInfo.setLast(pre);
                                break;
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
                        LinearLayout linearLayout = (LinearLayout) mNotificationRoot.getChildAt(i);
                        //上一首
                        final View vLast = linearLayout.getChildAt(1);
                        MusicAction pre = new MusicAction() {
                            @Override
                            public void run() throws Exception {
                                vLast.performClick();
                                sShowBarrage = true;
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
                                sShowBarrage = true;
                            }
                        };
                        mMusicInfo.setNext(next);

                    } else if (mNotificationRoot.getChildAt(i) instanceof RelativeLayout) {

                        RelativeLayout relativeLayout = (RelativeLayout) mNotificationRoot.getChildAt(i);

                        //封面布局
                        if (relativeLayout.getChildCount() == 1) {
                            Drawable drawable = ((ImageView) (relativeLayout.getChildAt(0))).getDrawable();
                            mMusicInfo.setAlbumCover(drawable);
                        }

                        //信息布局
                        if (relativeLayout.getChildCount() == 3) {
                            for (int j = 0; j < relativeLayout.getChildCount(); j++) {
                                if (relativeLayout.getChildAt(j) instanceof TextView) {

                                    TextView t = (TextView) relativeLayout.getChildAt(j);
                                    int inn = t.getCurrentTextColor();
                                    //黑色字体 为歌曲名称
                                    if (inn == -15263977) {
                                        mMusicInfo.setName(t.getText().toString());
                                    }
                                    //灰色名称 为歌手名字
                                    if (inn == -1979711488) {
                                        mMusicInfo.setSinger(t.getText().toString());
                                    }
                                }

                            }
                        }

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
}
