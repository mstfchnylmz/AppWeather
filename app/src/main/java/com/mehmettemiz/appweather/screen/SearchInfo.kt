package com.mehmettemiz.appweather.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.rememberAsyncImagePainter
import com.mehmettemiz.appweather.R
import com.mehmettemiz.appweather.model.Forecast.ForecastList
import com.mehmettemiz.appweather.model.WeatherModel
import com.mehmettemiz.appweather.ui.theme.bgColor
import com.mehmettemiz.appweather.ui.theme.oswaldFont
import com.mehmettemiz.appweather.ui.theme.poppinsFont
import com.mehmettemiz.appweather.viewmodel.SearchViewModel
import com.mehmettemiz.appweather.viewmodel.WeatherViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    weatherList: List<WeatherModel> = emptyList(),
    forecastTodayList: List<ForecastList> = emptyList(),
    forecastWeatherList: Map<String, List<ForecastList>> = emptyMap(),
    customBackground: List<Color>
) {
    var isSearchActive by remember { mutableStateOf(false) }
    var selectedCityWeather by remember { mutableStateOf<WeatherModel?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        SearchBar(
            query = viewModel.inputText,
            onQueryChange = { viewModel.onInputChange(it) },
            onSearch = { isSearchActive = false },
            active = isSearchActive,
            onActiveChange = { isSearchActive = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search a city...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Icon") }
        ) {
                viewModel.cityList.forEach { (cityName, placeId) ->
                    androidx.compose.material3.ListItem(
                        headlineContent = { Text(cityName) },
                        modifier = Modifier.clickable {
                            viewModel.fetchPlaceDetails(placeId)
                            isSearchActive = false

                            // Şehri seçtikten sonra bilgileri alıp, göster
                            selectedCityWeather = viewModel.searchWeatherList.value?.firstOrNull()
                            println("Seçilen şehir: $cityName, Place ID: $placeId")

                        }
                    )
                }


        }

        // Şehir seçildiyse, detaylı hava durumu bilgisi göster
        selectedCityWeather?.let {
            SearchInfo(
                weatherList = listOf(it),
                forecastTodayList = forecastTodayList,
                forecastWeatherList = forecastWeatherList,
                viewModel = viewModel,
                customBackground = customBackground
                )
        }
    }
}

@Composable
fun SearchInfo(
    weatherList: List<WeatherModel>,
    forecastTodayList: List<ForecastList>,
    forecastWeatherList: Map<String, List<ForecastList>>, // futureForecast used here
    viewModel: SearchViewModel,
    customBackground: List<Color>
) {
    Box(modifier = Modifier
        .fillMaxSize()
        .background(bgColor)
    ) {
        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)) {

            // Today's weather details
            item {
                SearchTodayInfo(weatherList = weatherList)
            }

            // Today's forecast
            item {
                Column(
                    modifier = Modifier
                        .padding(1.dp, bottom = 20.dp)
                        .border(1.dp, Color.Transparent, shape = ShapeDefaults.Medium)
                        .padding(5.dp)
                ) {
                    Text(
                        text = "Today",
                        modifier = Modifier.padding(start = 10.dp, bottom = 5.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    LazyRow(modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = customBackground
                            ),
                            shape = MaterialTheme.shapes.medium,
                            alpha = 0.9F
                        )
                    ) {
                        items(forecastTodayList) { forecast ->

                            SearchTodayForecast(forecast) // Use the forecast data

                        }
                    }
                }
            }

            item {
                Row(horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                ) {
                    Image(painter = painterResource(id = R.drawable.date_range_foreground), contentDescription = "Date", modifier = Modifier.size(50.dp))
                    Text(text = "5-Day Forecast",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }


            // Future forecasts by day
            items(forecastWeatherList.entries.toList()) { (day, forecasts) ->
                val dayName = viewModel.getDayNameByDate(day)
                Column(
                    modifier = Modifier
                        .padding(1.dp)
                        .border(1.dp, Color.Transparent, shape = ShapeDefaults.Medium)
                        .padding(5.dp)
                ) {
                    // Day title
                    Text(
                        text = dayName,
                        modifier = Modifier.padding(start = 10.dp, bottom = 5.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                    // List of forecast items for this day
                    LazyRow(modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = customBackground
                            ),
                            shape = MaterialTheme.shapes.medium,
                            alpha = 0.9F
                        )
                    ) {
                        items(forecasts) { forecast ->
                            SearchForecastItem(forecast) // Pass forecast data to ForecastItem
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun SearchTodayInfo(weatherList : List<WeatherModel>) {
    LazyColumn(modifier = Modifier
        .padding(4.dp)
        .width(500.dp)
        .height(400.dp)
    ) {
        items(weatherList) {weather ->
            SearchWeatherDetailes(weather = weather)
        }
    }
}

@Composable
fun SearchWeatherDetailes(weather: WeatherModel) {
    Column(modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = weather.name,
            fontFamily = oswaldFont,
            fontWeight = FontWeight.W100,
            fontSize = 40.sp,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Text(
            text = weather.main.temp.roundToInt().toString() + "°",
            modifier = Modifier
                .padding(top = 10.dp)
                .graphicsLayer(
                    scaleY = 1.2f
                ),
            fontFamily = poppinsFont,
            fontWeight = FontWeight.W100,
            fontSize = 100.sp,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Text(
            text = weather.weather.firstOrNull()?.description ?: "",
            fontFamily = poppinsFont,
            fontWeight = FontWeight.W800,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.onPrimary
        )
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Column(modifier = Modifier
                .fillMaxHeight()
                .padding(4.dp)
                ,verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(modifier = Modifier
                    .padding(2.dp)
                    .background(Color.Transparent)
                    .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row {
                        Image(
                            painter = painterResource(id = R.drawable.thermometer),
                            contentDescription = "thermometer",
                            modifier = Modifier.size(25.dp)
                        )
                        Text(
                            text = "Feels like",
                            modifier = Modifier.padding(start = 2.dp),
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    Text(
                        text = weather.main.feels_like.roundToInt().toString() + "°",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                }
                Column(modifier = Modifier
                    .padding(2.dp)
                    .background(Color.Transparent)
                    .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row {
                        Image(
                            painter = painterResource(id = R.drawable.pressure),
                            contentDescription = "pressure",
                            modifier = Modifier.size(25.dp)
                        )
                        Text(
                            text = "Pressure",
                            modifier = Modifier.padding(start = 2.dp),
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    Text(
                        text = weather.main.pressure.toString() + "hPa",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                }

            }
            Column(
                modifier = Modifier.fillMaxHeight()
                ,verticalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(modifier = Modifier
                    .padding(2.dp)
                    .background(Color.Transparent)
                    .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row {
                        Image(
                            painter = painterResource(id = R.drawable.windspeed),
                            contentDescription = "windspeed",
                            modifier = Modifier.size(25.dp)
                        )
                        Text(
                            text = "Wind speed",
                            modifier = Modifier.padding(start = 2.dp),
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    Text(
                        text = weather.wind.speed.toString() + "km/h",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimary
                    )

                }
                Column(modifier = Modifier
                    .padding(2.dp)
                    .background(Color.Transparent)
                    .padding(10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row {
                        Image(
                            painter = painterResource(id = R.drawable.humidity),
                            contentDescription = "humidity",
                            modifier = Modifier.size(25.dp)
                        )
                        Text(
                            text = "Humidity",
                            modifier = Modifier.padding(start = 2.dp),
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    Text(
                        text = weather.main.humidity.toString() + "%",
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }

}



@Composable
fun SearchTodayForecast(forecast: ForecastList) {
    val iconUrl = forecast.weather.firstOrNull()?.getIconUrl()
    val time = forecast.dt_txt.substring(11, 13)
    Column(
        modifier = Modifier
            .padding(4.dp)
            .background(Color.Transparent)
            .padding(start = 30.dp, bottom = 10.dp, top = 10.dp, end = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = time,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onTertiary
        )

        Image(
            painter = rememberAsyncImagePainter(model = iconUrl),
            contentDescription = "weather icon",
            modifier = Modifier
                .padding(3.dp)
                .size(50.dp)
        )
        Text(
            text = forecast.main.temp.roundToInt().toString() + "°",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onTertiary
        )
    }
}

@Composable
fun SearchForecastItem(forecast: ForecastList) {
    val iconUrl = forecast.weather.firstOrNull()?.getIconUrl()
    val time = forecast.dt_txt.substring(11, 13)

    Column(
        modifier = Modifier
            .padding(4.dp)
            .background(Color.Transparent)
            .padding(start = 30.dp, bottom = 10.dp, top = 10.dp, end = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        Text(
            text = time,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onTertiary
        )
        Image(
            painter = rememberAsyncImagePainter(model = iconUrl),
            contentDescription = "weather icon",
            modifier = Modifier
                .padding(3.dp)
                .size(50.dp)
        )
        Text(
            text = forecast.main.temp.roundToInt().toString() + "°",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onTertiary
        )
    }
}
