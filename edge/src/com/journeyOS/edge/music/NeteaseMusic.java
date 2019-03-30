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

import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.base.utils.Singleton;
import com.journeyOS.base.utils.UIUtils;
import com.journeyOS.core.BuildConfig;
import com.journeyOS.core.CoreManager;

public class NeteaseMusic {
    private static final String TAG = NeteaseMusic.class.getSimpleName();

    MusicInfo mMusicInfo = new MusicInfo();

    Context mContext = null;

    Resources mResources = null;

    ViewGroup mNotificationRoot;

    //网易云音乐封面ID
    private static final String NETEASE_ALBUM = "notifyAlbumCover";

    //网易云音乐文字总ID
    private static final String NETEASE_TEXT = "playNotificationText";
    //网易云音乐歌名ID
    private static final String NETEASE_TEXT_NAME = "notifyTitle";
    //网易云音乐歌手ID
    private static final String NETEASE_TEXT_SING = "notifyText";

    //网易云音乐按钮总ID
    private static final String NETEASE_BTN = "playNotificationBtns";
    private static final String NETEASE_BTN_START = "playNotificationStar";
    //网易云音乐按钮歌词ID
    private static final String NETEASE_BTN_LYRIC = "playNotificationLyric";
    //网易云音乐按钮上一首ID
    private static final String NETEASE_BTN_PRE = "playNotificationPre";
    //网易云音乐按钮播放ID
    private static final String NETEASE_BTN_TOGGLE = "playNotificationToggle";
    //网易云音乐按钮下一首ID
    private static final String NETEASE_BTN_NEXT = "playNotificationNext";

    //以下为原生样式
    //我喜欢 开
    private static final String NETEASE_NATIVE_BTN_LOVED = "note_btn_loved";
    //我喜欢 关
    private static final String NETEASE_NATIVE_BTN_LOVE = "note_btn_love";
    //上一首
    private static final String NETEASE_NATIVE_BTN_PRE = "note_btn_pre";
    //暂停
    private static final String NETEASE_NATIVE_BTN_PAUSE = "note_btn_pause_ms";
    //播放
    private static final String NETEASE_NATIVE_BTN_PLAY = "note_btn_play_ms";
    //下一首
    private static final String NETEASE_NATIVE_BTN_NEXT = "note_btn_next";
    //歌词 关
    private static final String NETEASE_NATIVE_BTN_LYC_OFF = "note_btn_lyc_mc";
    //歌词 开
    private static final String NETEASE_NATIVE_BTN_LYC_ON = "note_btn_lyced_ms";


