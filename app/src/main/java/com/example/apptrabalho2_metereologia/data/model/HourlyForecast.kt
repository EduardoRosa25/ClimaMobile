package com.example.apptrabalho2_metereologia.data.model

data class HourlyForecast(
    val time: String,
    val temperature: Int,
    val conditionIcon: String,
    val condition: String
) 