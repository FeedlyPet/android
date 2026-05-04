package com.example.feedlypet.ui.notifications

import com.example.feedlypet.data.network.model.NotificationDto
import com.example.feedlypet.ui.common.UiText

data class NotificationsUiState(
    val isLoading: Boolean = false,
    val notifications: List<NotificationDto> = emptyList(),
    val error: UiText? = null,
    val unreadOnly: Boolean = false
)
