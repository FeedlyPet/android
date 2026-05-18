package com.example.feedlypet.data.network

import com.example.feedlypet.data.network.model.CreatePetRequest
import com.example.feedlypet.data.network.model.PaginatedResponse
import com.example.feedlypet.data.network.model.PetDto
import com.example.feedlypet.data.network.model.UpdatePetRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface PetsApiService {
    @GET("pets")
    suspend fun getPets(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20,
        @Query("sortBy") sortBy: String? = null,
        @Query("sortOrder") sortOrder: String? = null,
        @Query("search") search: String? = null
    ): Response<PaginatedResponse<PetDto>>

    @POST("pets")
    suspend fun createPet(@Body request: CreatePetRequest): Response<PetDto>

    @PATCH("pets/{id}")
    suspend fun updatePet(@Path("id") id: String, @Body request: UpdatePetRequest): Response<PetDto>

    @DELETE("pets/{id}")
    suspend fun deletePet(@Path("id") id: String): Response<Unit>
}
