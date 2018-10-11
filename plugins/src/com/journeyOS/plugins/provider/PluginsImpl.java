/*
 * Copyright (c) 2018 anqi.huang@outlook.com.
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

package com.journeyOS.plugins.provider;

import android.content.Context;

import com.journeyOS.core.api.plugins.IPluginsApi;
import com.journeyOS.core.type.EdgeDirection;
import com.journeyOS.literouter.annotation.ARouterInject;
import com.journeyOS.plugins.SelectorActivity;

@ARouterInject(api = IPluginsApi.class)
public class PluginsImpl implements IPluginsApi {

    @Override
    public void onCreate() {

    }


    @Override
    public void navigationSelectorActivity(Context context, int postion, EdgeDirection direction) {
//        SelectorActivity.navigationActivity(context, postion, direction);
        SelectorActivity.navigationFromApplication(context, postion, direction);
    }
}
