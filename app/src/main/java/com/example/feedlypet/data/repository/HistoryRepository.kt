package com.example.feedlypet.data.repository

import com.example.feedlypet.data.network.model.FeedingEventDto
import com.example.feedlypet.data.network.model.PaginatedResponse
import com.example.feedlypet.domain.model.AuthResult

interface HistoryRepository {
    suspend fun getEvents(
        deviceId: String,
        page: Int = 1,
        limit: Int = 20,
        type: String? = null,
        success: Boolean? = null,
        startDate: String? = null,
        endDate: String? = null
    ): AuthResult<PaginatedResponse<FeedingEventDto>>
}
