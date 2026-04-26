package com.example.feedlypet.data.repository

import com.example.feedlypet.data.network.HistoryApiService
import com.example.feedlypet.data.network.bodyOrError
import com.example.feedlypet.data.network.model.FeedingEventDto
import com.example.feedlypet.data.network.model.PaginatedResponse
import com.example.feedlypet.data.network.safeApiCall
import com.example.feedlypet.domain.model.AuthResult
import javax.inject.Inject

class HistoryRepositoryImpl @Inject constructor(
    private val apiService: HistoryApiService
) : HistoryRepository {

    override suspend fun getEvents(
        deviceId: String,
        page: Int,
        limit: Int,
        type: String?,
        success: Boolean?,
        startDate: String?,
        endDate: String?
    ): AuthResult<PaginatedResponse<FeedingEventDto>> =
        safeApiCall { apiService.getEvents(deviceId, page, limit, type, success, startDate, endDate).bodyOrError() }
}
