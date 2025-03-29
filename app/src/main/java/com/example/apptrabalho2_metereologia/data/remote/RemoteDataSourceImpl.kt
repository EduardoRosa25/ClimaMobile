package com.example.apptrabalho2_metereologia.data.remote

import com.example.apptrabalho2_metereologia.data.remote.RemoteDataSource
import android.content.Context
import android.util.Log
import com.example.apptrabalho2_metereologia.R
import com.example.apptrabalho2_metereologia.data.remote.response.WeatherDataResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.url
import javax.inject.Inject

class RemoteDataSourceImpl @Inject constructor(
    private val httpClient: HttpClient,
    private val context: Context
) : RemoteDataSource {

    private val baseUrl = "https://api.openweathermap.org/data/3.0"
    private val apiKey: String
        get() = context.getString(R.string.openweathermap_api_key)

    override suspend fun getWeatherDataResponse(lat: Float, lng: Float): WeatherDataResponse {
        val url = "$baseUrl/onecall?lat=$lat&lon=$lng&exclude=minutely,daily,alerts&units=metric&appid=$apiKey"
        Log.d("WeatherAPI", "Fazendo requisição para: $url")

        return try {
            val response = httpClient.get {
                url(url)
            }.body<WeatherDataResponse>()

            Log.d("WeatherAPI", "Resposta recebida com sucesso")
            response
        } catch (e: Exception) {
            Log.e("WeatherAPI", "Erro ao fazer requisição: ${e.message}", e)
            throw e
        }
    }
}