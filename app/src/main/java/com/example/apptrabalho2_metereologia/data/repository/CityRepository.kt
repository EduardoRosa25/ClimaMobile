package com.example.apptrabalho2_metereologia.data.repository

import com.example.apptrabalho2_metereologia.data.local.CityDao
import com.example.apptrabalho2_metereologia.data.local.CityEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CityRepository @Inject constructor(private val cityDao: CityDao) {
    suspend fun insertCity(city: CityEntity) {
        cityDao.insertCity(city)
        cityDao.deleteOldCities()
    }

    fun getLastThreeCities(): Flow<List<CityEntity>> = cityDao.getLastThreeCities()

    suspend fun cityExists(cityName: String): Boolean {
        return cityDao.findCityByName(cityName) != null
    }
}