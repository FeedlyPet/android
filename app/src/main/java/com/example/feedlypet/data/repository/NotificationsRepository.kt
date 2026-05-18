package com.example.feedlypet.data.repository

import com.example.feedlypet.data.network.model.NotificationDto
import com.example.feedlypet.data.network.model.NotificationSettingsDto
import com.example.feedlypet.data.network.model.PaginatedResponse
import com.example.feedlypet.data.network.model.UpdateNotificationSettingsRequest
import com.example.feedlypet.domain.model.AuthResult

interface NotificationsRepository {
    suspend fun getNotifications(page: Int = 1, limit: Int = 20, unreadOnly: Boolean? = null): AuthResult<PaginatedResponse<NotificationDto>>
    suspend fun markAsRead(id: String): AuthResult<NotificationDto>
    suspend fun markAllAsRead(): AuthResult<Unit>
    suspend fun deleteNotification(id: String): AuthResult<Unit>
    suspend fun getSettings(): AuthResult<NotificationSettingsDto>
    suspend fun updateSettings(request: UpdateNotificationSettingsRequest): AuthResult<NotificationSettingsDto>
}
