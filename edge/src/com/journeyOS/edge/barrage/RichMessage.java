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

import android.os.Parcel;
import android.os.Parcelable;

public class RichMessage implements Parcelable {

    public String type;
    public String content;
    public String color;
    public String extend;
    public int giftId;

    protected RichMessage(Parcel in) {
        type = in.readString();
        content = in.readString();
        color = in.readString();
        extend = in.readString();
        giftId = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(content);
        dest.writeString(color);
        dest.writeString(extend);
        dest.writeInt(giftId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RichMessage> CREATOR = new Creator<RichMessage>() {
        @Override
        public RichMessage createFromParcel(Parcel in) {
            return new RichMessage(in);
        }

        @Override
        public RichMessage[] newArray(int size) {
            return new RichMessage[size];
        }
    };
}

