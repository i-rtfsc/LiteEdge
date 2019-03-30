/*
 * Copyright (c) 2018 anqi.huang@outlook.com
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

package com.journeyOS.edge.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;

import com.journeyOS.edge.R;

public class NotificationUtils {

    public static final int NOTIFICATION_ID = 19573;

    public static Notification getNotification(Context context) {
        String text = context.getString(R.string.app_name);

        String CHANNEL_ONE_ID = context.getPackageName();
        String CHANNEL_ONE_NAME = text;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                    CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.createNotificationChannel(notificationChannel);

            Notification notification = new Notification.Builder(context).setChannelId(CHANNEL_ONE_ID)
                    .setSmallIcon(R.drawable.svg_core_ball)
                    .setTicker(text)
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle(text)
                    .setContentText(text)
                    .build();
            return notification;
        } else {
            Notification notification = new Notification.Builder(context)
                    .setSmallIcon(R.drawable.svg_core_ball)
                    .setTicker(text)
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle(text)
                    .setContentText(text)
                    .build();
            return notification;
        }
    }
}
