package com.sanjay.weatherapp.model;

import com.google.gson.annotations.SerializedName;

public class WeatherResponse {

    @SerializedName("name")
    private String cityName;

    @SerializedName("main")
    private Main main;

    @SerializedName("weather")
    private Weather[] weather;

    public String getCityName() {
        return cityName;
    }

    public Main getMain() {
        return main;
    }

    public Weather[] getWeather() {
        return weather;
    }

    public static String updateWeatherIcon(int condition) {
        if (condition >= 0 && condition <= 300) {
            return "storm";
        } else if (condition >= 301 && condition <= 600) {
            return "raining";
        } else if (condition >= 601 && condition <= 700) {
            return "snow1";
        } else if (condition >= 701 && condition <= 771) {
            return "fog";
        } else if (condition >= 772 && condition <= 800) {
            return "overcast";
        } else if (condition == 800) {
            return "sunny";
        } else if (condition >= 900 && condition <= 902) {
            return "storm1";
        } else if (condition == 903) {
            return "snow";
        } else if (condition == 904) {
            return "sunny";
        } else if (condition >= 905 && condition <= 1000) {
            return "storm1";
        }

        return "no_image";
    }
}
