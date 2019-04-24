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

package com.journeyOS.core.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.journeyOS.base.utils.JsonHelper;
import com.journeyOS.base.utils.LogUtils;

import cn.bmob.push.PushConstants;

public class PushMessageReceiver extends BroadcastReceiver {
    private static final String TAG = PushMessageReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.d(TAG, "action = " + intent.getAction());
        if (intent.getAction().equals(PushConstants.ACTION_MESSAGE)) {
            String pushMsg = intent.getStringExtra("msg");
            LogUtils.d(TAG, "客户端收到推送内容 = " + pushMsg);
            PushManager.getDefault().handleMessage(pushMsg);
        }
    }

}
