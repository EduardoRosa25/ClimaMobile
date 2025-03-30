// File: app/src/main/java/com/example/apptrabalho2_metereologia/data/repository/WeatherRepositoryImpl.kt
package com.example.apptrabalho2_metereologia.data.repository

import com.example.apptrabalho2_metereologia.data.model.HourlyForecast
import com.example.apptrabalho2_metereologia.data.model.WeatherInfo
import com.example.apptrabalho2_metereologia.data.remote.RemoteDataSource
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject
import kotlin.text.titlecase // Import para titlecase se necessário

class WeatherRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : WeatherRepository {
    override suspend fun getWeatherData(lat: Float, lng: Float): WeatherInfo {
        val response = remoteDataSource.getWeatherDataResponse(lat, lng)
        val weather = response.weather.firstOrNull() ?: throw IllegalStateException("Weather list is empty in API response")

        val now = LocalDateTime.now()
        val currentHour = now.hour

        fun isDayTime(hour: Int): Boolean {
            return hour in 6..17
        }

        fun adjustIconForTime(icon: String, hour: Int): String {
            val baseIcon = if (icon.length > 1) icon.dropLast(1) else icon
            return baseIcon + if (isDayTime(hour)) "d" else "n"
        }

        // 1. Gera a lista de previsão horária primeiro
        val hourlyForecasts = response.hourlyForecast?.take(6)?.mapNotNull { forecast ->
            val forecastDateTime = LocalDateTime.ofEpochSecond(forecast.dt, 0, ZoneOffset.UTC)
            val forecastHour = forecastDateTime.hour
            val forecastWeather = forecast.weather.firstOrNull()

            if (forecastWeather != null) {
                HourlyForecast(
                    time = String.format("%02d:00", forecastHour),
                    temperature = forecast.main.temp.toInt(),
                    conditionIcon = adjustIconForTime(forecastWeather.icon, forecastHour),
                    condition = forecastWeather.main
                )
            } else {
                null
            }
        } ?: emptyList()

        // 2. Calcula Min/Max a partir da lista de previsão gerada
        val minTempFromForecast: Int?
        val maxTempFromForecast: Int?

        if (hourlyForecasts.isNotEmpty()) {
            // Extrai todas as temperaturas da lista de previsão
            val temperaturesInForecast = hourlyForecasts.map { it.temperature }
            minTempFromForecast = temperaturesInForecast.minOrNull() // Encontra o menor valor Int?
            maxTempFromForecast = temperaturesInForecast.maxOrNull() // Encontra o maior valor Int?
        } else {
            minTempFromForecast = null
            maxTempFromForecast = null
        }

        val ptBrLocale = Locale("pt", "BR")

        // 3. Cria o WeatherInfo usando os valores calculados (com fallback para API)
        return WeatherInfo(
            locationName = response.name,
            conditionIcon = adjustIconForTime(weather.icon, currentHour),
            condition = weather.main,
            temperature = response.main.temp.toInt(), // Temperatura atual ainda vem da API principal
            dayOfWeek = now.dayOfWeek.getDisplayName(TextStyle.FULL, ptBrLocale)
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(ptBrLocale) else it.toString() },
            isDay = isDayTime(currentHour),
            hourlyForecasts = hourlyForecasts, // A lista de previsão
            // --- MIN/MAX AGORA VÊM DA PREVISÃO (com fallback) ---
            maxTemperature = maxTempFromForecast ?: response.main.tempMax.toInt(), // Usa max da previsão, ou fallback da API
            minTemperature = minTempFromForecast ?: response.main.tempMin.toInt(), // Usa min da previsão, ou fallback da API
            // --- FIM DA MODIFICAÇÃO MIN/MAX ---
            humidity = response.main.humidity,
            windSpeed = response.wind.speed,
            rainVolumeLastHour = response.rain?.oneHour
        )
    }
}