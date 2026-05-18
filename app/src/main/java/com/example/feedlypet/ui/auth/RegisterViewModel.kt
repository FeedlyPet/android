package com.example.feedlypet.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feedlypet.data.local.TokenManager
import com.example.feedlypet.data.network.tryRegisterFcmToken
import com.example.feedlypet.data.repository.AuthRepository
import com.example.feedlypet.domain.model.AuthResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    var name by mutableStateOf("")
        private set
    var email by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    var nameError by mutableStateOf<String?>(null)
        private set
    var emailError by mutableStateOf<String?>(null)
        private set
    var passwordError by mutableStateOf<String?>(null)
        private set

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState = _uiState.asStateFlow()

    fun onNameChange(v: String) { name = v; nameError = null }
    fun onEmailChange(v: String) { email = v; emailError = null }
    fun onPasswordChange(v: String) { password = v; passwordError = null }

    fun register() {
        if (!validate()) return
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            _uiState.value = when (val result = repository.register(email, password, name)) {
                is AuthResult.Success -> {
                    tokenManager.getFcmToken()?.let { repository.tryRegisterFcmToken(it) }
                    AuthUiState.Success(result.data.user.email)
                }
                is AuthResult.Error -> AuthUiState.Error(mapErrorCode(result.code))
                is AuthResult.NetworkError -> AuthUiState.Error("No internet connection")
            }
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }

    private fun validate(): Boolean {
        var valid = true
        if (name.isBlank()) {
            nameError = "Name is required"
            valid = false
        }
        if (email.isBlank()) {
            emailError = "Email is required"
            valid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = "Enter a valid email"
            valid = false
        }
        if (password.length < 8) {
            passwordError = "Password must be at least 8 characters"
            valid = false
        }
        return valid
    }

    private fun mapErrorCode(code: Int) = when (code) {
        409 -> "An account with this email already exists"
        else -> "Registration failed. Please try again"
    }
}
