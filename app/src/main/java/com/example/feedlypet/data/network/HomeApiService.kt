package com.example.feedlypet.data.network

import com.example.feedlypet.data.network.model.HomeData
import retrofit2.Response
import retrofit2.http.GET

interface HomeApiService {
    @GET("home")
    suspend fun getHomeData(): Response<HomeData>
}
