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

package com.journeyOS.base.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.journeyOS.base.utils.Singleton;

import java.util.ArrayList;
import java.util.List;

public class GlobalActionObserver {

    private static final List<GlobalActionListener> mListeners = new ArrayList<>();

    private GlobalActionReceiver mReceiver;
    private boolean isRegister = false;

    private GlobalActionObserver() {
        mReceiver = new GlobalActionReceiver();
    }

    private static final Singleton<GlobalActionObserver> gDefault = new Singleton<GlobalActionObserver>() {
        @Override
        protected GlobalActionObserver create() {
            return new GlobalActionObserver();
        }
    };

    public static GlobalActionObserver getDefault() {
        return gDefault.get();
    }

    private class GlobalActionReceiver extends BroadcastReceiver {
        private String action = null;
        final String SYSTEM_DIALOG_REASON_KEY = "reason";
        final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
        final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

        @Override
        public void onReceive(Context context, Intent intent) {
            action = intent.getAction();
            if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(action)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (reason != null) {
                    if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                        for (GlobalActionListener listener : mListeners) {
                            listener.onPressHomeKey();
                        }
                    } else if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                        for (GlobalActionListener listener : mListeners) {
                            listener.onPressRecentapps();
                        }
                    }
                }
            }
        }
    }

    public void startGlobalActionReceiver(Context context) {
        if (!isRegister) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
            context.registerReceiver(mReceiver, filter);
            isRegister = true;
        }
    }

    public void unregisterReceiver(Context context) {
        if (isRegister) {
            context.unregisterReceiver(mReceiver);
            isRegister = false;
        }
    }

    public void setOnGlobalActionListener(GlobalActionListener listener) {
        mListeners.add(listener);
    }

    public interface GlobalActionListener {
        void onPressHomeKey();

        void onPressRecentapps();
    }

}
