package com.example.feedlypet.data.network

import com.example.feedlypet.data.network.model.FeedingEventDto
import com.example.feedlypet.data.network.model.PaginatedResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface HistoryApiService {
    @GET("devices/{deviceId}/events")
    suspend fun getEvents(
        @Path("deviceId") deviceId: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("type") type: String? = null,
        @Query("success") success: Boolean? = null,
        @Query("startDate") startDate: String? = null,
        @Query("endDate") endDate: String? = null
    ): Response<PaginatedResponse<FeedingEventDto>>
}
