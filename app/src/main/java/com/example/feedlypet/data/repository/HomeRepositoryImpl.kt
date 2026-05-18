package com.example.feedlypet.data.repository

import com.example.feedlypet.data.network.HomeApiService
import com.example.feedlypet.data.network.bodyOrError
import com.example.feedlypet.data.network.model.HomeData
import com.example.feedlypet.data.network.safeApiCall
import com.example.feedlypet.domain.model.AuthResult
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val apiService: HomeApiService
) : HomeRepository {

    override suspend fun getHomeData(): AuthResult<HomeData> =
        safeApiCall { apiService.getHomeData().bodyOrError() }
}
