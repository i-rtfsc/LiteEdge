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

package com.journeyOS.core.thread;

import com.journeyOS.core.api.thread.ICoreExecutors;
import com.journeyOS.literouter.annotation.ARouterInject;

import java.util.concurrent.Executor;


@ARouterInject(api = ICoreExecutors.class)
public class CoreExecutorsImpl implements ICoreExecutors {
    private static final String TAG = CoreExecutorsImpl.class.getSimpleName();
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

}
