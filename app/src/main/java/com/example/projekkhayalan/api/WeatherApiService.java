package com.example.projekkhayalan.api;

import com.example.projekkhayalan.models.WeatherResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApiService {
    @GET("publik/prakiraan-cuaca")
    Call<WeatherResponse> getWeatherForecast(@Query("adm4") String areaCode);
}