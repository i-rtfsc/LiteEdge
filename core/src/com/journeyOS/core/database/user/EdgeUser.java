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

package com.journeyOS.core.database.user;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobGeoPoint;

public class EdgeUser extends BmobUser {

    /**
     * 昵称
     */
    public String nickname;

    /**
     * 年龄
     */
    public Integer age;

    /**
     * 性别
     */
    public Integer gender;

    /**
     * 用户当前位置
     */
    public BmobGeoPoint address;


    /**
     * 头像
     */
    @Deprecated
    public BmobFile avatar;

    /**
     * 头像网址
     */
    public String icon;

    public String backUp;
}
