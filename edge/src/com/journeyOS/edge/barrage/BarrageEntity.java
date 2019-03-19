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

package com.journeyOS.edge.barrage;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class BarrageEntity implements Parcelable {

    public static final int BARRAGE_TYPE_SYSTEM = 0;// 系统弹幕消息
    public static final int BARRAGE_TYPE_USERCHAT = 1;// 用户聊天弹幕消息

    public String packageName;
    public Bitmap avatar;
    public String name;
    public String userId;
    public int level;
    public int role;
    public int type;// 0是系统公屏，1是用户弹幕信息

    public String text;
    public ArrayList<RichMessage> richText; // 富文本

    public BarrageEntity() {
    }

    protected BarrageEntity(Parcel in) {
        packageName = in.readString();
        avatar = in.readParcelable(Bitmap.class.getClassLoader());
        name = in.readString();
        userId = in.readString();
        level = in.readInt();
        role = in.readInt();
        type = in.readInt();
        text = in.readString();
        richText = in.createTypedArrayList(RichMessage.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(packageName);
        dest.writeParcelable(avatar, flags);
        dest.writeString(name);
        dest.writeString(userId);
        dest.writeInt(level);
        dest.writeInt(role);
        dest.writeInt(type);
        dest.writeString(text);
        dest.writeTypedList(richText);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<BarrageEntity> CREATOR = new Creator<BarrageEntity>() {
        @Override
        public BarrageEntity createFromParcel(Parcel in) {
            return new BarrageEntity(in);
        }

        @Override
        public BarrageEntity[] newArray(int size) {
            return new BarrageEntity[size];
        }
    };
}
