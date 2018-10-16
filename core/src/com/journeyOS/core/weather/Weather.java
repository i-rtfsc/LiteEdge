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
 * https://www.heweather.com/documents/api/s6/weather
 */
public class Weather {
    public List<HeWeather6Bean> HeWeather6;

    public static class HeWeather6Bean {
        public BasicBean basic;
        public UpdateBean update;
        public String status;
        public NowBean now;
        public List<DailyForecastBean> daily_forecast;
        public List<HourlyBean> hourly;
        public List<LifestyleBean> lifestyle;

        public static class BasicBean {
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
            public String loc;
            public String utc;
        }

        public static class NowBean {
            public String cloud;
            public String cond_code;
            public String cond_txt;
            public String fl;
            public String hum;
            public String pcpn;
            public String pres;
            public String tmp;
            public String vis;
            public String wind_deg;
            public String wind_dir;
            public String wind_sc;
            public String wind_spd;
        }

        public static class DailyForecastBean {
            public String cond_code_d;
            public String cond_code_n;
            public String cond_txt_d;
            public String cond_txt_n;
            public String date;
            public String hum;
            public String mr;
            public String ms;
            public String pcpn;
            public String pop;
            public String pres;
            public String sr;
            public String ss;
            public String tmp_max;
            public String tmp_min;
            public String uv_index;
            public String vis;
            public String wind_deg;
            public String wind_dir;
            public String wind_sc;
            public String wind_spd;
        }

        public static class HourlyBean {
            public String cloud;
            public String cond_code;
            public String cond_txt;
            public String dew;
            public String hum;
            public String pop;
            public String pres;
            public String time;
            public String tmp;
            public String wind_deg;
            public String wind_dir;
            public String wind_sc;
            public String wind_spd;
        }

        public static class LifestyleBean {
            public String brf;
            public String txt;
            public String type;
        }
    }
}
