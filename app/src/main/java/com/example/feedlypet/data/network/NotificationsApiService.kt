package com.example.feedlypet.data.network

import com.example.feedlypet.data.network.model.NotificationDto
import com.example.feedlypet.data.network.model.NotificationSettingsDto
import com.example.feedlypet.data.network.model.PaginatedResponse
import com.example.feedlypet.data.network.model.UpdateNotificationSettingsRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path
import retrofit2.http.Query

interface NotificationsApiService {
    @GET("notifications")
    suspend fun getNotifications(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("unreadOnly") unreadOnly: Boolean? = null
    ): Response<PaginatedResponse<NotificationDto>>

    @PATCH("notifications/{id}/read")
    suspend fun markAsRead(@Path("id") id: String): Response<NotificationDto>

    @PATCH("notifications/read-all")
    suspend fun markAllAsRead(): Response<Unit>

    @DELETE("notifications/{id}")
    suspend fun deleteNotification(@Path("id") id: String): Response<Unit>

    @GET("notifications/settings")
    suspend fun getSettings(): Response<NotificationSettingsDto>

    @PATCH("notifications/settings")
    suspend fun updateSettings(@Body request: UpdateNotificationSettingsRequest): Response<NotificationSettingsDto>
}
