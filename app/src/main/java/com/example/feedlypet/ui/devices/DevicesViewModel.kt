package com.example.feedlypet.ui.devices

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feedlypet.R
import com.example.feedlypet.data.network.model.CreateDeviceRequest
import com.example.feedlypet.data.repository.DevicesRepository
import com.example.feedlypet.domain.model.AuthResult
import com.example.feedlypet.ui.common.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DevicesViewModel @Inject constructor(
    private val repository: DevicesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DevicesUiState())
    val uiState: StateFlow<DevicesUiState> = _uiState.asStateFlow()

    init { loadDevices() }

    fun loadDevices() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = repository.getDevices()) {
                is AuthResult.Success -> {
                    val devices = result.data.data
                    _uiState.update { it.copy(isLoading = false, devices = devices) }
                    // Load food levels in parallel for all devices
                    val foodLevels = coroutineScope {
                        devices.map { device ->
                            async {
                                val fr = repository.getFoodLevel(device.id)
                                if (fr is AuthResult.Success) device.id to fr.data.foodLevel else null
                            }
                        }.mapNotNull { it.await() }.toMap()
                    }
                    _uiState.update { it.copy(foodLevels = foodLevels) }
                }
                else -> _uiState.update { it.copy(isLoading = false, error = UiText.Res(R.string.common_error_network)) }
            }
        }
    }

    fun showRegisterDialog() = _uiState.update { it.copy(showRegisterDialog = true) }
    fun hideRegisterDialog() = _uiState.update { it.copy(showRegisterDialog = false) }
    fun clearNewPassword() = _uiState.update { it.copy(newDevicePassword = null) }

    fun registerDevice(deviceId: String, name: String, location: String?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isRegistering = true) }
            when (val result = repository.createDevice(CreateDeviceRequest(deviceId, name, location?.takeIf { it.isNotBlank() }))) {
                is AuthResult.Success -> {
                    _uiState.update { it.copy(isRegistering = false, showRegisterDialog = false, newDevicePassword = result.data.mqttPassword) }
                    loadDevices()
                }
                else -> _uiState.update { it.copy(isRegistering = false, error = UiText.Res(R.string.common_error_network)) }
            }
        }
    }
}
