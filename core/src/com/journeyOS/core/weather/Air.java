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

package com.journeyOS.core.weather;

import java.util.List;


/**
 * 和风天气接口
 * https://www.heweather.com/documents/api/s6/air-now
 */
public class Air {

    public List<HeWeather6Bean> HeWeather6;

    public static class HeWeather6Bean {
        /**
         * air_now_city : {"aqi":"19","co":"0","main":"","no2":"34","o3":"31","pm10":"18","pm25":"8","pub_time":"2017-11-07 22:00","qlty":"优","so2":"2"}
         * air_now_station : [{"air_sta":"万寿西宫","aqi":"19","asid":"CNA1001","co":"0.4","lat":"39.8673","lon":"116.366","main":"-","no2":"37","o3":"29","pm10":"13","pm25":"5","pub_time":"2017-11-07 22:00","qlty":"优","so2":"3"},{"air_sta":"定陵","aqi":"21","asid":"CNA1002","co":"0.4","lat":"40.2865","lon":"116.17","main":"-","no2":"7","o3":"66","pm10":"6","pm25":"1","pub_time":"2017-11-07 22:00","qlty":"优","so2":"3"},{"air_sta":"东四","aqi":"16","asid":"CNA1003","co":"0.4","lat":"39.9522","lon":"116.434","main":"-","no2":"28","o3":"36","pm10":"16","pm25":"10","pub_time":"2017-11-07 22:00","qlty":"优","so2":"2"},{"air_sta":"天坛","aqi":"22","asid":"CNA1004","co":"0.4","lat":"39.8745","lon":"116.434","main":"-","no2":"38","o3":"29","pm10":"22","pm25":"8","pub_time":"2017-11-07 22:00","qlty":"优","so2":"1"},{"air_sta":"农展馆","aqi":"28","asid":"CNA1005","co":"0.6","lat":"39.9716","lon":"116.473","main":"-","no2":"56","o3":"15","pm10":"20","pm25":"12","pub_time":"2017-11-07 22:00","qlty":"优","so2":"2"},{"air_sta":"官园","aqi":"25","asid":"CNA1006","co":"0.4","lat":"39.9425","lon":"116.361","main":"-","no2":"50","o3":"13","pm10":"15","pm25":"10","pub_time":"2017-11-07 22:00","qlty":"优","so2":"1"},{"air_sta":"海淀区万柳","aqi":"31","asid":"CNA1007","co":"0.4","lat":"39.9934","lon":"116.315","main":"-","no2":"61","o3":"6","pm10":"28","pm25":"14","pub_time":"2017-11-07 22:00","qlty":"优","so2":"3"},{"air_sta":"顺义新城","aqi":"16","asid":"CNA1008","co":"0.4","lat":"40.1438","lon":"116.72","main":"-","no2":"12","o3":"51","pm10":"16","pm25":"6","pub_time":"2017-11-07 22:00","qlty":"优","so2":"4"},{"air_sta":"怀柔镇","aqi":"15","asid":"CNA1009","co":"0.3","lat":"40.3937","lon":"116.644","main":"-","no2":"21","o3":"48","pm10":"12","pm25":"5","pub_time":"2017-11-07 22:00","qlty":"优","so2":"3"},{"air_sta":"昌平镇","aqi":"15","asid":"CNA1010","co":"0.5","lat":"40.1952","lon":"116.23","main":"-","no2":"20","o3":"48","pm10":"7","pm25":"3","pub_time":"2017-11-07 22:00","qlty":"优","so2":"2"},{"air_sta":"奥体中心","aqi":"24","asid":"CNA1011","co":"0.4","lat":"40.0031","lon":"116.407","main":"-","no2":"48","o3":"21","pm10":"15","pm25":"9","pub_time":"2017-11-07 22:00","qlty":"优","so2":"3"},{"air_sta":"古城","aqi":"32","asid":"CNA1012","co":"0.4","lat":"39.9279","lon":"116.225","main":"-","no2":"36","o3":"20","pm10":"32","pm25":"3","pub_time":"2017-11-07 22:00","qlty":"优","so2":"1"}]
         * basic : {"cid":"CN101010100","location":"北京","parent_city":"北京","admin_area":"北京","cnty":"中国","lat":"39.90498734","lon":"116.40528870","tz":"+8.0"}
         * status : ok
         * update : {"loc":"2017-11-07 22:46","utc":"2017-11-07 14:46"}
         */

        public AirNowCityBean air_now_city;
        public BasicBean basic;
        public String status;
        public UpdateBean update;
        public List<AirNowStationBean> air_now_station;

        public static class AirNowCityBean {
            /**
             * aqi : 19
             * co : 0
             * main :
             * no2 : 34
             * o3 : 31
             * pm10 : 18
             * pm25 : 8
             * pub_time : 2017-11-07 22:00
             * qlty : 优
             * so2 : 2
             */

            public String aqi;
            public String co;
            public String main;
            public String no2;
            public String o3;
            public String pm10;
            public String pm25;
            public String pub_time;
            public String qlty;
            public String so2;
        }

        public static class BasicBean {
            /**
             * cid : CN101010100
             * location : 北京
             * parent_city : 北京
             * admin_area : 北京
             * cnty : 中国
             * lat : 39.90498734
             * lon : 116.40528870
             * tz : +8.0
             */

            public String cid;
            public String location;
            public String parent_city;
            public String admin_area;
            public String cnty;
            public String lat;
            public String lon;
            public String tz;
        }

        public static class UpdateBean {
            /**
             * loc : 2017-11-07 22:46
             * utc : 2017-11-07 14:46
             */

            public String loc;
            public String utc;
        }

        public static class AirNowStationBean {
            /**
             * air_sta : 万寿西宫
             * aqi : 19
             * asid : CNA1001
             * co : 0.4
             * lat : 39.8673
             * lon : 116.366
             * main : -
             * no2 : 37
             * o3 : 29
             * pm10 : 13
             * pm25 : 5
             * pub_time : 2017-11-07 22:00
             * qlty : 优
             * so2 : 3
             */

            public String air_sta;
            public String aqi;
            public String asid;
            public String co;
            public String lat;
            public String lon;
            public String main;
            public String no2;
            public String o3;
            public String pm10;
            public String pm25;
            public String pub_time;
            public String qlty;
            public String so2;
        }
    }
}
