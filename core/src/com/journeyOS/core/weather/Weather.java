package com.journeyOS.core.weather;

import com.google.gson.annotations.SerializedName;

public class Weather {
    @SerializedName("last_update")
    public String lastUpdate;

    @SerializedName("id")
    public String id;

    @SerializedName("name")
    public String name;

    @SerializedName("country")
    public String country;

    @SerializedName("path")
    public String address;

    @SerializedName("text_day")
    public String textDay;

    @SerializedName("code_day")
    public int codeDay;

    @SerializedName("text_night")
    public String textNight;

    @SerializedName("code_night")
    public int codeNight;

    @SerializedName("hight")
    public int high;

    @SerializedName("low")
    public int low;

    @SerializedName("precip")
    public String precip;

    @SerializedName("wind_direction")
    public String windDirection;

    @SerializedName("wind_direction_degree")
    public int windDirectionDegree;

    @SerializedName("wind_speed")
    public int windSpeed;

    @SerializedName("wind_scale")
    public int windScale;

    @Override
    public String toString() {
        return "Weather{" +
                "lastUpdate='" + lastUpdate + '\'' +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", country='" + country + '\'' +
                ", address='" + address + '\'' +
                ", textDay='" + textDay + '\'' +
                ", codeDay=" + codeDay +
                ", textNight='" + textNight + '\'' +
                ", codeNight=" + codeNight +
                ", high=" + high +
                ", low=" + low +
                ", precip='" + precip + '\'' +
                ", windDirection='" + windDirection + '\'' +
                ", windDirectionDegree=" + windDirectionDegree +
                ", windSpeed=" + windSpeed +
                ", windScale=" + windScale +
                '}';
    }
}
