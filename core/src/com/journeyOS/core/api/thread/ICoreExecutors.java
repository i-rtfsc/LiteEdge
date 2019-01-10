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

import com.journeyOS.core.api.ICoreApi;

import java.util.concurrent.Executor;

import static com.journeyOS.core.thread.CoreExecutorsImpl.OnMessageListener;

public interface ICoreExecutors extends ICoreApi {
    public static final String HANDLER_QS = "qs_handler";
    public static final String HANDLER_EDGE = "edge_handler";

    /**
     * 文件操作线程
     *
     * @return 线程
     */
    Executor diskIOThread();

    /**
     * 网络线程
     *
     * @return 线程
     */
    Executor networkIOThread();

    /**
     * 主线程
     *
     * @return 线程
     */
    Executor mainThread();

    /**
     * 主handler
     *
     * @return handler
     */
    Handler handler();

    /**
     * 后台handler
     *
     * @param handlerName handler名字
     * @return handler
     */
    Handler getHandle(String handlerName);

    /**
     * 监听handler消息
     *
     * @param handler  handler
     * @param listener 监听
     */
    void setOnMessageListener(Handler handler, OnMessageListener listener);
}

