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

package com.journeyOS.core.api.thread;

import android.os.Handler;
import android.os.Looper;

import com.journeyOS.literouter.annotation.ARouterInject;

import java.util.concurrent.Executor;


@ARouterInject(api = ICoreExecutorsApi.class)
public class CoreExecutorsImpl implements ICoreExecutorsApi {
    CoreExecutors mCoreExecutors;

    @Override
    public void onCreate() {
        mCoreExecutors = new CoreExecutors();
    }

    @Override
    public Executor diskIOThread() {
        return mCoreExecutors.diskIO();
    }

    @Override
    public Executor networkIOThread() {
        return mCoreExecutors.networkIO();
    }

    @Override
    public Executor mainThread() {
        return mCoreExecutors.mainThread();
    }

    @Override
    public Handler handler() {
        return new Handler(Looper.getMainLooper());
    }

}
