package com.mehmettemiz.appweather.viewmodel

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.CancellationToken
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.OnTokenCanceledListener
import com.mehmettemiz.appweather.R
import com.mehmettemiz.appweather.model.Forecast.ForecastList
import com.mehmettemiz.appweather.model.WeatherModel
import com.mehmettemiz.appweather.network.BaseWeatherService
import com.mehmettemiz.appweather.service.WeatherService
import com.mehmettemiz.appweather.ui.theme.clearColor1
import com.mehmettemiz.appweather.ui.theme.clearColor2
import com.mehmettemiz.appweather.ui.theme.cloudsColor1
import com.mehmettemiz.appweather.ui.theme.cloudsColor2
import com.mehmettemiz.appweather.ui.theme.drizzleColor1
import com.mehmettemiz.appweather.ui.theme.mistColor1
import com.mehmettemiz.appweather.ui.theme.mistColor2
import com.mehmettemiz.appweather.ui.theme.mistColor3
import com.mehmettemiz.appweather.ui.theme.rainColor1
import com.mehmettemiz.appweather.ui.theme.rainColor2
import com.mehmettemiz.appweather.ui.theme.snowColor1
import com.mehmettemiz.appweather.ui.theme.snowColor2
import com.mehmettemiz.appweather.ui.theme.snowColor3
import com.mehmettemiz.appweather.ui.theme.thunderstormColor1
import com.mehmettemiz.appweather.ui.theme.thunderstormColor2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Date
import java.util.Locale
class WeatherViewModel : ViewModel() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val _loading = MutableLiveData(true)
    val loading: LiveData<Boolean> = _loading
    private val _weatherList = MutableLiveData<List<WeatherModel>>()
    val weatherList: LiveData<List<WeatherModel>> = _weatherList
    private val _forecastTodayList = MutableLiveData<List<ForecastList>>()
    val forecastTodayList: LiveData<List<ForecastList>> = _forecastTodayList
    private val _forecastWeatherList = MutableLiveData<Map<String, List<ForecastList>>>()
    val forecastWeatherList: LiveData<Map<String, List<ForecastList>>> get() = _forecastWeatherList
    val backgroundColor = MutableLiveData<List<Color>>()
    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    private val retrofit: WeatherService = BaseWeatherService.createService(WeatherService::class.java)

    fun fetchWeather(lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            _loading.postValue(true)
            try {
                val response = retrofit.getWeatherByLocation(lat, lon, "metric")
                if (response.isSuccessful) {
                    response.body()?.let {
                        withContext(Dispatchers.Main) {
                            _weatherList.postValue(listOf(it))
                            if (it.weather[0].main == "Clear") {
                                backgroundColor.value = listOf(clearColor1, clearColor2)
                            } else if (it.weather[0].main == "Snow") {
                                backgroundColor.value = listOf(snowColor1, snowColor2, snowColor3)
                            } else if (it.weather[0].main == "Rain") {
                                backgroundColor.value = listOf(rainColor1, rainColor2)
                            } else if (it.weather[0].main == "Thunderstorm") {
                                backgroundColor.value = listOf(thunderstormColor1, thunderstormColor2)
                            } else if (it.weather[0].main == "Clouds") {
                                backgroundColor.value = listOf(cloudsColor1, cloudsColor2)
                            } else if (it.weather[0].main == "Drizzle") {
                                backgroundColor.value = listOf(drizzleColor1)
                            } else {
                                backgroundColor.value = listOf(mistColor1, mistColor2, mistColor3)
                            }
                        }
                    }
                } else {
                    Log.e("WeatherViewModel", "Response Error: ${response.code()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.postValue("Hava durumu alınırken bir hata oluştu.")
            } finally {
                _loading.postValue(false)
            }
        }
    }

    fun getDayNameByDate(dateString: String, format: String = "yyyy-MM-dd"): String {
        val formatter = DateTimeFormatter.ofPattern(format)
        val localDate = LocalDate.parse(dateString, formatter)

        return localDate.dayOfWeek.getDisplayName(TextStyle.FULL_STANDALONE, Locale.getDefault())
    }

    fun fetchForecast(lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            val dateFormatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val todayDate = dateFormatter.format(Date())

            try {
                val response = retrofit.getForecastByLocation(lat, lon)
                if (response.isSuccessful) {
                    response.body()?.let { forecastModel ->


                        val groupedForecast = forecastModel.list.groupBy { item ->
                            item.dt_txt.substring(0, 10)
                        }

                        val todayForecast = groupedForecast[todayDate] ?: emptyList()
                        val futureForecast = groupedForecast.filterKeys { it != todayDate }


                        withContext(Dispatchers.Main) {
                            _forecastTodayList.postValue(todayForecast)
                            _forecastWeatherList.postValue(futureForecast)
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.postValue("Tahmin verileri alınırken bir hata oluştu.")
            } finally {
                _loading.postValue(false)
            }
        }
    }

    fun getLocation(context: Context, activity: Activity) {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            _errorMessage.postValue("Konum izni gerekli. Lütfen izin verin.")
            ActivityCompat.requestPermissions(activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                101
            )
            return
        }

        fusedLocationClient.getCurrentLocation(
            LocationRequest.PRIORITY_HIGH_ACCURACY,
            object : CancellationToken() {
                override fun onCanceledRequested(p0: OnTokenCanceledListener) = CancellationTokenSource().token
                override fun isCancellationRequested() = false
            }
        ).addOnSuccessListener {
            if (it != null) {
                fetchWeather(it.latitude, it.longitude)
                fetchForecast(it.latitude, it.longitude)
                Log.d("WeatherViewModel", "Location: ${it.latitude}, ${it.longitude}")
            }
        }
    }
}
