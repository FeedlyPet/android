package com.example.feedlypet.ui.auth

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val message: String = "") : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}
