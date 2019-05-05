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

package com.journeyOS.edge;

import android.content.Context;
import android.os.Message;
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.base.utils.Singleton;
import com.journeyOS.base.utils.TimeUtils;
import com.journeyOS.core.CoreManager;

import cn.bmob.v3.BmobInstallation;
import cn.bmob.v3.BmobInstallationManager;

public class AdManager {
    private static final String TAG = AdManager.class.getSimpleName();
    private Context mContext;

    final H mHandler = H.getDefault().getHandler();

    private static final int SHOW_BANNER_DAYS_DIFF = 1;
    private static final int SHOW_INTERSTITIAL_DAYS_DIFF = 5;

    private static final int REPEAT_MAX = 50;
    private int mBannerCount = 0;
    private int mInterstitialCount = 0;

    private AdManager() {
        mContext = CoreManager.getDefault().getContext();
    }

    private static final Singleton<AdManager> gDefault = new Singleton<AdManager>() {
        @Override
        protected AdManager create() {
            return new AdManager();
        }
    };

    public static AdManager getDefault() {
        return gDefault.get();
    }

    public void loadAndListener(final AdView adView) {
        BmobInstallation bmobInstallation = BmobInstallationManager.getInstance().getCurrentInstallation();
        LogUtils.d(TAG, "current installation = [" + bmobInstallation + "]");
        if (bmobInstallation != null) {
            String createdTime = bmobInstallation.getCreatedAt();
            LogUtils.d(TAG, "time = [" + createdTime + "]");
            long daysDiff = TimeUtils.getDaysDiff(createdTime);
            if (daysDiff < SHOW_BANNER_DAYS_DIFF) {
                LogUtils.e(TAG, "new device don't need show banner ad");
                return;
            }
        } else {
            if (mHandler.hasMessages(H.MSG_AD_BANNER)) {
                mHandler.removeMessages(H.MSG_AD_BANNER);
            }
            if (mBannerCount <= REPEAT_MAX) {
                Message message = Message.obtain();
                message.what = H.MSG_AD_BANNER;
                message.obj = adView;
                mBannerCount++;
                mHandler.sendMessageDelayed(message, H.AD_DELAY_TIME);
            }
            return;
        }

        AdRequest adRequest = new AdRequest.Builder()
                .build();

//        adView.setAdSize(AdSize.SMART_BANNER);
//        adView.setAdUnitId(mContext.getResources().getString(R.string.banner_ad_unit_id));

        // Start loading the ad in the background.
        adView.loadAd(adRequest);

        adView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                adView.setVisibility(View.VISIBLE);
                LogUtils.d(TAG, "ad finishes loading");
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                LogUtils.d(TAG, "ad request fails, errorCode = [" + errorCode + "]");
//                adView.setVisibility(View.GONE);
            }

            @Override
            public void onAdOpened() {
                LogUtils.d(TAG, "ad opens an overlay that covers the screen.");
            }

            @Override
            public void onAdLeftApplication() {
                LogUtils.d(TAG, "when the user has left the app");
            }

            @Override
            public void onAdClosed() {
                LogUtils.d(TAG, "user is about to return to the app after tapping on an ad.");
            }
        });
    }

    public void loadInterstitialAd() {
        BmobInstallation bmobInstallation = BmobInstallationManager.getInstance().getCurrentInstallation();
        LogUtils.d(TAG, "current installation = [" + bmobInstallation + "]");
        if (bmobInstallation != null) {
            String createdTime = bmobInstallation.getCreatedAt();
            LogUtils.d(TAG, "time = [" + createdTime + "]");
            long daysDiff = TimeUtils.getDaysDiff(createdTime);
            if (daysDiff < SHOW_INTERSTITIAL_DAYS_DIFF) {
                LogUtils.e(TAG, "new device don't need show interstitial ad");
                return;
            }
        } else {
            if (mHandler.hasMessages(H.MSG_AD_INTERSTITIAL)) {
                mHandler.removeMessages(H.MSG_AD_INTERSTITIAL);
            }
            if (mInterstitialCount <= REPEAT_MAX) {
                mInterstitialCount++;
                mHandler.sendEmptyMessageDelayed(H.MSG_AD_INTERSTITIAL, H.AD_DELAY_TIME);
            }
            return;
        }

        final InterstitialAd interstitialAd = new InterstitialAd(mContext);
        interstitialAd.setAdUnitId(mContext.getString(R.string.interstitial_ad_unit_id));
        AdRequest adRequest = new AdRequest.Builder().build();
        interstitialAd.loadAd(adRequest);

        LogUtils.d(TAG, "wanna show interstitial, is loading = [" + interstitialAd.isLoading() + "], is loaded = [" + interstitialAd.isLoaded() + "]");
        if (!interstitialAd.isLoading() && !interstitialAd.isLoaded()) {
            interstitialAd.loadAd(adRequest);
        }

        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                LogUtils.d(TAG, "interstitial ad finishes loading");
                interstitialAd.show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                LogUtils.d(TAG, "interstitial ad request fails, errorCode = [" + errorCode + "]");
            }

            @Override
            public void onAdClosed() {
            }
        });

    }
}
