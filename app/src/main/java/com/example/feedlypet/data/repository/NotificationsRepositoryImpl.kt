package com.example.feedlypet.data.repository

import com.example.feedlypet.data.network.NotificationsApiService
import com.example.feedlypet.data.network.bodyOrError
import com.example.feedlypet.data.network.model.NotificationDto
import com.example.feedlypet.data.network.model.NotificationSettingsDto
import com.example.feedlypet.data.network.model.PaginatedResponse
import com.example.feedlypet.data.network.model.UpdateNotificationSettingsRequest
import com.example.feedlypet.data.network.safeApiCall
import com.example.feedlypet.domain.model.AuthResult
import javax.inject.Inject

class NotificationsRepositoryImpl @Inject constructor(
    private val apiService: NotificationsApiService
) : NotificationsRepository {

    override suspend fun getNotifications(page: Int, limit: Int, unreadOnly: Boolean?): AuthResult<PaginatedResponse<NotificationDto>> =
        safeApiCall { apiService.getNotifications(page, limit, unreadOnly).bodyOrError() }

    override suspend fun markAsRead(id: String): AuthResult<NotificationDto> =
        safeApiCall { apiService.markAsRead(id).bodyOrError() }

    override suspend fun markAllAsRead(): AuthResult<Unit> =
        safeApiCall { apiService.markAllAsRead().bodyOrError() }

    override suspend fun deleteNotification(id: String): AuthResult<Unit> =
        safeApiCall { apiService.deleteNotification(id).bodyOrError() }

    override suspend fun getSettings(): AuthResult<NotificationSettingsDto> =
        safeApiCall { apiService.getSettings().bodyOrError() }

    override suspend fun updateSettings(request: UpdateNotificationSettingsRequest): AuthResult<NotificationSettingsDto> =
        safeApiCall { apiService.updateSettings(request).bodyOrError() }
}
