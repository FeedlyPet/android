package com.example.feedlypet.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.feedlypet.data.repository.AuthRepository
import com.example.feedlypet.domain.model.AuthResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(private val repository: AuthRepository) : ViewModel() {

    var email by mutableStateOf("")
        private set
    var emailError by mutableStateOf<String?>(null)
        private set

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun onEmailChange(v: String) {
        email = v
        emailError = null
    }

    fun submit() {
        if (!validate()) return
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            _uiState.value = when (val result = repository.forgotPassword(email)) {
                is AuthResult.Success -> AuthUiState.Success(result.data.message)
                is AuthResult.Error -> AuthUiState.Error(mapErrorCode(result.code))
                is AuthResult.NetworkError -> AuthUiState.Error("No internet connection")
            }
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }

    private fun validate(): Boolean {
        if (email.isBlank()) {
            emailError = "Email is required"
            return false
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Enter a valid email"
            return false
        }
        return true
    }

    private fun mapErrorCode(code: Int) = when (code) {
        404 -> "No account found with this email"
        else -> "Something went wrong. Please try again"
    }

    class Factory(private val repository: AuthRepository) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T = ForgotPasswordViewModel(repository) as T
    }
}
