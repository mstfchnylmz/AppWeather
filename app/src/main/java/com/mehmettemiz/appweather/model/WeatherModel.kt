package com.mehmettemiz.appweather.model

data class WeatherModel(
    var dt: Int,
    var timezone: Int,
    var id: Int,
    var name: String,
    var sys: Sys,
    var main: Main,
    var weather: List<Weather>,
    var wind : Wind,
    var clouds : Clouds
)

data class Sys(
    var country: String,
    var sunrise: Int,
    var sunset: Int
)

data class Main(
    var temp: Double,
    var feels_like : Double,
    var humidity : Int,
    var temp_min : Double,
    var temp_max : Double,
    var pressure : Int
)

data class Weather(
    var main: String,
    var description: String,
    var icon: String
)

data class Wind(
    var speed : Double
)

data class Clouds(
    val all : Int
)

