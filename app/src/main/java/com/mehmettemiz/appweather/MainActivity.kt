package com.mehmettemiz.appweather

import android.content.Context
import android.graphics.pdf.models.ListItem
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresExtension
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.BottomAppBar
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.libraries.places.api.Places
import com.mehmettemiz.appweather.factory.SearchViewModelFactory
import com.mehmettemiz.appweather.model.Forecast.ForecastList
import com.mehmettemiz.appweather.model.WeatherModel
import com.mehmettemiz.appweather.screen.LocationInfo
import com.mehmettemiz.appweather.screen.SearchScreen
import com.mehmettemiz.appweather.ui.theme.AppWeatherTheme
import com.mehmettemiz.appweather.viewmodel.SearchViewModel
import com.mehmettemiz.appweather.viewmodel.WeatherViewModel

class MainActivity : ComponentActivity() {
    private val viewModel : WeatherViewModel by viewModels<WeatherViewModel>()
    private lateinit var searchViewModel: SearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "AIzaSyD8EWmmzViIz9m6oMO3IHGzUbDhcBQFQ_0")
        }

        searchViewModel = ViewModelProvider(this, SearchViewModelFactory(applicationContext)) [SearchViewModel::class.java]

        setContent {
            viewModel.getLocation(this, this)
            val weatherList by viewModel.weatherList.observeAsState(emptyList())
            val forecastTodayList by viewModel.forecastTodayList.observeAsState(emptyList())
            val forecastWeatherList by viewModel.forecastWeatherList.observeAsState(emptyMap())
            val customBackground by viewModel.backgroundColor.observeAsState(emptyList())
            val searchWeatherList by searchViewModel.searchWeatherList.observeAsState(emptyList())
            val searchForecastTodayList by searchViewModel.searchForecastTodayList.observeAsState(emptyList())
            val searchForecastWeatherList by searchViewModel.searchForecastWeatherList.observeAsState(emptyMap())
            val searchCustomBackground by viewModel.backgroundColor.observeAsState(emptyList())


            val navController = rememberNavController()

            AppWeatherTheme {
                Scaffold(
                    modifier = Modifier
                        .fillMaxSize(),
                    bottomBar = {
                        BottomAppBar(
                            actions = {
                                Row(
                                    modifier = Modifier.fillMaxSize(),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(onClick = { navController.navigate("LocationInfo") }) {
                                        Icon(imageVector = Icons.Default.LocationOn, contentDescription = "example")
                                    }
                                    IconButton(onClick = { navController.navigate("Search") }) {
                                        Icon(imageVector = Icons.Default.Search, contentDescription = "example")
                                    }
                                }
                            },
                            containerColor = Color.Black,
                            contentColor = Color.White
                        )
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) { // Box'ı da tam boy yapıyoruz
                        NavHost(navController = navController, startDestination = "SplashScreen") {
                            composable("SplashScreen") {
                                SplashScreen(navController, weatherList)
                            }
                            composable("LocationInfo") {
                                if (weatherList.isNotEmpty()) {
                                    LocationInfo(
                                        weatherList = weatherList,
                                        forecastTodayList = forecastTodayList,
                                        forecastWeatherList = forecastWeatherList,
                                        viewModel = viewModel,
                                        customBackground = customBackground
                                    )
                                } else {
                                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                                }
                            }
                            composable("Search") {
                                // SearchScreen burada!
                                SearchScreen(
                                    viewModel = searchViewModel,
                                    weatherList = searchWeatherList,
                                    forecastTodayList = searchForecastTodayList,
                                    forecastWeatherList = searchForecastWeatherList,
                                    customBackground = searchCustomBackground
                                    )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SplashScreen(
    navController: NavController,
    weatherList: List<WeatherModel>
) {
    LaunchedEffect(weatherList) {
        if (weatherList.isNotEmpty()) {
            navController.navigate("LocationInfo") {
                popUpTo("SplashScreen") { inclusive = true } // Splash ekranını geri yığından kaldır
            }
        }
    }

    // Splash ekranı içeriği
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Image(painter = painterResource(id = R.drawable.logo), contentDescription = "App logo")
    }
}







@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppWeatherTheme {
    }
}