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
import android.os.Handler;
import android.os.Message;
import android.widget.LinearLayout;

import com.google.android.gms.ads.AdView;
import com.journeyOS.base.utils.Singleton;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.type.EdgeDirection;
import com.journeyOS.edge.wm.EdgeManager;

public class H extends Handler {
    private static final int BASE = 1;

    public static final long EDGE_DELAY_TIME = 25;
    public static final long DELAY_TIME = 5 * 1000l;
    public static final long AD_DELAY_TIME = 200;

    public static final int MSG_BALL_SHOWING = BASE << 0;
    public static final int MSG_BALL_HIDING = BASE << 1;
    public static final int MSG_EDGE_SHOWING = BASE << 2;
    public static final int MSG_EDGE_HIDING = BASE << 3;
    public static final int MSG_BARRAGE_START_SERVICE = BASE << 4;
    public static final int MSG_BARRAGE_NOTIFICATION = BASE << 5;
    public static final int MSG_SLIDE_CLICK = BASE << 6;
    public static final int MSG_DRAWER_RELEASE = BASE << 7;
    public static final int MSG_AD_BANNER = BASE << 8;
    public static final int MSG_AD_INTERSTITIAL = BASE << 9;

    private H mH;
    private Context mContext;

    private H() {
        mContext = CoreManager.getDefault().getContext();
    }

    private static final Singleton<H> gDefault = new Singleton<H>() {
        @Override
        protected H create() {
            return new H();
        }
    };

    public static H getDefault() {
        return gDefault.get();
    }

    public H getHandler() {
        if (mH == null) {
            mH = new H();
        }

        return mH;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
            case MSG_EDGE_SHOWING:
                EdgeDirection direction = (EdgeDirection) msg.obj;
                EdgeManager.getDefault().showEdge(direction);
                break;
            case MSG_EDGE_HIDING:
                boolean isAnimator = (boolean) msg.obj;
                if (isAnimator) {
                    EdgeManager.getDefault().hideEdge();
                } else {
                    EdgeManager.getDefault().removeEdge();
                }
                break;
            case MSG_BALL_SHOWING:
                EdgeServiceManager.getDefault().showingBall(true);
                break;
            case MSG_BALL_HIDING:
                EdgeServiceManager.getDefault().showingBall(false);
                break;
            case MSG_BARRAGE_START_SERVICE:
                NotificationManager.getDefault().startNotificationService();
                break;
            case MSG_BARRAGE_NOTIFICATION:
                NotificationManager.getDefault().handleNotification();
                break;
            case MSG_SLIDE_CLICK:
                SlidingDrawer.getDefault().onItemClick((int) msg.arg1);
                break;
            case MSG_DRAWER_RELEASE:
                SlidingDrawer.getDefault().releaseDrawer();
                break;
            case MSG_AD_BANNER:
                if (AdManager.OLD_INTERFACE) {
                    AdManager.getDefault().loadAdBanner((AdView) msg.obj);
                } else {
                    AdManager.getDefault().loadBannerAd((LinearLayout) msg.obj);
                }
                break;
            case MSG_AD_INTERSTITIAL:
                if (AdManager.OLD_INTERFACE) {
                    AdManager.getDefault().loadAdInterstitial();
                } else {
                    AdManager.getDefault().loadInterstitial();
                }
                break;
            default:
                break;
        }
    }


}
