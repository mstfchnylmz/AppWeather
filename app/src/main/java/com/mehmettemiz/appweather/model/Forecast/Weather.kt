package com.mehmettemiz.appweather.model.Forecast

data class Weather(
    val description: String,
    val icon: String,
    val id: Int,
    val main: String
) {
    fun getIconUrl(): String {
        return "https://openweathermap.org/img/wn/$icon@2x.png"
    }
}