package com.example.feedlypet.ui.devices

import com.example.feedlypet.data.network.model.DeviceDto
import com.example.feedlypet.ui.common.UiText

data class DevicesUiState(
    val isLoading: Boolean = false,
    val devices: List<DeviceDto> = emptyList(),
    val foodLevels: Map<String, Int> = emptyMap(),
    val error: UiText? = null,
    val showRegisterDialog: Boolean = false,
    val isRegistering: Boolean = false,
    val newDevicePassword: String? = null
)
