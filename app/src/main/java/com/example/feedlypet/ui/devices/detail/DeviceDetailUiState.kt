package com.example.feedlypet.ui.devices.detail

import com.example.feedlypet.data.network.model.DeviceDto
import com.example.feedlypet.data.network.model.FeedingEventDto
import com.example.feedlypet.data.network.model.ScheduleDto
import com.example.feedlypet.ui.common.UiText

data class DeviceDetailUiState(
    val isLoading: Boolean = false,
    val device: DeviceDto? = null,
    val foodLevel: Int = 0,
    val recentEvents: List<FeedingEventDto> = emptyList(),
    val activeSchedules: List<ScheduleDto> = emptyList(),
    val error: UiText? = null,
    val isFeedingLoading: Boolean = false,
    val feedingSuccess: UiText? = null,
    val showFeedDialog: Boolean = false,
    val showRegenDialog: Boolean = false,
    val newPassword: String? = null
)
