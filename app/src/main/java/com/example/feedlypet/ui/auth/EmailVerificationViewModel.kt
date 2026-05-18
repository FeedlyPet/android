package com.example.feedlypet.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feedlypet.data.repository.AuthRepository
import com.example.feedlypet.domain.model.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EmailVerificationViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState = _uiState.asStateFlow()

    private val _cooldown = MutableStateFlow(0)
    val cooldown = _cooldown.asStateFlow()

    private var cooldownJob: Job? = null

    fun resendEmail(email: String) {
        if (_cooldown.value > 0) return
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            _uiState.value = when (val result = repository.resendVerification(email)) {
                is AuthResult.Success -> {
                    startCooldown()
                    AuthUiState.Success(result.data.message)
                }
                is AuthResult.Error -> AuthUiState.Error(
                    when (result.code) {
                        404 -> "No account found with this email"
                        else -> "Failed to resend. Please try again"
                    }
                )
                is AuthResult.NetworkError -> AuthUiState.Error("No internet connection")
            }
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }

    private fun startCooldown() {
        cooldownJob?.cancel()
        cooldownJob = viewModelScope.launch {
            _cooldown.value = 60
            while (_cooldown.value > 0) {
                delay(1000)
                _cooldown.value -= 1
            }
        }
    }
}
