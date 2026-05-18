package com.example.feedlypet.ui.devices.schedules

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feedlypet.R
import com.example.feedlypet.data.network.model.CreateScheduleRequest
import com.example.feedlypet.data.network.model.ScheduleDto
import com.example.feedlypet.data.network.model.UpdateScheduleRequest
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
class SchedulesViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: SchedulesRepository
) : ViewModel() {

    private val deviceId: String = checkNotNull(savedStateHandle["deviceId"])

    private val _uiState = MutableStateFlow(SchedulesUiState())
    val uiState: StateFlow<SchedulesUiState> = _uiState.asStateFlow()

    init { loadSchedules() }

    fun loadSchedules() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = repository.getSchedules(deviceId)) {
                is AuthResult.Success -> _uiState.update { it.copy(isLoading = false, schedules = result.data) }
                else -> _uiState.update { it.copy(isLoading = false, error = UiText.Res(R.string.common_error_network)) }
            }
        }
    }

    fun setFilter(filter: ScheduleFilter) = _uiState.update { it.copy(filter = filter) }
    fun showAddForm() = _uiState.update { it.copy(isFormVisible = true, editingSchedule = null) }
    fun showEditForm(schedule: ScheduleDto) = _uiState.update { it.copy(isFormVisible = true, editingSchedule = schedule) }
    fun hideForm() = _uiState.update { it.copy(isFormVisible = false, editingSchedule = null) }
    fun showDeleteDialog(schedule: ScheduleDto) = _uiState.update { it.copy(deleteCandidate = schedule) }
    fun hideDeleteDialog() = _uiState.update { it.copy(deleteCandidate = null) }

    fun toggleSchedule(scheduleId: String) {
        viewModelScope.launch {
            when (val result = repository.toggleSchedule(scheduleId)) {
                is AuthResult.Success -> _uiState.update { state ->
                    state.copy(schedules = state.schedules.map { if (it.id == scheduleId) result.data else it })
                }
                else -> _uiState.update { it.copy(error = UiText.Res(R.string.common_error_network)) }
            }
        }
    }

    fun saveSchedule(feedingTime: String, portionSize: Int, daysOfWeek: List<String>, isActive: Boolean) {
        viewModelScope.launch {
            val editing = _uiState.value.editingSchedule
            val result = if (editing == null) {
                repository.createSchedule(deviceId, CreateScheduleRequest(feedingTime, portionSize, daysOfWeek, isActive))
            } else {
                repository.updateSchedule(editing.id, UpdateScheduleRequest(feedingTime, portionSize, daysOfWeek, isActive))
            }
            when (result) {
                is AuthResult.Success -> { hideForm(); loadSchedules() }
                else -> _uiState.update { it.copy(error = UiText.Res(R.string.common_error_network)) }
            }
        }
    }

    fun deleteSchedule() {
        val schedule = _uiState.value.deleteCandidate ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(deleteCandidate = null) }
            when (repository.deleteSchedule(schedule.id)) {
                is AuthResult.Success -> loadSchedules()
                else -> _uiState.update { it.copy(error = UiText.Res(R.string.common_error_network)) }
            }
        }
    }
}
