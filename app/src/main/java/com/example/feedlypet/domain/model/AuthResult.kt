package com.example.feedlypet.domain.model

sealed class AuthResult<out T> {
    data class Success<T>(val data: T) : AuthResult<T>()
    data class Error(val code: Int, val message: String) : AuthResult<Nothing>()
    object NetworkError : AuthResult<Nothing>()
}
