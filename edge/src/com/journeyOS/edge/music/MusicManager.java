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
import android.service.notification.StatusBarNotification;

import com.journeyOS.base.BuildConfig;
import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.base.utils.Singleton;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.GlobalType;

public class MusicManager {
    private static final String TAG = MusicManager.class.getSimpleName();

    public static final String MUSIC_LAST = GlobalType.MUSIC_LAST;
    public static final String MUSIC_PLAY = GlobalType.MUSIC_PLAY;
    public static final String MUSIC_NEXT = GlobalType.MUSIC_NEXT;

    public static final String MUSIC_NETEASE = "com.netease.cloudmusic";
    public static final String MUSIC_QQ = "com.tencent.qqmusic";
    public static final String MUSIC_XIAMI = "fm.xiami.main";

    Context mContext;
    MusicInfo mMusicInfo;
    int mStatusBarId;

    private MusicManager() {
        mContext = CoreManager.getDefault().getContext();
    }

    private static final Singleton<MusicManager> gDefault = new Singleton<MusicManager>() {
        @Override
        protected MusicManager create() {
            return new MusicManager();
        }
    };

    public static MusicManager getDefault() {
        return gDefault.get();
    }

    public void onNotification(StatusBarNotification sbn) {
        mMusicInfo = null;
        if (BuildConfig.DEBUG) LogUtils.d(TAG, "wanna get music play info...");
        Notification notification = sbn.getNotification();
        if (notification == null) {
            LogUtils.w(TAG, "notification was null");
            return;
        }

        mStatusBarId = sbn.getId();
        String packageName = sbn.getPackageName();

        if (MUSIC_NETEASE.equals(packageName)) {
            mMusicInfo = NeteaseMusic.getDefault().netease(notification, packageName);
        } else if (MUSIC_QQ.equals(packageName)) {
            mMusicInfo = QQMusic.getDefault().qq(notification, packageName);
        }else if (MUSIC_XIAMI.equals(packageName)) {
            mMusicInfo = XiamiMusic.getDefault().xiami(notification, packageName);
        }
    }

    public void onNotificationRemoved(StatusBarNotification sbn) {
        if (mStatusBarId == sbn.getId()) {
            mMusicInfo = null;
        }
    }

    public boolean isPlay() {
        boolean isPlay = false;
        if (mMusicInfo != null && mMusicInfo.isPlaying()) {
            isPlay = true;
        }

        return isPlay;
    }

    public void play() {
        boolean isPlay = isPlay();
        if (!isPlay) {
            LogUtils.d(TAG, "music wasn't play...");
        }
        try {
            mMusicInfo.getClick().run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void last() {
        boolean isPlay = isPlay();
        if (!isPlay) {
            LogUtils.d(TAG, "music wasn't play...");
        }
        try {
            mMusicInfo.getLast().run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void next() {
        boolean isPlay = isPlay();
        if (!isPlay) {
            LogUtils.d(TAG, "music wasn't play...");
        }
        try {
            mMusicInfo.getNext().run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}