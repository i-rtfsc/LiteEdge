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


package com.journeyOS.core.api.edgeprovider;

import com.journeyOS.core.api.ICoreApi;
import com.journeyOS.core.database.user.User;

public interface IUserProvider extends ICoreApi {

    User getCurrentAccount();

    /**
     * 获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    User getConfig(String userId);

    /**
     * 更新用户信息
     *
     * @param user 用户信息
     */
    void insertOrUpdateUser(User user);

    /**
     * 删除用户信息
     *
     * @param user 用户信息
     */
    void deleteUser(User user);

    /**
     * 删除全部悬浮球的配置
     */
    void deleteAll();
}
