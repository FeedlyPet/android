package com.example.feedlypet.domain.model

data class User(
    val id: String,
    val email: String,
    val name: String,
    val timezone: String?,
    val isEmailVerified: Boolean
)
