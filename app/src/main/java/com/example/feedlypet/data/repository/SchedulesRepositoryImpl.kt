package com.example.feedlypet.data.repository

import com.example.feedlypet.data.network.SchedulesApiService
import com.example.feedlypet.data.network.bodyOrError
import com.example.feedlypet.data.network.model.CreateScheduleRequest
import com.example.feedlypet.data.network.model.ScheduleDto
import com.example.feedlypet.data.network.model.UpdateScheduleRequest
import com.example.feedlypet.data.network.safeApiCall
import com.example.feedlypet.domain.model.AuthResult
import javax.inject.Inject

class SchedulesRepositoryImpl @Inject constructor(
    private val apiService: SchedulesApiService
) : SchedulesRepository {

    override suspend fun getSchedules(deviceId: String): AuthResult<List<ScheduleDto>> =
        safeApiCall { apiService.getSchedules(deviceId).bodyOrError() }

    override suspend fun createSchedule(deviceId: String, request: CreateScheduleRequest): AuthResult<ScheduleDto> =
        safeApiCall { apiService.createSchedule(deviceId, request).bodyOrError() }

    override suspend fun updateSchedule(id: String, request: UpdateScheduleRequest): AuthResult<ScheduleDto> =
        safeApiCall { apiService.updateSchedule(id, request).bodyOrError() }

    override suspend fun toggleSchedule(id: String): AuthResult<ScheduleDto> =
        safeApiCall { apiService.toggleSchedule(id).bodyOrError() }

    override suspend fun deleteSchedule(id: String): AuthResult<Unit> =
        safeApiCall { apiService.deleteSchedule(id).bodyOrError() }
}
