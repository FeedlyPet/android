package com.example.feedlypet.data.repository

import com.example.feedlypet.data.network.model.StatisticsDto
import com.example.feedlypet.domain.model.AuthResult

interface StatisticsRepository {
    suspend fun getStatistics(deviceId: String, period: String = "week"): AuthResult<StatisticsDto>
}
