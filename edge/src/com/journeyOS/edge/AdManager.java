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
import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.journeyOS.base.utils.LogUtils;
import com.journeyOS.base.utils.Singleton;
import com.journeyOS.core.CoreManager;

public class AdManager {
    private static final String TAG = AdManager.class.getSimpleName();
    private Context mContext;

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
        AdRequest adRequest = new AdRequest.Builder()
                .build();

//        adView.setAdSize(AdSize.SMART_BANNER);
//        adView.setAdUnitId(mContext.getResources().getString(R.string.banner_ad_unit_id2));

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
