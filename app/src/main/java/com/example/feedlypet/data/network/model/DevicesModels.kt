package com.example.feedlypet.data.network.model

data class DeviceDto(
    val id: String,
    val userId: String,
    val petId: String?,
    val deviceId: String,
    val name: String,
    val location: String?,
    val isOnline: Boolean,
    val lastSeen: String?,
    val createdAt: String,
    val updatedAt: String
)

data class DeviceWithPasswordDto(
    val id: String,
    val userId: String,
    val petId: String?,
    val deviceId: String,
    val name: String,
    val location: String?,
    val isOnline: Boolean,
    val lastSeen: String?,
    val createdAt: String,
    val updatedAt: String,
    val mqttPassword: String
)

data class CreateDeviceRequest(
    val deviceId: String,
    val name: String,
    val location: String? = null,
    val petId: String? = null
)

data class UpdateDeviceRequest(
    val name: String? = null,
    val location: String? = null,
    val petId: String? = null
)

data class ManualFeedRequest(val portionSize: Int)

data class ManualFeedResponse(val success: Boolean, val message: String)

data class FoodLevelDto(
    val deviceId: String,
    @com.google.gson.annotations.SerializedName("level") val foodLevel: Int,
    val timestamp: String
)
