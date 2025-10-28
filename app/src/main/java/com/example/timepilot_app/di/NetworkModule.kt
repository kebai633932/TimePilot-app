package com.example.timepilot_app.di

import com.example.timepilot_app.data.remote.ScheduleApi
import com.example.timepilot_app.data.remote.RetrofitClient
import com.example.timepilot_app.data.repository.ScheduleRepository
import com.example.timepilot_app.data.local.ScheduleDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideScheduleApi(): ScheduleApi = RetrofitClient.scheduleApi

    @Provides
    @Singleton
    fun provideRepository(
        dao: ScheduleDao,
        api: ScheduleApi
    ): ScheduleRepository = ScheduleRepository(dao, api)
}
