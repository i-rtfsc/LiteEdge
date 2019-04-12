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

package com.journeyOS.core;

import android.content.Context;

import com.journeyOS.base.utils.Singleton;
import com.journeyOS.core.api.thread.ICoreExecutors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class SyncMarket {

    private Context mContext;
    private String mPackageName;

    private static final int TIME_OUT = 5000;

    public static final String MARKET = "com.coolapk.market";

    private SyncMarket() {
        mContext = CoreManager.getDefault().getContext();
        mPackageName = mContext.getPackageName();
    }

    private static final Singleton<SyncMarket> gDefault = new Singleton<SyncMarket>() {
        @Override
        protected SyncMarket create() {
            return new SyncMarket();
        }
    };

    public static SyncMarket getDefault() {
        return gDefault.get();
    }

    public String getMarketUrl() {
        return "https://www.coolapk.com/apk/" + mPackageName;
    }

    public void get(final onVersionObservable listener) {
        CoreManager.getDefault().getImpl(ICoreExecutors.class).networkIOThread().execute(new Runnable() {
            @Override
            public void run() {
                String currentVersion = Version.getVersionName(mContext);
                String version = Version.getVersionName(mContext);
                String description = "";
                try {
                    Document doc = Jsoup.connect(getMarketUrl())
                            .timeout(TIME_OUT)
                            .post();
                    if (doc != null) {
                        Elements versionElements = doc.getElementsByClass("list_app_info");
                        if (versionElements != null && versionElements.get(0) != null) {
                            version = versionElements.get(0).text();
                        }

                        Elements descriptionElements = doc.getElementsByClass("apk_left_title_info");
                        if (descriptionElements != null && descriptionElements.get(0) != null) {
                            description = descriptionElements.get(0).text();
                        }
                        boolean needUpdate = !currentVersion.equals(version);
                        listener.onResult(needUpdate, version, description);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

    }

    public interface onVersionObservable {
        void onResult(boolean needUpdate, String version, String description);
    }
}
