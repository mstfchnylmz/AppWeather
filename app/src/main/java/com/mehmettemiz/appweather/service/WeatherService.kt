package com.mehmettemiz.appweather.service

import com.mehmettemiz.appweather.model.Forecast.ForecastModel
import com.mehmettemiz.appweather.model.WeatherModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    //1d7868d4d7283f07fdd1824e5f3878a9

    @GET("weather?appid=ed0ce0871815789bb4e7258b559032e2")
    suspend fun getWeatherByLocation(
        @Query("lat") latitude : Double,
        @Query("lon") longitude : Double,
        @Query("units") units : String = "metric"
    ) : Response<WeatherModel>

    @GET("forecast?appid=ed0ce0871815789bb4e7258b559032e2")
    suspend fun getForecastByLocation(
        @Query("lat") latitude : Double,
        @Query("lon") longitude : Double,
        @Query("units") units : String = "metric"
    ) : Response<ForecastModel>
}