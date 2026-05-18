package com.example.feedlypet.ui.notifications.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feedlypet.R
import com.example.feedlypet.data.network.model.UpdateNotificationSettingsRequest
import com.example.feedlypet.data.repository.NotificationsRepository
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
class NotificationSettingsViewModel @Inject constructor(
    private val repository: NotificationsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationSettingsUiState())
    val uiState: StateFlow<NotificationSettingsUiState> = _uiState.asStateFlow()

    init { loadSettings() }

    private fun loadSettings() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = repository.getSettings()) {
                is AuthResult.Success -> _uiState.update {
                    it.copy(
                        isLoading = false,
                        feedingSuccess = result.data.feedingSuccess,
                        feedingFailed = result.data.feedingFailed,
                        lowFoodLevel = result.data.lowFoodLevel,
                        deviceStatus = result.data.deviceStatus
                    )
                }
                else -> _uiState.update { it.copy(isLoading = false, error = UiText.Res(R.string.common_error_network)) }
            }
        }
    }

    fun updateSetting(key: String, value: Boolean) {
        val prev = _uiState.value
        _uiState.update {
            when (key) {
                "feedingSuccess" -> it.copy(feedingSuccess = value)
                "feedingFailed" -> it.copy(feedingFailed = value)
                "lowFoodLevel" -> it.copy(lowFoodLevel = value)
                "deviceStatus" -> it.copy(deviceStatus = value)
                else -> it
            }
        }
        viewModelScope.launch {
            val state = _uiState.value
            val request = UpdateNotificationSettingsRequest(state.feedingSuccess, state.feedingFailed, state.lowFoodLevel, state.deviceStatus)
            when (repository.updateSettings(request)) {
                is AuthResult.Success -> _uiState.update { it.copy(savedMessage = UiText.Res(R.string.notifications_settings_saved)) }
                else -> {
                    _uiState.update {
                        when (key) {
                            "feedingSuccess" -> it.copy(feedingSuccess = prev.feedingSuccess)
                            "feedingFailed" -> it.copy(feedingFailed = prev.feedingFailed)
                            "lowFoodLevel" -> it.copy(lowFoodLevel = prev.lowFoodLevel)
                            "deviceStatus" -> it.copy(deviceStatus = prev.deviceStatus)
                            else -> it
                        }.copy(error = UiText.Res(R.string.common_error_network))
                    }
                }
            }
        }
    }

    fun clearSavedMessage() = _uiState.update { it.copy(savedMessage = null) }
}
