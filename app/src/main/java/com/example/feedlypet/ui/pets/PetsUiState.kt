package com.example.feedlypet.ui.pets

import com.example.feedlypet.data.network.model.PetDto
import com.example.feedlypet.ui.common.UiText

data class PetsUiState(
    val isLoading: Boolean = false,
    val pets: List<PetDto> = emptyList(),
    val error: UiText? = null,
    val searchQuery: String = "",
    val isFormVisible: Boolean = false,
    val editingPet: PetDto? = null,
    val deleteCandidate: PetDto? = null,
    val isSaving: Boolean = false,
    val successMessage: UiText? = null
)
