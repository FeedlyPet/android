package com.example.feedlypet.ui.devices.statistics

import com.example.feedlypet.data.network.model.StatisticsDto
import com.example.feedlypet.ui.common.UiText

data class StatisticsUiState(
    val isLoading: Boolean = false,
    val statistics: StatisticsDto? = null,
    val error: UiText? = null,
    val period: String = "week"
)
