package com.example.feedlypet.data.network.model

data class LoginRequest(val email: String, val password: String)

data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String,
    val timezone: String? = null
)

data class AuthResponse(
    val user: UserDto,
    val accessToken: String,
    val refreshToken: String
)

data class UserDto(
    val id: String,
    val email: String,
    val name: String,
    val timezone: String?,
    val isEmailVerified: Boolean
)

data class MessageResponse(val message: String)

data class ForgotPasswordRequest(val email: String)

data class RefreshTokenRequest(val refreshToken: String)

data class RefreshTokenResponse(val accessToken: String, val refreshToken: String)

data class ResendVerificationRequest(val email: String)

data class VerifyEmailRequest(val token: String)
