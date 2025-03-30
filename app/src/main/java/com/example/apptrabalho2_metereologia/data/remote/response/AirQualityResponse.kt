package com.example.apptrabalho2_metereologia.data.remote.response

import kotlinx.serialization.Serializable

@Serializable
data class AirQualityResponse(
    val list: List<AirQualityData>
)

@Serializable
data class AirQualityData(
    val main: AirQualityIndex,
    val components: AirQualityComponents
)

@Serializable
data class AirQualityIndex(
    val aqi: Int
)

@Serializable
data class AirQualityComponents(
    val co: Float,
    val no2: Float,
    val o3: Float,
    val so2: Float,
    val pm2_5: Float,
    val pm10: Float,
    val nh3: Float
)