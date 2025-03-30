package com.example.apptrabalho2_metereologia.ui.feature

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.IconButton
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.apptrabalho2_metereologia.data.model.HourlyForecast
import com.example.apptrabalho2_metereologia.data.model.WeatherInfo
import com.example.apptrabalho2_metereologia.ui.theme.AppTrabalho2MetereologiaTheme
import com.example.apptrabalho2_metereologia.ui.theme.BlueSky
import com.example.apptrabalho2_metereologia.data.locations
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp

@Composable
fun WeatherRoute(
    viewModel: WeatherViewodel = viewModel()
) {
    val weatherInfo by viewModel.weatherInfoState.collectAsStateWithLifecycle()
    WeatherScreen(weatherInfo = weatherInfo.weatherInfo)
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("DiscouragedApi")
@Composable
fun WeatherScreen(
    viewModel: WeatherViewodel = viewModel(),
    context: Context = LocalContext.current,
    weatherInfo: WeatherInfo?,
){
    var selectedLocation by remember { mutableStateOf(locations[0]) }
    var expanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val filteredLocations = locations.filter { location ->
        location == Pair(null, null) ||
                location.first.contains(searchQuery, ignoreCase = true)
    }
    val focusManager = LocalFocusManager.current
    val cityHistory by viewModel.cityHistory.collectAsStateWithLifecycle()

    weatherInfo?.let {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = if (weatherInfo.isDay) {
                BlueSky
            } else Color.DarkGray
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(32.dp))

                    BoxWithConstraints(
                        modifier = Modifier.fillMaxWidth(),
                    )  {
                        TextField(
                            modifier = Modifier
                                .width(maxWidth),
                            value = searchQuery,
                            label = { Text("Search Location", color = Color.White) },
                            trailingIcon = {
                                IconButton(onClick = {
                                    expanded = !expanded
                                    focusManager.clearFocus()
                                }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Search, // Search icon from Material Icons
                                        contentDescription = "Search Icon",
                                        tint = Color.White
                                    )
                                }
                            },
                            textStyle = TextStyle(color = Color.White),
                            colors = ExposedDropdownMenuDefaults.textFieldColors(
                                focusedContainerColor = if (weatherInfo.isDay) {
                                    BlueSky
                                } else Color.DarkGray,
                                unfocusedContainerColor = if (weatherInfo.isDay) {
                                    BlueSky
                                } else Color.DarkGray,
                                focusedTrailingIconColor = Color.White,
                                unfocusedTrailingIconColor = Color.White
                            ),
                            onValueChange = {
                                searchQuery = it
                            }
                        )

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier
                                .background(
                                    if (weatherInfo.isDay) {
                                        BlueSky
                                    } else Color.DarkGray
                                )
                                .heightIn(max = 400.dp)
                                .width(maxWidth)
                        ) {
                            filteredLocations.forEach { (city, coordinates) ->
                                DropdownMenuItem(
                                    text = { Text(city, color = Color.White) },
                                    onClick = {
                                        selectedLocation = city to coordinates
                                        expanded = false
                                        viewModel.updateLocation(city, coordinates)
                                        searchQuery = city
                                    },
                                    modifier = Modifier.background(
                                        if (weatherInfo.isDay) {
                                            BlueSky
                                        } else Color.DarkGray
                                    )
                                )
                            }
                            if (filteredLocations.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("No results", color = Color.White) },
                                    enabled = false,
                                    onClick = {},
                                )
                            }
                        }

                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Search History",
                        color = Color.White,
                    )
                    Row {
                        Spacer(modifier = Modifier.height(8.dp))
                        cityHistory.forEach { city ->
                            Button(
                                onClick = {
                                    searchQuery = city.cityName
                                    viewModel.updateLocation(city.cityName, city.latitude to city.longitude)
                                },
                                modifier = Modifier
                                    .padding(4.dp)
                                    .widthIn(max = 125.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0x40000000)
                                ),
                            ) {
                                Text(
                                    text = city.cityName,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = weatherInfo.locationName,
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Text(
                        text = weatherInfo.dayOfWeek.lowercase(),
                        color = Color.White.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Informações principais do tempo
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val iconDrawableResId: Int = context.resources.getIdentifier(
                            "weather_${weatherInfo.conditionIcon}",
                            "drawable",
                            context.packageName
                        )

                        Image(
                            painter = painterResource(id = iconDrawableResId),
                            contentDescription = null,
                            modifier = Modifier.size(140.dp)
                        )

                        Text(
                            text = weatherInfo.condition,
                            color = Color.White,
                            style = MaterialTheme.typography.titleLarge,
                            textAlign = TextAlign.Center
                        )

                        Text(
                            text = "${weatherInfo.temperature}°",
                            color = Color.White,
                            style = MaterialTheme.typography.displayLarge,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Previsão horária
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x40000000))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Forecast for the next hours",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(24.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(weatherInfo.hourlyForecasts) { forecast ->
                                HourlyForecastItem(forecast = forecast, context = context)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun HourlyForecastItem(
    forecast: HourlyForecast,
    context: Context
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.width(56.dp)
    ) {

        Text(
            text = forecast.time,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center
        )

        val iconDrawableResId: Int = context.resources.getIdentifier(
            "weather_${forecast.conditionIcon}",
            "drawable",
            context.packageName
        )

        Image(
            painter = painterResource(id = iconDrawableResId),
            contentDescription = null,
            modifier = Modifier.size(32.dp)
        )

        Text(
            text = "${forecast.temperature}°",
            color = Color.White,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WeatherScreenPreview() {
    AppTrabalho2MetereologiaTheme {
        WeatherScreen(
            weatherInfo = WeatherInfo(
                locationName = "Teste",
                conditionIcon = "01d",
                condition = "Clear",
                temperature = 25,
                dayOfWeek = "Monday",
                isDay = true,
                hourlyForecasts = List(6) { index ->
                    HourlyForecast(
                        time = "${index + 1}:00",
                        temperature = 25 + index,
                        conditionIcon = "01d",
                        condition = "Clear"
                    )
                }
            )
        )
    }
}
