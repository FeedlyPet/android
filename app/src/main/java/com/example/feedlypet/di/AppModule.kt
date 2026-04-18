package com.example.feedlypet.di

import com.example.feedlypet.BuildConfig
import com.example.feedlypet.data.network.AuthApiService
import com.example.feedlypet.data.network.AuthInterceptor
import com.example.feedlypet.data.network.DevicesApiService
import com.example.feedlypet.data.network.HistoryApiService
import com.example.feedlypet.data.network.NotificationsApiService
import com.example.feedlypet.data.network.PetsApiService
import com.example.feedlypet.data.network.ProfileApiService
import com.example.feedlypet.data.network.SchedulesApiService
import com.example.feedlypet.data.network.StatisticsApiService
import com.example.feedlypet.data.network.TokenAuthenticator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor,
        tokenAuthenticator: TokenAuthenticator
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(authInterceptor)
            .authenticator(tokenAuthenticator)
        if (BuildConfig.DEBUG) {
            builder.addInterceptor(
                HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY }
            )
        }
        return builder.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideAuthApiService(retrofit: Retrofit): AuthApiService =
        retrofit.create(AuthApiService::class.java)

    @Provides
    @Singleton
    fun provideProfileApiService(retrofit: Retrofit): ProfileApiService =
        retrofit.create(ProfileApiService::class.java)

    @Provides
    @Singleton
    fun providePetsApiService(retrofit: Retrofit): PetsApiService =
        retrofit.create(PetsApiService::class.java)

    @Provides
    @Singleton
    fun provideDevicesApiService(retrofit: Retrofit): DevicesApiService =
        retrofit.create(DevicesApiService::class.java)

    @Provides
    @Singleton
    fun provideSchedulesApiService(retrofit: Retrofit): SchedulesApiService =
        retrofit.create(SchedulesApiService::class.java)

    @Provides
    @Singleton
    fun provideHistoryApiService(retrofit: Retrofit): HistoryApiService =
        retrofit.create(HistoryApiService::class.java)

    @Provides
    @Singleton
    fun provideStatisticsApiService(retrofit: Retrofit): StatisticsApiService =
        retrofit.create(StatisticsApiService::class.java)

    @Provides
    @Singleton
    fun provideNotificationsApiService(retrofit: Retrofit): NotificationsApiService =
        retrofit.create(NotificationsApiService::class.java)
}
