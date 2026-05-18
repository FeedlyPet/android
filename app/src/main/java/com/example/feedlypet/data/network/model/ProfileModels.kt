package com.example.feedlypet.data.network.model

data class UserProfileDto(
    val id: String,
    val email: String,
    val name: String,
    val timezone: String?,
    val createdAt: String
)

data class UpdateProfileRequest(
    val name: String? = null,
    val email: String? = null,
    val timezone: String? = null
)

data class ChangePasswordRequest(
    val currentPassword: String,
    val newPassword: String
)
