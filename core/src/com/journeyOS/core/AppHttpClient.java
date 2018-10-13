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

package com.journeyOS.core;

import com.journeyOS.base.utils.BaseUtils;
import com.journeyOS.base.utils.FileIOUtils;
import com.journeyOS.base.utils.NetWork;
import com.journeyOS.base.utils.Singleton;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AppHttpClient {
    private static final String BASE_URL = "https://free-api.heweather.com/s6/";
    private static final int HTTP_RESPONSE_DISK_CACHE_MAX_SIZE = 10 * 1024 * 1024;
    private static final int MAX_AGE = 60 * 10;
    private static final int MAX_STALE = 60 * 60 * 24;
    private Map<String, Object> serviceByType = new HashMap<>();
    private Retrofit mRetrofit;

    private AppHttpClient() {
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(cacheInterceptor()).cache(cache()).build();
        mRetrofit = new Retrofit.Builder().baseUrl(BASE_URL).client(client).addConverterFactory(GsonConverterFactory.create()).build();
    }

    private static final Singleton<AppHttpClient> gDefault = new Singleton<AppHttpClient>() {
        @Override
        protected AppHttpClient create() {
            return new AppHttpClient();
        }
    };

    public static AppHttpClient getDefault() {
        return gDefault.get();
    }

    public synchronized <T> T getService(Class<T> apiInterface) {
        String serviceName = apiInterface.getName();
        if (BaseUtils.isNull(serviceByType.get(serviceName))) {
            T service = mRetrofit.create(apiInterface);
            serviceByType.put(serviceName, service);
            return service;
        } else {
            return (T) serviceByType.get(serviceName);
        }
    }

    private Cache cache() {
        final File cacheDir = FileIOUtils.buildFile("HttpResponseCache");
        return (new Cache(cacheDir, HTTP_RESPONSE_DISK_CACHE_MAX_SIZE));

    }

    private Interceptor cacheInterceptor() {
        return new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {

                Request request = chain.request();

                if (!NetWork.isAvailable(CoreManager.getDefault().getContext())) {
                    request = request.newBuilder().cacheControl(CacheControl.FORCE_CACHE).build();
                }
                Response originalResponse = chain.proceed(request);

                if (NetWork.isAvailable(CoreManager.getDefault().getContext())) {
                    return originalResponse.newBuilder().header("Cache-Control", "public ,max-age=" + MAX_AGE).build();
                } else {
                    return originalResponse.newBuilder().header("Cache-Control", "public, only-if-cached, max-stale=" + MAX_STALE).build();
                }
            }
        };
    }

}
