package com.example.apptrabalho2_metereologia.ui.feature

import android.app.Application
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.apptrabalho2_metereologia.data.local.CityEntity
import com.example.apptrabalho2_metereologia.data.repository.CityRepository
import com.example.apptrabalho2_metereologia.data.repository.WeatherRepository
import com.example.apptrabalho2_metereologia.utils.LocationHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WeatherViewodel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val application: Application,
    private val cityRepository: CityRepository,
) : ViewModel() {

    private val _weatherInfoState = MutableStateFlow(WeatherInfoState())
    val weatherInfoState: StateFlow<WeatherInfoState> = _weatherInfoState.asStateFlow()
    private val _customLocation = MutableStateFlow<Pair<Float?, Float?>?>(null)
    fun updateLocation(city: String, coordinates: Pair<Float?, Float?>) {
        _customLocation.value = coordinates
        getWeatherInfo()
        viewModelScope.launch {
            try {
                coordinates.takeIf { it.first != null && it.second != null }?.let { (lat, lon) ->
                    if (!cityRepository.cityExists(city)) {
                        cityRepository.insertCity(
                            CityEntity(
                                cityName = city,
                                latitude = lat!!,
                                longitude = lon!!
                            )
                        )
                        Log.d("ViewModel", "City added: $city")
                    } else {
                        Log.d("ViewModel", "City already exists: $city")
                    }
                }
            } catch (e: Exception) {
                Log.e("ViewModel", "Error saving city", e)
            }
        }
    }
    private val _cityHistory = MutableStateFlow<List<CityEntity>>(emptyList())
    val cityHistory: StateFlow<List<CityEntity>> = _cityHistory.asStateFlow()

    init {
        Log.d("WeatherViewModel", "Inicializando ViewModel")
        getWeatherInfo()
        viewModelScope.launch {
            cityRepository.getLastThreeCities().collect { cities ->
                _cityHistory.value = cities
                Log.d("ViewModel", "Stored cities: ${cities.joinToString { it.cityName }}")
            }
        }
    }

    private fun getWeatherInfo() {
        viewModelScope.launch {
            Log.d("WeatherViewModel", "Iniciando busca de dados do clima")
            val locationHelper = LocationHelper(application.applicationContext)
            try {
                val location = locationHelper.getCurrentLocation()
                if (location != null) {
                    Log.d("WeatherViewModel", "Localização obtida: ${location.latitude}, ${location.longitude}")
                    val latitude = _customLocation.value?.first ?: location.latitude.toFloat()
                    val longitude = _customLocation.value?.second ?: location.longitude.toFloat()
                    val weatherInfo = weatherRepository.getWeatherData(latitude, longitude)
                    Log.d("WeatherViewModel", "Dados do clima obtidos: $weatherInfo")
                    _weatherInfoState.update {
                        it.copy(weatherInfo = weatherInfo)
                    }
                } else {
                    Log.e("WeatherViewModel", "Não foi possível obter a localização")
                }
            } catch (e: Exception) {
                Log.e("WeatherViewModel", "Erro ao buscar dados do clima: ${e.message}", e)
                e.printStackTrace()
            }
        }
    }
}