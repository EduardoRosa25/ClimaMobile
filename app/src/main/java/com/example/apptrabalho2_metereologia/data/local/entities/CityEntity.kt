package com.example.apptrabalho2_metereologia.data.local
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "city_history")
data class CityEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cityName: String,
    val latitude: Float,
    val longitude: Float
)
