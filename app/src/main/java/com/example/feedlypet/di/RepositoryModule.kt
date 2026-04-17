package com.example.feedlypet.di

import com.example.feedlypet.data.repository.AuthRepository
import com.example.feedlypet.data.repository.AuthRepositoryImpl
import com.example.feedlypet.data.repository.DevicesRepository
import com.example.feedlypet.data.repository.DevicesRepositoryImpl
import com.example.feedlypet.data.repository.HistoryRepository
import com.example.feedlypet.data.repository.HistoryRepositoryImpl
import com.example.feedlypet.data.repository.HomeRepository
import com.example.feedlypet.data.repository.HomeRepositoryImpl
import com.example.feedlypet.data.repository.NotificationsRepository
import com.example.feedlypet.data.repository.NotificationsRepositoryImpl
import com.example.feedlypet.data.repository.PetsRepository
import com.example.feedlypet.data.repository.PetsRepositoryImpl
import com.example.feedlypet.data.repository.ProfileRepository
import com.example.feedlypet.data.repository.ProfileRepositoryImpl
import com.example.feedlypet.data.repository.SchedulesRepository
import com.example.feedlypet.data.repository.SchedulesRepositoryImpl
import com.example.feedlypet.data.repository.StatisticsRepository
import com.example.feedlypet.data.repository.StatisticsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindHomeRepository(impl: HomeRepositoryImpl): HomeRepository

    @Binds
    @Singleton
    abstract fun bindProfileRepository(impl: ProfileRepositoryImpl): ProfileRepository

    @Binds
    @Singleton
    abstract fun bindPetsRepository(impl: PetsRepositoryImpl): PetsRepository

    @Binds
    @Singleton
    abstract fun bindDevicesRepository(impl: DevicesRepositoryImpl): DevicesRepository

    @Binds
    @Singleton
    abstract fun bindSchedulesRepository(impl: SchedulesRepositoryImpl): SchedulesRepository

    @Binds
    @Singleton
    abstract fun bindHistoryRepository(impl: HistoryRepositoryImpl): HistoryRepository

    @Binds
    @Singleton
    abstract fun bindStatisticsRepository(impl: StatisticsRepositoryImpl): StatisticsRepository

    @Binds
    @Singleton
    abstract fun bindNotificationsRepository(impl: NotificationsRepositoryImpl): NotificationsRepository
}
