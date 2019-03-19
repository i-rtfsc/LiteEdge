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

package com.journeyOS.barrage.model.painter;

import com.journeyOS.barrage.model.BarrageModel;
import com.journeyOS.barrage.model.channel.BarrageChannel;

public class R2LPainter extends BarragePainter {

    @Override
    protected void layout(BarrageModel model, BarrageChannel channel) {
        if (model.getX() - model.getSpeed() <= -model.getWidth()) {
            model.setAlive(false);
            return;
        }
        model.setStartPositionX(model.getX() - model.getSpeed());
    }
}

