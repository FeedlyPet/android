package com.example.feedlypet.data.repository

import com.example.feedlypet.data.network.model.CreatePetRequest
import com.example.feedlypet.data.network.model.PaginatedResponse
import com.example.feedlypet.data.network.model.PetDto
import com.example.feedlypet.data.network.model.UpdatePetRequest
import com.example.feedlypet.domain.model.AuthResult

interface PetsRepository {
    suspend fun getPets(page: Int = 1, limit: Int = 20, sortBy: String? = null, sortOrder: String? = null, search: String? = null): AuthResult<PaginatedResponse<PetDto>>
    suspend fun createPet(request: CreatePetRequest): AuthResult<PetDto>
    suspend fun updatePet(id: String, request: UpdatePetRequest): AuthResult<PetDto>
    suspend fun deletePet(id: String): AuthResult<Unit>
}
