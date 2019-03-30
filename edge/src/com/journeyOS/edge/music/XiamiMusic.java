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
import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.base.utils.Singleton;
import com.journeyOS.base.utils.UIUtils;
import com.journeyOS.core.CoreManager;

public class XiamiMusic {
    private static final String TAG = XiamiMusic.class.getSimpleName();

    MusicInfo mMusicInfo = new MusicInfo();

    Context mContext = null;

    Resources mResources = null;

    ViewGroup mNotificationRoot;

    //以下为原生样式(惊呆了这些ID名字)
    //喜欢
    private static final String XIAMI_NATIVE_BTN_LIKE = "maintabbar_button_setting_selector_not_skin";
    //上一首
    private static final String XIAMI_NATIVE_BTN_PRE = "maintabbar_button_search_highlight";
    //暂停
    private static final String XIAMI_NATIVE_BTN_PAUSE = "maintabbar_button_recognizer_normal_for_old";
    //播放
    private static final String XIAMI_NATIVE_BTN_PLAY = "maintabbar_button_recognizer_normal_for_old";
    //下一首
    private static final String XIAMI_NATIVE_BTN_NEXT = "maintabbar_button_recognizer_normal";
    //歌词
    private static final String XIAMI_NATIVE_BTN_LYRIC = "maintabbar_button_setting_normal";

    private XiamiMusic() {
        mContext = CoreManager.getDefault().getContext();
        try {
            mResources = mContext.getPackageManager().getResourcesForApplication(MusicManager.MUSIC_QQ);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
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
                        switch (iconId) {
//                            case XIAMI_NATIVE_BTN_PAUSE:
//                                MusicAction pause = new MusicAction() {
//                                    @Override
//                                    public void run() throws Exception {
//                                        action.actionIntent.send();
//                                    }
//                                };
//                                mMusicInfo.setClick(pause);
//                                mMusicInfo.setPlaying(true);
//                                break;
                            case XIAMI_NATIVE_BTN_PLAY:
                                MusicAction play = new MusicAction() {
                                    @Override
                                    public void run() throws Exception {
                                        action.actionIntent.send();
                                    }
                                };
                                mMusicInfo.setClick(play);
                                mMusicInfo.setPlaying(true);
                                break;
                            case XIAMI_NATIVE_BTN_NEXT:
                                MusicAction next = new MusicAction() {
                                    @Override
                                    public void run() throws Exception {
                                        action.actionIntent.send();
                                    }
                                };
                                mMusicInfo.setNext(next);
                                break;
                            case XIAMI_NATIVE_BTN_PRE:
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
        return mMusicInfo;
    }
}
