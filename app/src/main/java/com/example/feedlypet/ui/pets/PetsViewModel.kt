package com.example.feedlypet.ui.pets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feedlypet.R
import com.example.feedlypet.data.network.model.CreatePetRequest
import com.example.feedlypet.data.network.model.PetDto
import com.example.feedlypet.data.network.model.UpdatePetRequest
import com.example.feedlypet.data.repository.PetsRepository
import com.example.feedlypet.domain.model.AuthResult
import com.example.feedlypet.ui.common.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PetsViewModel @Inject constructor(
    private val repository: PetsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PetsUiState())
    val uiState: StateFlow<PetsUiState> = _uiState.asStateFlow()

    init { loadPets() }

    fun loadPets() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            val search = _uiState.value.searchQuery.takeIf { it.isNotBlank() }
            when (val result = repository.getPets(search = search)) {
                is AuthResult.Success -> _uiState.update { it.copy(isLoading = false, pets = result.data.data) }
                else -> _uiState.update { it.copy(isLoading = false, error = UiText.Res(R.string.common_error_network)) }
            }
        }
    }

    fun onSearchChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        loadPets()
    }

    fun showAddForm() = _uiState.update { it.copy(isFormVisible = true, editingPet = null) }
    fun showEditForm(pet: PetDto) = _uiState.update { it.copy(isFormVisible = true, editingPet = pet) }
    fun hideForm() = _uiState.update { it.copy(isFormVisible = false, editingPet = null) }
    fun showDeleteDialog(pet: PetDto) = _uiState.update { it.copy(deleteCandidate = pet) }
    fun hideDeleteDialog() = _uiState.update { it.copy(deleteCandidate = null) }

    fun savePet(name: String, species: String, weight: Double) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            val editing = _uiState.value.editingPet
            val result = if (editing == null) {
                repository.createPet(CreatePetRequest(name, species, weight))
            } else {
                repository.updatePet(editing.id, UpdatePetRequest(name, species, weight))
            }
            when (result) {
                is AuthResult.Success -> {
                    _uiState.update { it.copy(isSaving = false, isFormVisible = false, editingPet = null) }
                    loadPets()
                }
                else -> _uiState.update { it.copy(isSaving = false, error = UiText.Res(R.string.common_error_network)) }
            }
        }
    }

    fun deletePet() {
        val pet = _uiState.value.deleteCandidate ?: return
        viewModelScope.launch {
            _uiState.update { it.copy(deleteCandidate = null) }
            when (repository.deletePet(pet.id)) {
                is AuthResult.Success -> loadPets()
                else -> _uiState.update { it.copy(error = UiText.Res(R.string.common_error_network)) }
            }
        }
    }

    fun clearError() = _uiState.update { it.copy(error = null) }
}
