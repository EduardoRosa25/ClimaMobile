package com.example.apptrabalho2_metereologia.data.repository

import com.example.apptrabalho2_metereologia.data.model.HourlyForecast
import com.example.apptrabalho2_metereologia.data.model.WeatherInfo
import com.example.apptrabalho2_metereologia.data.remote.RemoteDataSource
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : WeatherRepository {
    override suspend fun getWeatherData(lat: Float, lng: Float): WeatherInfo {
        val response = remoteDataSource.getWeatherDataResponse(lat, lng)
        val weather = response.weather[0]

        val now = LocalDateTime.now()
        val currentHour = now.hour

        // Função auxiliar para determinar se é dia ou noite
        fun isDayTime(hour: Int): Boolean {
            return hour in 6..17 // Considera dia entre 6h e 17h
        }

        // Função para ajustar o ícone baseado na hora
        fun adjustIconForTime(icon: String, hour: Int): String {
            val baseIcon = icon.dropLast(1) // Remove o 'd' ou 'n' do final
            return baseIcon + if (isDayTime(hour)) "d" else "n"
        }
        
        val hourlyForecasts = response.hourlyForecast?.let { forecasts ->
            val forecastMap = forecasts.associate { forecast ->
                val forecastDateTime = LocalDateTime.ofEpochSecond(forecast.dt, 0, ZoneOffset.UTC)
                forecastDateTime.hour to forecast
            }
            
            (currentHour + 1 until currentHour + 25).mapNotNull { hour ->
                val adjustedHour = hour % 24
                forecastMap[adjustedHour]?.let { forecast ->
                    HourlyForecast(
                        time = String.format("%02d:00", adjustedHour),
                        temperature = forecast.main.temp.toInt(),
                        conditionIcon = adjustIconForTime(forecast.weather[0].icon, adjustedHour),
                        condition = forecast.weather[0].main
                    )
                } ?: HourlyForecast(
                    time = String.format("%02d:00", adjustedHour),
                    temperature = response.main.temp.toInt(),
                    conditionIcon = adjustIconForTime(weather.icon, adjustedHour),
                    condition = weather.main
                )
            }
        } ?: (currentHour + 1 until currentHour + 25).map { hour ->
            val adjustedHour = hour % 24
            HourlyForecast(
                time = String.format("%02d:00", adjustedHour),
                temperature = response.main.temp.toInt(),
                conditionIcon = adjustIconForTime(weather.icon, adjustedHour),
                condition = weather.main
            )
        }

        val dailyForecasts = response.dailyForecast?.map { forecast ->
            val forecastDate = LocalDateTime.ofEpochSecond(forecast.dt, 0, ZoneOffset.UTC)
                .toLocalDate()
            val dayMonth = forecastDate.format(
                DateTimeFormatter.ofPattern("d MMM", Locale("pt", "BR"))
            )
            HourlyForecast(
                time = dayMonth.toString(),
                temperature = forecast.main.temp.toInt(),
                conditionIcon = forecast.weather[0].icon,
                condition = forecast.weather[0].main
            )
        } ?: emptyList()

        return WeatherInfo(
            locationName = response.name,
            conditionIcon = adjustIconForTime(weather.icon, currentHour),
            condition = weather.main,
            temperature = response.main.temp.toInt(),
            dayOfWeek = now.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("pt", "BR"))
                .replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale("pt", "BR")) else it.toString() },
            isDay = isDayTime(currentHour),
            hourlyForecasts = hourlyForecasts,
            maxTemperature = response.main.tempMax.toInt(),
            minTemperature = response.main.tempMin.toInt(),
            humidity = response.main.humidity,
            windSpeed = response.wind.speed,
            rainVolumeLastHour = response.rain?.oneHour,
            airQuality = response.airQuality,
            feelsLike = response.main.feelsLike.toInt(),
            dailyForecast = dailyForecasts
        )
    }
}