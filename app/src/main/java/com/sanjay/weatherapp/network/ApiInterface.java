package com.sanjay.weatherapp.network;


import com.sanjay.weatherapp.model.WeatherResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiInterface {

    @GET("data/2.5/weather")
    Call<WeatherResponse> getWeatherBasedOnGps(@Query("lat") String lat,
                                               @Query("lon") String lon,
                                               @Query("appid") String apiKey);
}
