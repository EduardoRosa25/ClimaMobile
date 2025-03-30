package com.example.apptrabalho2_metereologia.data

import android.content.Context
import android.util.Log
import com.example.apptrabalho2_metereologia.R
import com.example.apptrabalho2_metereologia.data.remote.RemoteDataSource
import com.example.apptrabalho2_metereologia.data.remote.response.AirQualityResponse
import com.example.apptrabalho2_metereologia.data.remote.response.ForecastResponse
import com.example.apptrabalho2_metereologia.data.remote.response.WeatherDataResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.url
import javax.inject.Inject

class KtorRemoteDataSource @Inject constructor(
    private val httpClient: HttpClient,
    private val context: Context
) : RemoteDataSource {

    private val baseUrl = "https://api.openweathermap.org/data/2.5"
    private val apiKey: String
        get() = context.getString(R.string.openweathermap_api_key)

    override suspend fun getWeatherDataResponse(lat: Float, lng: Float): WeatherDataResponse {
        val weatherUrl = "$baseUrl/weather?lat=$lat&lon=$lng&units=metric&appid=$apiKey"
        val forecastUrl = "$baseUrl/forecast?lat=$lat&lon=$lng&units=metric&appid=$apiKey"
        val airQualityUrl = "$baseUrl/air_pollution?lat=$lat&lon=$lng&appid=$apiKey"
        Log.d("WeatherAPI", "Fazendo requisição para: $weatherUrl")
        Log.d("WeatherAPI", "Fazendo requisição para previsão: $forecastUrl")

        return try {
            val weatherResponse = httpClient.get {
                url(weatherUrl)
            }.body<WeatherDataResponse>()

            val forecastResponse = httpClient.get {
                url(forecastUrl)
            }.body<ForecastResponse>()
            val airQualityResponse = httpClient.get { url(airQualityUrl) }.body<AirQualityResponse>()
            // Atualiza a resposta do tempo atual com a previsão horária
            weatherResponse.copy(
                airQuality = airQualityResponse,
                hourlyForecast = forecastResponse.list.take(6),
                dailyForecast = forecastResponse.list
                    .groupBy { it.dtTxt.substring(0, 10) }
                    .mapNotNull { (_, forecasts) ->
                        forecasts.maxByOrNull { it.main.tempMax }
                    }
            )
        } catch (e: Exception) {
            Log.e("WeatherAPI", "Erro ao fazer requisição: ${e.message}", e)
            throw e
        }
    }
}