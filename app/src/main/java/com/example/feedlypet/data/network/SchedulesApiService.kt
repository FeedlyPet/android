package com.example.feedlypet.data.network

import com.example.feedlypet.data.network.model.CreateScheduleRequest
import com.example.feedlypet.data.network.model.ScheduleDto
import com.example.feedlypet.data.network.model.UpdateScheduleRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface SchedulesApiService {
    @GET("devices/{deviceId}/schedules")
    suspend fun getSchedules(@Path("deviceId") deviceId: String): Response<List<ScheduleDto>>

    @POST("devices/{deviceId}/schedules")
    suspend fun createSchedule(
        @Path("deviceId") deviceId: String,
        @Body request: CreateScheduleRequest
    ): Response<ScheduleDto>

    @PATCH("schedules/{id}")
    suspend fun updateSchedule(
        @Path("id") id: String,
        @Body request: UpdateScheduleRequest
    ): Response<ScheduleDto>

    @PATCH("schedules/{id}/toggle")
    suspend fun toggleSchedule(@Path("id") id: String): Response<ScheduleDto>

    @DELETE("schedules/{id}")
    suspend fun deleteSchedule(@Path("id") id: String): Response<Unit>
}
