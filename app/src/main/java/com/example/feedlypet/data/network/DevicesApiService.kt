package com.example.feedlypet.data.network

import com.example.feedlypet.data.network.model.CreateDeviceRequest
import com.example.feedlypet.data.network.model.DeviceDto
import com.example.feedlypet.data.network.model.DeviceWithPasswordDto
import com.example.feedlypet.data.network.model.FoodLevelDto
import com.example.feedlypet.data.network.model.ManualFeedRequest
import com.example.feedlypet.data.network.model.ManualFeedResponse
import com.example.feedlypet.data.network.model.PaginatedResponse
import com.example.feedlypet.data.network.model.UpdateDeviceRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface DevicesApiService {
    @GET("devices")
    suspend fun getDevices(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): Response<PaginatedResponse<DeviceDto>>

    @GET("devices/{id}")
    suspend fun getDevice(@Path("id") id: String): Response<DeviceDto>

    @POST("devices")
    suspend fun createDevice(@Body request: CreateDeviceRequest): Response<DeviceWithPasswordDto>

    @PATCH("devices/{id}")
    suspend fun updateDevice(
        @Path("id") id: String,
        @Body request: UpdateDeviceRequest
    ): Response<DeviceDto>

    @DELETE("devices/{id}")
    suspend fun deleteDevice(@Path("id") id: String): Response<Unit>

    @POST("devices/{id}/feed")
    suspend fun feed(
        @Path("id") id: String,
        @Body request: ManualFeedRequest
    ): Response<ManualFeedResponse>

    @POST("devices/{id}/regenerate-password")
    suspend fun regeneratePassword(@Path("id") id: String): Response<DeviceWithPasswordDto>

    @GET("devices/{deviceId}/food-level")
    suspend fun getLatestFoodLevel(@Path("deviceId") deviceId: String): Response<FoodLevelDto>
}
