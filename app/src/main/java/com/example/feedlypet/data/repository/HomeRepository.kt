package com.example.feedlypet.data.repository

import com.example.feedlypet.data.network.model.HomeData
import com.example.feedlypet.domain.model.AuthResult

interface HomeRepository {
    suspend fun getHomeData(): AuthResult<HomeData>
}
