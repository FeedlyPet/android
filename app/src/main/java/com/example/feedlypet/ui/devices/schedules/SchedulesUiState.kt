package com.example.feedlypet.ui.devices.schedules

import com.example.feedlypet.data.network.model.ScheduleDto
import com.example.feedlypet.ui.common.UiText

data class SchedulesUiState(
    val isLoading: Boolean = false,
    val schedules: List<ScheduleDto> = emptyList(),
    val error: UiText? = null,
    val filter: ScheduleFilter = ScheduleFilter.ALL,
    val isFormVisible: Boolean = false,
    val editingSchedule: ScheduleDto? = null,
    val deleteCandidate: ScheduleDto? = null
)

enum class ScheduleFilter { ALL, ACTIVE, INACTIVE }
