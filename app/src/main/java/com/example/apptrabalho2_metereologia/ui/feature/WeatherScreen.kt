package com.example.apptrabalho2_metereologia.ui.feature

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged // Import para medir o TextField
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity // Import para converter Px para Dp
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize // Import para converter IntSize para Size
import androidx.compose.ui.window.Popup // <-- IMPORT para Popup manual
import androidx.compose.ui.window.PopupProperties // <-- IMPORT para propriedades do Popup

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
        expanded = searchQuery.isNotEmpty() && results.isNotEmpty() // Condição original aqui está ok
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
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(32.dp))

                    Box(modifier = Modifier.fillMaxWidth()) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { newValue -> searchQuery = newValue },
                            label = { Text("Search Location", color = Color.White.copy(alpha = 0.8f)) },
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
                            val textFieldHeightDp = with(localDensity) { textFieldHeightPx.toDp() }

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
                                                text = { Text("No results found", color = Color.White.copy(alpha = 0.7f)) },
                                                enabled = false,
                                                onClick = {},
                                            )
                                        } else {
                                            filteredLocations.forEach { (city, coordinates) ->
                                                DropdownMenuItem(
                                                    text = { Text(city, color = Color.White) },
                                                    onClick = {
                                                        // Limpa a busca em vez de preencher com a cidade
                                                        searchQuery = ""
                                                        expanded = false // Fecha o popup
                                                        coordinates?.let { nonNullCoords ->
                                                            viewModel.updateLocation(city, nonNullCoords)
                                                        }
                                                        focusManager.clearFocus() // Tira o foco
                                                    },
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } // Fim Box TextField+Popup

                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Search History",
                        color = Color.White,
                        style = MaterialTheme.typography.titleSmall // Um pouco menor
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        horizontalArrangement = Arrangement.Center // Centraliza os botões
                    ) {
                        //Spacer(modifier = Modifier.height(8.dp)) // Removido para alinhar melhor
                        cityHistory.take(3).forEach { city ->
                            Button(
                                onClick = {
                                    // Limpa a busca ao clicar no histórico também
                                    searchQuery = ""
                                    viewModel.updateLocation(city.cityName, city.latitude to city.longitude)
                                    expanded = false
                                    focusManager.clearFocus()
                                },
                                modifier = Modifier
                                    .padding(horizontal = 4.dp)
                                    .widthIn(max = 120.dp), // Limita largura máxima
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp), // Padding interno menor
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0x40000000) // Fundo semi-transparente
                                ),
                            ) {
                                Text(
                                    text = city.cityName,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis, // Adiciona '...' se não couber
                                    fontSize = 12.sp // Tamanho da fonte
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp)) // Espaço antes do nome da cidade
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
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp) // Espaço entre os itens
                    ) {
                        val iconDrawableResId: Int = try {
                            context.resources.getIdentifier(
                                "weather_${weatherInfo.conditionIcon}",
                                "drawable",
                                context.packageName
                            ).takeIf { it != 0 } ?: R.drawable.ic_launcher_foreground // Fallback icon
                        } catch (e: Exception) {
                            R.drawable.ic_launcher_foreground // Fallback icon em caso de erro
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
                        // --- INÍCIO DA MODIFICAÇÃO ---
                        // Adiciona Row para Max e Min
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp), // Espaço entre Max e Min
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Min: ${weatherInfo.minTemperature}°",
                                color = Color.White.copy(alpha = 0.9f), // Um pouco menos opaco que a temp principal
                                style = MaterialTheme.typography.titleMedium // Tamanho médio
                            )
                            Text(
                                text = "Max: ${weatherInfo.maxTemperature}°",
                                color = Color.White.copy(alpha = 0.9f),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                        // --- FIM DA MODIFICAÇÃO ---
                    }
                    Spacer(modifier = Modifier.height(32.dp)) // Espaço antes da previsão
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp)) // Cantos arredondados
                            .background(Color(0x40000000)) // Fundo semi-transparente
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Forecast for the next hours",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(bottom = 16.dp) // Espaço abaixo do título
                        )
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(24.dp), // Espaço entre itens da previsão
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(weatherInfo.hourlyForecasts) { forecast ->
                                HourlyForecastItem(forecast = forecast, context = context)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f)) // Empurra tudo para cima
                }
            } else {
                // Mostra um indicador de carregamento enquanto weatherInfo é nulo
                CircularProgressIndicator(color = Color.White)
            }
        }
    }
}


@Composable
fun HourlyForecastItem(forecast: HourlyForecast, context: Context) {
    // Sem alterações aqui, apenas garantindo que o contexto é passado corretamente
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.width(56.dp) // Largura fixa para cada item da previsão
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
            ).takeIf { it != 0 } ?: R.drawable.ic_launcher_foreground // Fallback
        } catch (e: Exception) {
            R.drawable.ic_launcher_foreground // Fallback em erro
        }
        Image(
            painter = painterResource(id = iconDrawableResId),
            contentDescription = forecast.condition,
            modifier = Modifier.size(32.dp) // Tamanho do ícone da previsão
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