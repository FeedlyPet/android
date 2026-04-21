package com.example.feedlypet.ui.devices.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feedlypet.R
import com.example.feedlypet.data.repository.DevicesRepository
import com.example.feedlypet.data.repository.HistoryRepository
import com.example.feedlypet.data.repository.SchedulesRepository
import com.example.feedlypet.domain.model.AuthResult
import com.example.feedlypet.ui.common.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DeviceDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val devicesRepository: DevicesRepository,
    private val historyRepository: HistoryRepository,
    private val schedulesRepository: SchedulesRepository
) : ViewModel() {

    private val deviceId: String = checkNotNull(savedStateHandle["deviceId"])

    private val _uiState = MutableStateFlow(DeviceDetailUiState())
    val uiState: StateFlow<DeviceDetailUiState> = _uiState.asStateFlow()

    init { loadData() }

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val deviceResult = devicesRepository.getDevice(deviceId)
            val historyResult = historyRepository.getEvents(deviceId, limit = 5)
            val schedulesResult = schedulesRepository.getSchedules(deviceId)
            val foodResult = devicesRepository.getFoodLevel(deviceId)

            if (deviceResult is AuthResult.Success) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        device = deviceResult.data,
                        recentEvents = if (historyResult is AuthResult.Success) historyResult.data.data else emptyList(),
                        activeSchedules = if (schedulesResult is AuthResult.Success) schedulesResult.data.filter { s -> s.isActive }.take(3) else emptyList(),
                        foodLevel = if (foodResult is AuthResult.Success) foodResult.data.foodLevel else 0
                    )
                }
            } else {
                _uiState.update { it.copy(isLoading = false, error = UiText.Res(R.string.common_error_network)) }
            }
        }
    }

    fun showFeedDialog() = _uiState.update { it.copy(showFeedDialog = true) }
    fun hideFeedDialog() = _uiState.update { it.copy(showFeedDialog = false) }
    fun showRegenDialog() = _uiState.update { it.copy(showRegenDialog = true) }
    fun hideRegenDialog() = _uiState.update { it.copy(showRegenDialog = false) }
    fun clearNewPassword() = _uiState.update { it.copy(newPassword = null) }
    fun clearFeedingSuccess() = _uiState.update { it.copy(feedingSuccess = null) }

    fun feed(portionSize: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isFeedingLoading = true, showFeedDialog = false) }
            when (devicesRepository.feed(deviceId, portionSize)) {
                is AuthResult.Success -> _uiState.update { it.copy(isFeedingLoading = false, feedingSuccess = UiText.Res(R.string.device_feed_command_sent)) }
                else -> _uiState.update { it.copy(isFeedingLoading = false, error = UiText.Res(R.string.common_error_network)) }
            }
        }
    }

    fun regeneratePassword() {
        viewModelScope.launch {
            _uiState.update { it.copy(showRegenDialog = false) }
            when (val result = devicesRepository.regeneratePassword(deviceId)) {
                is AuthResult.Success -> _uiState.update { it.copy(newPassword = result.data.mqttPassword) }
                else -> _uiState.update { it.copy(error = UiText.Res(R.string.common_error_network)) }
            }
        }
    }
}