    private NeteaseMusic() {
        mContext = CoreManager.getDefault().getContext();
        try {
            mResources = mContext.getPackageManager().getResourcesForApplication(MusicManager.MUSIC_NETEASE);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static final Singleton<NeteaseMusic> gDefault = new Singleton<NeteaseMusic>() {
        @Override
        protected NeteaseMusic create() {
            return new NeteaseMusic();
        }
    };

    public static NeteaseMusic getDefault() {
        return gDefault.get();
    }

    public MusicInfo netease(Notification notification, String packageName) {
        mMusicInfo.setPackageName(packageName);

//        APP1通过包名获取到对应APP2的Context之后
//        杀掉APP2，APP1也跟着被杀掉。
//        ActivityManager: Killing 25124:com.journeyOS.edge/u0a206 (adj 100): stop com.netease.cloudmusic
//        Context context = AppUtils.getPackageContext(mContext, packageName);
//        mResources = context.getResources();

        if (notification.extras != null &&
                notification.extras.containsKey(NotificationCompat.EXTRA_MEDIA_SESSION)) {

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
            for (int i = 0; i < actionCount; i++) {
                final NotificationCompat.Action action = NotificationCompat.getAction(notification, i);
                if (mResources != null) {
                    try {
                        String iconId = mResources.getResourceEntryName(action.getIcon());
                        switch (iconId) {
                            case NETEASE_NATIVE_BTN_PAUSE:
                                MusicAction pause = new MusicAction() {
                                    @Override
                                    public void run() throws Exception {
                                        action.actionIntent.send();
                                    }
                                };
                                mMusicInfo.setClick(pause);
                                mMusicInfo.setPlaying(true);
                                break;
                            case NETEASE_NATIVE_BTN_PLAY:
                                MusicAction play = new MusicAction() {
                                    @Override
                                    public void run() throws Exception {
                                        action.actionIntent.send();
                                    }
                                };
                                mMusicInfo.setClick(play);
                                mMusicInfo.setPlaying(false);
                                break;
                            case NETEASE_NATIVE_BTN_NEXT:
                                MusicAction next = new MusicAction() {
                                    @Override
                                    public void run() throws Exception {
                                        action.actionIntent.send();
                                    }
                                };
                                mMusicInfo.setNext(next);
                                break;
                            case NETEASE_NATIVE_BTN_PRE:
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

        } else if (notification.tickerText != null
                && notification.tickerText.toString().equals("网易云音乐正在播放")) {
            try {
                //获得ViewRoot
                mNotificationRoot = (ViewGroup) notification.bigContentView.apply(mContext, new FrameLayout(mContext));
                if (mNotificationRoot == null) {
                    return null;
                }
                //得到布局文件
                RelativeLayout relativeLayout = (RelativeLayout) mNotificationRoot.getChildAt(0);
                if (relativeLayout == null) {
                    return null;
                }

                //遍历布局文件 通过id找相应的view
                for (int i = 0; i < relativeLayout.getChildCount(); i++) {
                    View view = relativeLayout.getChildAt(i);
                    String viewId = mResources.getResourceEntryName(view.getId());
                    if (BuildConfig.DEBUG) LogUtils.d(TAG, "view id = " + viewId);
                    switch (viewId) {
                        case NETEASE_TEXT:
                            LinearLayout linearLayout = (LinearLayout) view;
                            for (int j = 0; j < linearLayout.getChildCount(); j++) {
                                TextView tv = (TextView) linearLayout.getChildAt(j);
                                String textId = mResources.getResourceEntryName(tv.getId());
                                LogUtils.d(TAG, "text id = " + textId);
                                if (NETEASE_TEXT_NAME.equals(textId)) {
                                    String text = tv.getText().toString();
                                    mMusicInfo.setName(text);
                                }
                                if (NETEASE_TEXT_SING.equals(textId)) {
                                    String text = tv.getText().toString();
                                    if (text != null) {
                                        String[] texts = text.split(" - ");
                                        if (texts.length == 2) {
                                            mMusicInfo.setSinger(texts[0]);
                                        }
                                    }
                                }
                            }
                            break;
                        case NETEASE_ALBUM:
                            Drawable drawable = ((ImageView) view).getDrawable();
                            mMusicInfo.setAlbumCover(drawable);
                            break;
                        case NETEASE_BTN://按钮布局
                            //遍历按钮
                            LinearLayout linearLayoutBtn = (LinearLayout) view;
                            for (int j = 0; j < linearLayoutBtn.getChildCount(); j++) {
                                final ImageView imageView = (ImageView) linearLayoutBtn.getChildAt(j);
                                String buttonId = mResources.getResourceEntryName(imageView.getId());
                                if (BuildConfig.DEBUG) LogUtils.d(TAG, "button id = " + buttonId);
                                switch (buttonId) {
                                    case NETEASE_BTN_START:
                                        break;
                                    case NETEASE_BTN_PRE:
                                        MusicAction last = new MusicAction() {
                                            @Override
                                            public void run() throws Exception {
                                                imageView.performClick();
                                            }
                                        };
                                        mMusicInfo.setLast(last);
                                        break;
                                    case NETEASE_BTN_TOGGLE:
                                        //这里通过拿到View的图片资源Bitmap 判断中间像素点颜色来判断是否播放
                                        Bitmap mp = UIUtils.drawableToBitmap(imageView.getDrawable());
                                        int color = mp.getPixel(mp.getWidth() / 2, mp.getHeight() / 2);
                                        MusicAction play = new MusicAction() {
                                            @Override
                                            public void run() throws Exception {
                                                imageView.performClick();
                                            }
                                        };
                                        mMusicInfo.setClick(play);
                                        mMusicInfo.setPlaying(color == 0);
                                        break;
                                    case NETEASE_BTN_NEXT:
                                        MusicAction next = new MusicAction() {
                                            @Override
                                            public void run() throws Exception {
                                                imageView.performClick();
                                            }
                                        };
                                        mMusicInfo.setNext(next);
                                        break;
                                    case NETEASE_BTN_LYRIC:
                                        break;
                                }
                            }
                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mMusicInfo;
    }

}
