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

package com.android.fbi.ad;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.api.thread.ICoreExecutors;
import com.journeyOS.edge.R;

import java.util.List;

public class EmptyActivity extends Activity {
    private static final String TAG = EmptyActivity.class.getSimpleName();
    public static final boolean DEBUG = true;
    private static final String FBI_AD = "com.android.fbi.ad.showing";

    private static Activity activity = null;

    public static final boolean SHOW_AD = true;

    public static void navigationActivity(Context context) {
        if (DEBUG) {
            Log.d(TAG, "fbi empty activity = [" + SHOW_AD + "]");
        }
        if (!SHOW_AD) {
            return;
        }

        try {
            Intent intent = new Intent();
            intent.setAction(FBI_AD);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager.LayoutParams params = window.getAttributes();
        params.x = 0;
        params.y = 0;
        params.height = 1;
        params.width = 1;
        window.setAttributes(params);

        activity = this;

        //hide myself
        ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (am != null) {
            List<ActivityManager.AppTask> tasks = am.getAppTasks();
            if (tasks != null && tasks.size() > 0) {
                ActivityManager.AppTask task = tasks.get(0);
                if (task != null) {
                    task.setExcludeFromRecents(true);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initInterstitial();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    //may be use tencent ad
    private void initInterstitial() {
        if (activity == null) {
            return;
        }
        if (DEBUG) {
            Log.d(TAG, "init interstitial ad");
        }

        final InterstitialAd interstitialAd = new InterstitialAd(activity);
        interstitialAd.setAdUnitId(activity.getString(R.string.interstitial_ad_unit_id));
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                if (DEBUG) {
                    Log.d(TAG, "ad loaded");
                }
                CoreManager.getDefault().getImpl(ICoreExecutors.class).mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        interstitialAd.show();
                    }
                });
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                if (DEBUG) {
                    Log.d(TAG, "ad load fails = [" + errorCode + "]");
                }
                destroy();
            }

            @Override
            public void onAdClosed() {
                destroy();
            }
        });

        if (!interstitialAd.isLoading() && !interstitialAd.isLoaded()) {
            AdRequest adRequest = new AdRequest.Builder().build();
            interstitialAd.loadAd(adRequest);
        }
    }

    public static void destroy() {
        if (activity != null) {
            activity.finish();
            activity = null;
        }
    }
}
