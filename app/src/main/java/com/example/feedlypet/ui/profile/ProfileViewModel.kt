package com.example.feedlypet.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.feedlypet.R
import com.example.feedlypet.data.local.TokenManager
import com.example.feedlypet.data.network.model.ChangePasswordRequest
import com.example.feedlypet.data.network.model.UpdateProfileRequest
import com.example.feedlypet.data.network.model.UserProfileDto
import com.example.feedlypet.data.repository.AuthRepository
import com.example.feedlypet.data.repository.ProfileRepository
import com.example.feedlypet.domain.model.AuthResult
import com.example.feedlypet.ui.common.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val isLoading: Boolean = false,
    val profile: UserProfileDto? = null,
    val error: UiText? = null,
    val isFormVisible: Boolean = false,
    val isPasswordFormVisible: Boolean = false,
    val isSaving: Boolean = false,
    val successMessage: UiText? = null,
    val showLogoutDialog: Boolean = false,
    val loggedOut: Boolean = false
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    private val authRepository: AuthRepository,
    private val tokenManager: TokenManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init { loadProfile() }

    fun loadProfile() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = profileRepository.getProfile()) {
                is AuthResult.Success -> _uiState.update { it.copy(isLoading = false, profile = result.data) }
                else -> _uiState.update { it.copy(isLoading = false, error = UiText.Res(R.string.common_error_network)) }
            }
        }
    }

    fun showEditForm() = _uiState.update { it.copy(isFormVisible = true) }
    fun hideEditForm() = _uiState.update { it.copy(isFormVisible = false) }
    fun showPasswordForm() = _uiState.update { it.copy(isPasswordFormVisible = true) }
    fun hidePasswordForm() = _uiState.update { it.copy(isPasswordFormVisible = false) }
    fun showLogoutDialog() = _uiState.update { it.copy(showLogoutDialog = true) }
    fun hideLogoutDialog() = _uiState.update { it.copy(showLogoutDialog = false) }
    fun clearSuccessMessage() = _uiState.update { it.copy(successMessage = null) }

    fun saveProfile(name: String, email: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            when (val result = profileRepository.updateProfile(UpdateProfileRequest(name, email))) {
                is AuthResult.Success -> _uiState.update { it.copy(isSaving = false, isFormVisible = false, profile = result.data, successMessage = UiText.Res(R.string.profile_saved)) }
                else -> _uiState.update { it.copy(isSaving = false, error = UiText.Res(R.string.common_error_network)) }
            }
        }
    }

    fun changePassword(currentPassword: String, newPassword: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSaving = true) }
            when (profileRepository.changePassword(ChangePasswordRequest(currentPassword, newPassword))) {
                is AuthResult.Success -> _uiState.update { it.copy(isSaving = false, isPasswordFormVisible = false, successMessage = UiText.Res(R.string.profile_password_changed)) }
                else -> _uiState.update { it.copy(isSaving = false, error = UiText.Res(R.string.common_error_network)) }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            _uiState.update { it.copy(showLogoutDialog = false) }
            tokenManager.clearTokens()
            _uiState.update { it.copy(loggedOut = true) }
        }
    }
}
