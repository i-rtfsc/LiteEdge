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

package com.journeyOS.base.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.journeyOS.base.utils.Singleton;

import java.util.ArrayList;
import java.util.List;

public class ScreenObserver {

    private static final List<ScreenStateListener> mListeners = new ArrayList<>();

    private ScreenBroadcastReceiver mScreenReceiver;
    private boolean isRegister = false;

    private ScreenObserver() {
        mScreenReceiver = new ScreenBroadcastReceiver();
    }

    private static final Singleton<ScreenObserver> gDefault = new Singleton<ScreenObserver>() {
        @Override
        protected ScreenObserver create() {
            return new ScreenObserver();
        }
    };

    public static ScreenObserver getDefault() {
        return gDefault.get();
    }

    private class ScreenBroadcastReceiver extends BroadcastReceiver {
        private String action = null;

        @Override
        public void onReceive(Context context, Intent intent) {
            action = intent.getAction();
            if (Intent.ACTION_SCREEN_ON.equals(action)) {
                for (ScreenStateListener listener : mListeners) {
                    listener.onScreenChanged(true);
                }
            } else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
                for (ScreenStateListener listener : mListeners) {
                    listener.onScreenChanged(false);
                }
            }
        }
    }

    public void startScreenBroadcastReceiver(Context context) {
        if (!isRegister) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            context.registerReceiver(mScreenReceiver, filter);
            isRegister = true;
        }
    }

    public void stopScreenStateUpdate(Context context) {
        if (isRegister) {
            context.unregisterReceiver(mScreenReceiver);
            isRegister = false;
        }
    }

    public void setOnScreenStateListener(ScreenStateListener listener) {
        mListeners.add(listener);
    }

    public interface ScreenStateListener {
        void onScreenChanged(boolean isScreenOn);
    }

}
