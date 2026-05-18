package com.example.feedlypet.data.network

import com.example.feedlypet.data.network.model.StatisticsDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface StatisticsApiService {
    @GET("statistics/feeding/{deviceId}")
    suspend fun getStatistics(
        @Path("deviceId") deviceId: String,
        @Query("period") period: String = "week"
    ): Response<StatisticsDto>
}
