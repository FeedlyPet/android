package com.example.feedlypet.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feedlypet.R
import com.example.feedlypet.data.network.bodyOrError
import com.example.feedlypet.data.network.model.ResetPasswordRequest
import com.example.feedlypet.data.network.safeApiCall
import com.example.feedlypet.data.repository.AuthRepository
import com.example.feedlypet.domain.model.AuthResult
import com.example.feedlypet.ui.common.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ResetPasswordUiState(
    val newPassword: String = "",
    val confirmPassword: String = "",
    val newPasswordError: Int? = null,
    val confirmPasswordError: Int? = null,
    val isLoading: Boolean = false,
    val event: ResetPasswordEvent? = null
)

sealed class ResetPasswordEvent {
    object Success : ResetPasswordEvent()
    data class ShowError(val message: UiText) : ResetPasswordEvent()
}

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ResetPasswordUiState())
    val uiState: StateFlow<ResetPasswordUiState> = _uiState.asStateFlow()

    fun onNewPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(newPassword = value, newPasswordError = null)
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(confirmPassword = value, confirmPasswordError = null)
    }

    fun resetPassword(token: String) {
        val state = _uiState.value
        val pwdError = validatePasswordMin(state.newPassword)
        val confirmError = if (state.newPassword != state.confirmPassword) R.string.auth_error_passwords_mismatch else null
        if (pwdError != null || confirmError != null) {
            _uiState.value = state.copy(newPasswordError = pwdError, confirmPasswordError = confirmError)
            return
        }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            when (val result = repository.resetPassword(token, state.newPassword)) {
                is AuthResult.Success -> _uiState.value = _uiState.value.copy(isLoading = false, event = ResetPasswordEvent.Success)
                is AuthResult.Error -> _uiState.value = _uiState.value.copy(isLoading = false, event = ResetPasswordEvent.ShowError(UiText.Raw(result.message)))
                AuthResult.NetworkError -> _uiState.value = _uiState.value.copy(isLoading = false, event = ResetPasswordEvent.ShowError(UiText.Res(R.string.common_error_network)))
            }
        }
    }

    fun clearEvent() { _uiState.value = _uiState.value.copy(event = null) }
}
