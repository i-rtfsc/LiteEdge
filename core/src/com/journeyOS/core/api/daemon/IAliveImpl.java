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

package com.journeyOS.core.api.daemon;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;

import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.core.daemon.AliveActivity;
import com.journeyOS.literouter.annotation.ARouterInject;

@ARouterInject(api = IAliveApi.class)
public class IAliveImpl implements IAliveApi {
    private static final String TAG = IAliveImpl.class.getCanonicalName();
    private static final String KEEP_ALIVE = "com.journeyOS.core.daemon.keep_alive";

    @Override
    public void onCreate() {

    }

    @Override
    public void keepAlive(Context context) {
        try {
            LogUtils.d(TAG, "start daemon activity!");
            Intent intent = new Intent();
            intent.setAction(KEEP_ALIVE);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            LogUtils.d(TAG, e);
        }
    }

    @Override
    public void destroy() {
        AliveActivity.destroy();
    }

}
