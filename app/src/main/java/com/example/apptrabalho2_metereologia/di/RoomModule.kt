package com.example.apptrabalho2_metereologia.di

import android.content.Context
import androidx.room.Room
import com.example.apptrabalho2_metereologia.data.local.AppDatabase
import com.example.apptrabalho2_metereologia.data.local.CityDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(SingletonComponent::class)
object RoomModule {

    @Provides
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "weather_database"
        ).build()
    }

    @Provides
    fun provideCityDao(database: AppDatabase): CityDao {
        return database.cityDao()
    }
}