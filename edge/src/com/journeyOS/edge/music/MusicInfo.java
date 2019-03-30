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

import android.graphics.drawable.Drawable;

public class MusicInfo {
    private String mPackageName = "";
    private String mName = "";
    private String mSinger = "";
    private String mAlbum = "";
    private boolean isPlaying = false;
    private Drawable mAlbumCover = null;

    private MusicAction mNext = null;
    private MusicAction mLast = null;
    private MusicAction mClick = null;
    private MusicAction mPage = null;


    public String getPackageName() {
        return mPackageName;
    }

    public void setPackageName(String packageName) {
        this.mPackageName = packageName;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = mName;
    }

    public String getSinger() {
        return mSinger;
    }

    public void setSinger(String singer) {
        this.mSinger = singer;
    }

    public String getAlbum() {
        return mAlbum;
    }

    public void setAlbum(String album) {
        this.mAlbum = album;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public Drawable getAlbumCover() {
        return mAlbumCover;
    }

    public void setAlbumCover(Drawable album) {
        this.mAlbumCover = album;
    }

    public MusicAction getNext() {
        return mNext;
    }

    public void setNext(MusicAction next) {
        this.mNext = next;
    }

    public MusicAction getLast() {
        return mLast;
    }

    public void setLast(MusicAction last) {
        this.mLast = last;
    }

    public MusicAction getClick() {
        return mClick;
    }

    public void setClick(MusicAction click) {
        this.mClick = click;
    }

    public MusicAction getPage() {
        return mPage;
    }

    public void setPage(MusicAction page) {
        this.mPage = page;
    }
}
