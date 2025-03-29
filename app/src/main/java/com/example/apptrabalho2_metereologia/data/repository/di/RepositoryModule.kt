package com.example.apptrabalho2_metereologia.data.repository.di

import com.example.apptrabalho2_metereologia.data.repository.WeatherRepository
import com.example.apptrabalho2_metereologia.data.repository.WeatherRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideWeatherRepository(
        remoteDataSource: com.example.apptrabalho2_metereologia.data.remote.RemoteDataSource
    ): WeatherRepository {
        return WeatherRepositoryImpl(remoteDataSource)
    }
} 