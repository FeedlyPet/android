package com.example.feedlypet.data.repository

import com.example.feedlypet.data.network.PetsApiService
import com.example.feedlypet.data.network.bodyOrError
import com.example.feedlypet.data.network.model.CreatePetRequest
import com.example.feedlypet.data.network.model.PaginatedResponse
import com.example.feedlypet.data.network.model.PetDto
import com.example.feedlypet.data.network.model.UpdatePetRequest
import com.example.feedlypet.data.network.safeApiCall
import com.example.feedlypet.domain.model.AuthResult
import javax.inject.Inject

class PetsRepositoryImpl @Inject constructor(
    private val apiService: PetsApiService
) : PetsRepository {

    override suspend fun getPets(page: Int, limit: Int, sortBy: String?, sortOrder: String?, search: String?): AuthResult<PaginatedResponse<PetDto>> =
        safeApiCall { apiService.getPets(page, limit, sortBy, sortOrder, search).bodyOrError() }

    override suspend fun createPet(request: CreatePetRequest): AuthResult<PetDto> =
        safeApiCall { apiService.createPet(request).bodyOrError() }

    override suspend fun updatePet(id: String, request: UpdatePetRequest): AuthResult<PetDto> =
        safeApiCall { apiService.updatePet(id, request).bodyOrError() }

    override suspend fun deletePet(id: String): AuthResult<Unit> =
        safeApiCall { apiService.deletePet(id).bodyOrError() }
}
