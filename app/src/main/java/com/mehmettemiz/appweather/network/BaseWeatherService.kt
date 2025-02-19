package com.mehmettemiz.appweather.network

import com.mehmettemiz.appweather.service.WeatherService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object BaseWeatherService {

    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"

    private val retrofit :Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    fun <T> createService(serviceClass: Class<T>): T {
        return retrofit.create(serviceClass)

    }
}