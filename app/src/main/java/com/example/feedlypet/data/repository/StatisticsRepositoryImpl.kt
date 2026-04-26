package com.example.feedlypet.data.repository

import com.example.feedlypet.data.network.StatisticsApiService
import com.example.feedlypet.data.network.bodyOrError
import com.example.feedlypet.data.network.model.StatisticsDto
import com.example.feedlypet.data.network.safeApiCall
import com.example.feedlypet.domain.model.AuthResult
import javax.inject.Inject

class StatisticsRepositoryImpl @Inject constructor(
    private val apiService: StatisticsApiService
) : StatisticsRepository {

    override suspend fun getStatistics(deviceId: String, period: String): AuthResult<StatisticsDto> =
        safeApiCall { apiService.getStatistics(deviceId, period).bodyOrError() }
}
