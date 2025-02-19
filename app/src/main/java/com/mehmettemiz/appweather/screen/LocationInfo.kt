package com.mehmettemiz.appweather.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.mehmettemiz.appweather.viewmodel.WeatherViewModel
import kotlin.math.roundToInt

@Composable
fun LocationInfo(
    weatherList: List<WeatherModel>,
    forecastTodayList: List<ForecastList>,
    forecastWeatherList: Map<String, List<ForecastList>>, // futureForecast used here
    viewModel: WeatherViewModel,
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
                TodayInfo(weatherList = weatherList)
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

                            TodayForecast(forecast) // Use the forecast data

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
                            ForecastItem(forecast) // Pass forecast data to ForecastItem
                        }
                    }
                }
            }
        }
    }
}



@Composable
fun TodayInfo(weatherList : List<WeatherModel>) {
    LazyColumn(modifier = Modifier
        .padding(4.dp)
        .width(500.dp)
        .height(400.dp)
    ) {
        items(weatherList) {weather ->
            WeatherDetailes(weather = weather)
        }
    }
}

@Composable
fun WeatherDetailes(weather: WeatherModel) {
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
fun TodayForecast(forecast: ForecastList) {
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
fun ForecastItem(forecast: ForecastList) {
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
