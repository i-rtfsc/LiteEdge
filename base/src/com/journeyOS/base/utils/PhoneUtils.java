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

package com.journeyOS.base.utils;

public class PhoneUtils {
//    /*  2019年1月已知
//     * 中国电信号段 133,149,153,173,174,177,180,181,189,199
//     * 中国联通号段 130,131,132,145,146,155,156,166,175,176,185,186
//     * 中国移动号段 134(0-8),135,136,137,138,139,147,148,150,151,152,157,158,159,178,182,183,184,187,188,198
//     * 上网卡专属号段（用于上网和收发短信，不打打电话）,如中国联通的是145
//        虚拟运营商
//        电信：1700,1701,1702
//        移动：1703,1705,1706
//        联通：1704,1707,1708,1709,171
//     * 卫星通信： 1349
//     * 未知号段：141、142、143、144、154
//     */

    private static final String PHONE_NUMBER_REG = "^(13[0-9]|14[579]|15[0-3,5-9]|16[6]|17[0135678]|18[0-9]|19[89])\\d{8}$";

    public static boolean isMobile(String str) {
        if (!BaseUtils.isNull(str)) {
            return str.matches(PHONE_NUMBER_REG);
        }
        return false;
    }


}
