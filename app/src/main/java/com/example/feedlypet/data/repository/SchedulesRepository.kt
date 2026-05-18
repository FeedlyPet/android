package com.example.feedlypet.data.repository

import com.example.feedlypet.data.network.model.CreateScheduleRequest
import com.example.feedlypet.data.network.model.ScheduleDto
import com.example.feedlypet.data.network.model.UpdateScheduleRequest
import com.example.feedlypet.domain.model.AuthResult

interface SchedulesRepository {
    suspend fun getSchedules(deviceId: String): AuthResult<List<ScheduleDto>>
    suspend fun createSchedule(deviceId: String, request: CreateScheduleRequest): AuthResult<ScheduleDto>
    suspend fun updateSchedule(id: String, request: UpdateScheduleRequest): AuthResult<ScheduleDto>
    suspend fun toggleSchedule(id: String): AuthResult<ScheduleDto>
    suspend fun deleteSchedule(id: String): AuthResult<Unit>
}
