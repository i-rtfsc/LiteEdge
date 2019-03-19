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

package com.journeyOS.barrage.model.channel;

import com.journeyOS.barrage.model.BarrageModel;


public class BarrageChannel {

    public float speed = 3;
    public int width;
    public int height;
    public int topY;
    public int space = 60;

    public BarrageModel r2lReferenceView;
    public BarrageModel l2rReferenceView;

    public void dispatch(BarrageModel model) {
        if (model.isAttached()) {
            return;
        }

        model.setSpeed(speed);
        if (model.getDisplayType() == BarrageModel.RIGHT_TO_LEFT) {
            int mDeltaX = 0;
            if (r2lReferenceView != null) {
                mDeltaX = (int) (width - r2lReferenceView.getX() - r2lReferenceView.getWidth());
            }
            if (r2lReferenceView == null || !r2lReferenceView.isAlive() || mDeltaX > space) {
                model.setAttached(true);
                r2lReferenceView = model;
            }
        } else if (model.getDisplayType() == BarrageModel.LEFT_TO_RIGHT) {
            int mDeltaX = 0;
            if (l2rReferenceView != null) {
                mDeltaX = (int) l2rReferenceView.getX();
            }
            if (l2rReferenceView == null || !l2rReferenceView.isAlive() || mDeltaX > space) {
                model.setAttached(true);
                l2rReferenceView = model;
            }
        }
    }

}
