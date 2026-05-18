package com.example.feedlypet.ui.devices.statistics

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feedlypet.R
import com.example.feedlypet.data.repository.StatisticsRepository
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
class StatisticsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: StatisticsRepository
) : ViewModel() {

    private val deviceId: String = checkNotNull(savedStateHandle["deviceId"])

    private val _uiState = MutableStateFlow(StatisticsUiState())
    val uiState: StateFlow<StatisticsUiState> = _uiState.asStateFlow()

    init { loadStatistics() }

    fun loadStatistics() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = repository.getStatistics(deviceId, _uiState.value.period)) {
                is AuthResult.Success -> _uiState.update { it.copy(isLoading = false, statistics = result.data) }
                else -> _uiState.update { it.copy(isLoading = false, error = UiText.Res(R.string.common_error_network)) }
            }
        }
    }

    fun setPeriod(period: String) {
        _uiState.update { it.copy(period = period) }
        loadStatistics()
    }
}
