package com.example.feedlypet.data.network.model

data class FeedingEventDto(
    val id: String,
    val deviceId: String,
    val petId: String?,
    val scheduleId: String?,
    val timestamp: String,
    val portionSize: Int,
    val type: String,
    val success: Boolean,
    val errorMessage: String?,
    val createdAt: String
)
