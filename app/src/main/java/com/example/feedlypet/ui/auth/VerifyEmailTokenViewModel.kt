package com.example.feedlypet.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feedlypet.R
import com.example.feedlypet.data.repository.AuthRepository
import com.example.feedlypet.domain.model.AuthResult
import com.example.feedlypet.ui.common.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class VerifyEmailTokenUiState(
    val isLoading: Boolean = false,
    val event: VerifyEmailTokenEvent? = null
)

sealed class VerifyEmailTokenEvent {
    object Success : VerifyEmailTokenEvent()
    data class ShowError(val message: UiText) : VerifyEmailTokenEvent()
}

@HiltViewModel
class VerifyEmailTokenViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VerifyEmailTokenUiState())
    val uiState: StateFlow<VerifyEmailTokenUiState> = _uiState.asStateFlow()

    fun verifyEmail(token: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            when (val result = repository.verifyEmail(token)) {
                is AuthResult.Success -> _uiState.update { it.copy(isLoading = false, event = VerifyEmailTokenEvent.Success) }
                is AuthResult.Error -> _uiState.update { it.copy(isLoading = false, event = VerifyEmailTokenEvent.ShowError(UiText.Raw(result.message))) }
                AuthResult.NetworkError -> _uiState.update { it.copy(isLoading = false, event = VerifyEmailTokenEvent.ShowError(UiText.Res(R.string.common_error_network))) }
            }
        }
    }

    fun clearEvent() = _uiState.update { it.copy(event = null) }
}
