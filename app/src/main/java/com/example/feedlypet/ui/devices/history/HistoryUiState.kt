package com.example.feedlypet.ui.devices.history

import com.example.feedlypet.data.network.model.FeedingEventDto
import com.example.feedlypet.ui.common.UiText

data class HistoryUiState(
    val isLoading: Boolean = false,
    val events: List<FeedingEventDto> = emptyList(),
    val error: UiText? = null,
    val typeFilter: String? = null,
    val successFilter: Boolean? = null
)
