package com.example.feedlypet.data.network.model

data class NotificationDto(
    val id: String,
    val userId: String,
    val deviceId: String?,
    val type: String,
    val title: String,
    val message: String,
    val isRead: Boolean,
    val createdAt: String
)

data class NotificationSettingsDto(
    val feedingSuccess: Boolean,
    val feedingFailed: Boolean,
    val lowFoodLevel: Boolean,
    val deviceStatus: Boolean
)

data class UpdateNotificationSettingsRequest(
    val feedingSuccess: Boolean? = null,
    val feedingFailed: Boolean? = null,
    val lowFoodLevel: Boolean? = null,
    val deviceStatus: Boolean? = null
)
