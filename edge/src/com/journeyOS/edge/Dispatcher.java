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

package com.journeyOS.edge;

import android.content.Context;
import android.content.res.Configuration;

import com.journeyOS.base.Constant;
import com.journeyOS.base.persistence.SpUtils;
import com.journeyOS.base.utils.AppUtils;
import com.journeyOS.base.utils.Singleton;
import com.journeyOS.core.CoreManager;
import com.journeyOS.core.StateMachine;
import com.journeyOS.core.api.edge.IEdge;
import com.journeyOS.core.type.BarrageState;
import com.journeyOS.core.type.Direction;
import com.journeyOS.core.type.EdgeDirection;
import com.journeyOS.edge.wm.BarrageManager;

public class Dispatcher {
    private Context mContext;

    private Dispatcher() {
        mContext = CoreManager.getDefault().getContext();
    }

    private static final Singleton<Dispatcher> gDefault = new Singleton<Dispatcher>() {
        @Override
        protected Dispatcher create() {
            return new Dispatcher();
        }
    };

    public static Dispatcher getDefault() {
        return gDefault.get();
    }

    public void handleGestureDirection(Direction direction) {
        boolean isPortrait = mContext.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
        switch (direction) {
            case LEFT:
                if (isPortrait) {
                    CoreManager.getDefault().getImpl(IEdge.class).showingEdge(EdgeDirection.RIGHT);
                } else {

                }

                break;

            case LONG_LEFT:
                if (isPortrait) {
                    CoreManager.getDefault().getImpl(IEdge.class).showingEdge(EdgeDirection.RIGHT);
                } else {

                }

                break;

            case RIGHT:
                if (isPortrait) {
                    CoreManager.getDefault().getImpl(IEdge.class).showingEdge(EdgeDirection.LEFT);
                } else {

                }
                break;

            case LONG_RIGHT:
                if (isPortrait) {
                    CoreManager.getDefault().getImpl(IEdge.class).showingEdge(EdgeDirection.LEFT);
                } else {

                }
                break;

            case DOWN:
                if (isPortrait) {

                } else {
                    CoreManager.getDefault().getImpl(IEdge.class).showingEdge(EdgeDirection.UP);
                }
                break;

            case LONG_DOWN:
                if (isPortrait) {

                } else {
                    CoreManager.getDefault().getImpl(IEdge.class).showingEdge(EdgeDirection.UP);
                }
                break;

            case UP:
                break;

            case LONG_UP:
                break;

            case CLICK:
                if (SpUtils.getInstant().getInt(Constant.BARRAGE_CLICK, Constant.BARRAGE_CLICK_DEFAULT) == 1) {
                    if (BarrageState.SHOW == StateMachine.getBarrageState()) {
                        AppUtils.startApp(mContext, BarrageManager.getDefault().getPackageName());
                    }
                }
                break;

            case LONG_PRESS:
                break;
        }
    }
}
