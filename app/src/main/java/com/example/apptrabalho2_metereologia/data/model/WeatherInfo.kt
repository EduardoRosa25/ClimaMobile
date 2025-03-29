package com.example.apptrabalho2_metereologia.data.model

data class WeatherInfo(
    val locationName: String,
    val conditionIcon: String,
    val condition: String,
    val temperature: Int,
    val dayOfWeek: String,
    val isDay: Boolean,
    val hourlyForecasts: List<HourlyForecast> = emptyList()
)