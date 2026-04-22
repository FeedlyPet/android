package com.example.feedlypet.ui.devices.history

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feedlypet.R
import com.example.feedlypet.data.repository.HistoryRepository
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
class HistoryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: HistoryRepository
) : ViewModel() {

    private val deviceId: String = checkNotNull(savedStateHandle["deviceId"])

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init { loadEvents() }

    fun loadEvents() {
        viewModelScope.launch {
            val state = _uiState.value
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = repository.getEvents(deviceId, type = state.typeFilter, success = state.successFilter)) {
                is AuthResult.Success -> _uiState.update { it.copy(isLoading = false, events = result.data.data) }
                else -> _uiState.update { it.copy(isLoading = false, error = UiText.Res(R.string.common_error_network)) }
            }
        }
    }

    fun setTypeFilter(type: String?) {
        _uiState.update { it.copy(typeFilter = type) }
        loadEvents()
    }

    fun setSuccessFilter(success: Boolean?) {
        _uiState.update { it.copy(successFilter = success) }
        loadEvents()
    }
}
