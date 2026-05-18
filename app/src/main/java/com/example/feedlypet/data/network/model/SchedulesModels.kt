package com.example.feedlypet.data.network.model

data class ScheduleDto(
    val id: String,
    val deviceId: String,
    val feedingTime: String,
    val portionSize: Int,
    val daysOfWeek: List<String>,
    val isActive: Boolean,
    val createdAt: String,
    val updatedAt: String
)

data class CreateScheduleRequest(
    val feedingTime: String,
    val portionSize: Int,
    val daysOfWeek: List<String>,
    val isActive: Boolean = true
)

data class UpdateScheduleRequest(
    val feedingTime: String? = null,
    val portionSize: Int? = null,
    val daysOfWeek: List<String>? = null,
    val isActive: Boolean? = null
)
