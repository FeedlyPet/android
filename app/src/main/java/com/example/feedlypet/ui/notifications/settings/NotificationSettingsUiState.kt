package com.example.feedlypet.ui.notifications.settings

import com.example.feedlypet.ui.common.UiText

data class NotificationSettingsUiState(
    val isLoading: Boolean = false,
    val feedingSuccess: Boolean = true,
    val feedingFailed: Boolean = true,
    val lowFoodLevel: Boolean = true,
    val deviceStatus: Boolean = true,
    val error: UiText? = null,
    val savedMessage: UiText? = null
)
