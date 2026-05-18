package com.example.feedlypet.data.network.model

data class PetDto(
    val id: String,
    val userId: String,
    val name: String,
    val species: String,
    val weight: Double,
    val createdAt: String,
    val updatedAt: String
)

data class CreatePetRequest(
    val name: String,
    val species: String,
    val weight: Double
)

data class UpdatePetRequest(
    val name: String? = null,
    val species: String? = null,
    val weight: Double? = null
)
