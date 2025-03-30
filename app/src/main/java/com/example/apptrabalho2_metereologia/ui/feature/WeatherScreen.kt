// File: app/src/main/java/com/example/apptrabalho2_metereologia/ui/feature/WeatherScreen.kt
package com.example.apptrabalho2_metereologia.ui.feature

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.FilterDrama
import androidx.compose.material.icons.filled.Umbrella
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.apptrabalho2_metereologia.R
import com.example.apptrabalho2_metereologia.data.local.CityEntity
import com.example.apptrabalho2_metereologia.data.locations
import com.example.apptrabalho2_metereologia.data.model.HourlyForecast
import com.example.apptrabalho2_metereologia.data.model.WeatherInfo
import com.example.apptrabalho2_metereologia.ui.theme.AppTrabalho2MetereologiaTheme
import com.example.apptrabalho2_metereologia.ui.theme.BlueSky
import kotlinx.coroutines.delay

@Composable
fun WeatherRoute(
    viewModel: WeatherViewodel = viewModel()
) {
    val weatherInfo by viewModel.weatherInfoState.collectAsStateWithLifecycle()
    WeatherScreen(viewModel = viewModel, context = LocalContext.current, weatherInfo = weatherInfo.weatherInfo)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@SuppressLint("DiscouragedApi")
@Composable
fun WeatherScreen(
    viewModel: WeatherViewodel = viewModel(),
    context: Context = LocalContext.current,
    weatherInfo: WeatherInfo?,
){
    var expanded by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current
    var filteredLocations by remember {
        mutableStateOf<List<Pair<String, Pair<Float?, Float?>?>>>(emptyList())
    }
    var textFieldWidth by remember { mutableStateOf(0) }
    var textFieldHeightPx by remember { mutableStateOf(0) }
    val localDensity = LocalDensity.current
    val cityHistory by viewModel.cityHistory.collectAsStateWithLifecycle()
    val isDayTime = weatherInfo?.isDay ?: true
    val currentColorScheme = if (isDayTime) BlueSky else Color.DarkGray
    val scrollState = rememberScrollState()

    LaunchedEffect(searchQuery) {
        if (searchQuery.isBlank()) {
            filteredLocations = emptyList()
            expanded = false
            return@LaunchedEffect
        }
        delay(300L) // Debounce
        val results = locations.filter { location ->
            location.second != Pair(null, null) &&
                    location.first.contains(searchQuery, ignoreCase = true)
        }
        filteredLocations = results
        expanded = searchQuery.isNotEmpty() && results.isNotEmpty()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = currentColorScheme
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (weatherInfo != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(32.dp))

                    // --- Search Box and History ---
                    Box(modifier = Modifier.fillMaxWidth()) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { newValue -> searchQuery = newValue },
                            label = { Text("Busca de localização", color = Color.White.copy(alpha = 0.8f)) },
                            trailingIcon = {
                                IconButton(onClick = { focusManager.clearFocus() }) {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = "Search Icon",
                                        tint = Color.White
                                    )
                                }
                            },
                            colors = ExposedDropdownMenuDefaults.textFieldColors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = currentColorScheme,
                                unfocusedContainerColor = currentColorScheme,
                                cursorColor = Color.White,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTrailingIconColor = Color.White,
                                unfocusedTrailingIconColor = Color.White,
                                focusedLabelColor = Color.White.copy(alpha = 0.8f),
                                unfocusedLabelColor = Color.White.copy(alpha = 0.6f)
                            ),
                            singleLine = true,
                            textStyle = TextStyle(color = Color.White),
                            modifier = Modifier
                                .fillMaxWidth()
                                .onSizeChanged {
                                    textFieldWidth = it.width
                                    textFieldHeightPx = it.height
                                }
                        )

                        if (expanded) {
                            Popup(
                                alignment = Alignment.TopStart,
                                offset = IntOffset(x = 0, y = textFieldHeightPx),
                                onDismissRequest = { expanded = false },
                                properties = PopupProperties(focusable = false)
                            ) {
                                Card(
                                    modifier = Modifier
                                        .width(with(localDensity) { textFieldWidth.toDp() })
                                        .heightIn(max = 300.dp),
                                    shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                                    colors = CardDefaults.cardColors(containerColor = currentColorScheme)
                                ) {
                                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                        if (filteredLocations.isEmpty() && searchQuery.isNotEmpty()) {
                                            DropdownMenuItem(
                                                text = { Text("Sem resultados", color = Color.White.copy(alpha = 0.7f)) },
                                                enabled = false,
                                                onClick = {},
                                            )
                                        } else {
                                            filteredLocations.forEach { (city, coordinates) ->
                                                DropdownMenuItem(
                                                    text = { Text(city, color = Color.White) },
                                                    onClick = {
                                                        searchQuery = ""
                                                        expanded = false
                                                        coordinates?.let { nonNullCoords ->
                                                            viewModel.updateLocation(city, nonNullCoords)
                                                        }
                                                        focusManager.clearFocus()
                                                    },
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } // End Search Box

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Histórico de Busca",
                        color = Color.White,
                        style = MaterialTheme.typography.titleSmall
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        cityHistory.take(3).forEach { city ->
                            Button(
                                onClick = {
                                    searchQuery = ""
                                    viewModel.updateLocation(city.cityName, city.latitude to city.longitude)
                                    expanded = false
                                    focusManager.clearFocus()
                                },
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .widthIn(max = 120.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White.copy(alpha = 0.25f)
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
                    // --- End Search History ---

                    Spacer(modifier = Modifier.height(16.dp))

                    // --- Main Weather Info ---
                    Text(
                        text = weatherInfo.locationName,
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        text = weatherInfo.dayOfWeek,
                        color = Color.White.copy(alpha = 0.8f),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(32.dp))

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val iconDrawableResId: Int = try {
                            context.resources.getIdentifier(
                                "weather_${weatherInfo.conditionIcon}",
                                "drawable",
                                context.packageName
                            ).takeIf { it != 0 } ?: R.drawable.weather_01d
                        } catch (e: Exception) {
                            R.drawable.weather_01d
                        }
                        Image(
                            painter = painterResource(id = iconDrawableResId),
                            contentDescription = weatherInfo.condition,
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
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Min: ${weatherInfo.minTemperature}°",
                                color = Color.White.copy(alpha = 0.9f),
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Max: ${weatherInfo.maxTemperature}°",
                                color = Color.White.copy(alpha = 0.9f),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                    // --- End Main Weather Info ---

                    // ****** Previsão Horária ANTES dos cards ******
                    Spacer(modifier = Modifier.height(32.dp))

                    // --- Hourly Forecast Section ---
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x40000000))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Previsão para as próximas horas",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )
                        // Log.d("ForecastCheck", "Hourly forecasts count: ${weatherInfo.hourlyForecasts.size}")
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(24.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(weatherInfo.hourlyForecasts) { forecast ->
                                HourlyForecastItem(forecast = forecast, context = context)
                            }
                        }
                    }
                    // --- FIM Hourly Forecast ---

                    // ****** Cards de Informação DEPOIS da previsão ******
                    Spacer(modifier = Modifier.height(24.dp))

                    // --- INFO CARDS START ---
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        // Espaçar uniformemente para 2 colunas parecerem melhor
                        horizontalArrangement = Arrangement.SpaceEvenly, // Alterado para SpaceEvenly
                        verticalArrangement = Arrangement.spacedBy(10.dp), // Espaçamento vertical entre linhas
                        maxItemsInEachRow = 2 // ****** AJUSTADO para 2 colunas ******
                    ) {
                        // Humidity Card
                        InfoCard(
                            label = "Umidade do Ar",
                            value = "${weatherInfo.humidity}%",
                            icon = Icons.Default.WaterDrop
                        )
                        // Wind Speed Card
                        InfoCard(
                            label = "Ventos",
                            value = "%.1f m/s".format(weatherInfo.windSpeed),
                            icon = Icons.Default.Air
                        )

                        // Rain Card (Conditional)
                        weatherInfo.rainVolumeLastHour?.takeIf { it > 0 }?.let { rain ->
                            InfoCard(
                                label = "Precipitação (1h)",
                                value = "$rain mm",
                                icon = Icons.Default.Umbrella
                            )
                        }

                        weatherInfo.feelsLike?.let { feelsLike ->
                            InfoCard(
                                label = "Sensação Térmica",
                                value = "$feelsLike°C",
                                icon = Icons.Default.Thermostat
                            )
                        }

                        // Air Quality Card
                        weatherInfo.airQuality?.list?.get(0)?.main?.aqi?.let { aqi ->
                            val airQuality = when (aqi) {
                                1 -> "Bom"
                                2 -> "Moderado"
                                3 -> "USG"
                                4 -> "Nocivo"
                                5 -> "Muito Nocivo"
                                else -> "Desconhecido"
                            }

                            InfoCard(
                                label = "Qualidade do ar",
                                value = airQuality,
                                icon = Icons.Default.FilterDrama
                            )
                        }
                    }
                    // --- INFO CARDS END ---

                    Spacer(modifier = Modifier.height(16.dp))

                } // End Main Column (Scrollable)
            } else {
                CircularProgressIndicator(color = Color.White)
            }
        } // End Box
    } // End Surface
} // End WeatherScreen


@Composable
fun InfoCard(label: String, value: String, icon: ImageVector) {
    // Pode manter o tamanho anterior ou ajustar se necessário
    Card(
        modifier = Modifier
            .width(125.dp)
            .height(125.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.2f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 8.dp, vertical = 12.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = value,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = label,
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
fun HourlyForecastItem(forecast: HourlyForecast, context: Context) {
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
        val iconDrawableResId: Int = try {
            context.resources.getIdentifier(
                "weather_${forecast.conditionIcon}",
                "drawable",
                context.packageName
            ).takeIf { it != 0 } ?: R.drawable.weather_01d
        } catch (e: Exception) {
            R.drawable.weather_01d
        }
        Image(
            painter = painterResource(id = iconDrawableResId),
            contentDescription = forecast.condition,
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