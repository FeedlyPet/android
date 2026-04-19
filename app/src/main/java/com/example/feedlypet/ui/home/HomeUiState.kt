package com.example.feedlypet.ui.home

import com.example.feedlypet.data.network.model.DeviceDto
import com.example.feedlypet.data.network.model.FeedingEventDto
import com.example.feedlypet.data.network.model.StatisticsDto
import com.example.feedlypet.ui.common.UiText

data class HomeUiState(
    val isLoading: Boolean = false,
    val petsCount: Int = 0,
    val devicesOnline: Int = 0,
    val devicesOffline: Int = 0,
    val unreadNotifications: Int = 0,
    val devices: List<DeviceDto> = emptyList(),
    val foodLevels: Map<String, Int> = emptyMap(),
    val recentEvents: List<FeedingEventDto> = emptyList(),
    val weekStats: StatisticsDto? = null,
    val error: UiText? = null,
    val isFeedingLoading: Boolean = false,
    val feedingSuccess: UiText? = null
)
