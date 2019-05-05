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

package com.journeyOS.base.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class TimeUtils {
    private static final String TAG = TimeUtils.class.getSimpleName();

    public static final long DAY_OF_YEAR = 365;
    public static final long DAY_OF_MONTH = 30;
    public static final long HOUR_OF_DAY = 24;
    public static final long MIN_OF_HOUR = 60;
    public static final long SEC_OF_MIN = 60;
    public static final long MILLIS_OF_SEC = 1000;

    private static SimpleDateFormat MONTH_DAY_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private static SimpleDateFormat HOUR_MINUTE = new SimpleDateFormat("HH:mm");
    private static SimpleDateFormat HOUR = new SimpleDateFormat("HH");
    private static SimpleDateFormat WEEK = new SimpleDateFormat("EEEE");

    private static SimpleDateFormat DATE2_FORMAT = new SimpleDateFormat("yyyyMMdd");

    public static Date getDate(String time) {
        try {
            return DATE_FORMAT.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static long getDaysDiff(String time) {
        LogUtils.d(TAG, "time = [" + time + "]");
        if (time == null) {
            return -1;
        }

        Date createdDate = getDate(time);
        long msDiff = new Date().getTime() - createdDate.getTime();
        long daysDiff = TimeUnit.MILLISECONDS.toDays(msDiff);
        LogUtils.d(TAG, "get days diff = [" + daysDiff + "]");
        return daysDiff;
    }
}
