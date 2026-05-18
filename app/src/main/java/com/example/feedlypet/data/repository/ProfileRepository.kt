package com.example.feedlypet.data.repository

import com.example.feedlypet.data.network.model.ChangePasswordRequest
import com.example.feedlypet.data.network.model.UpdateProfileRequest
import com.example.feedlypet.data.network.model.UserProfileDto
import com.example.feedlypet.domain.model.AuthResult

interface ProfileRepository {
    suspend fun getProfile(): AuthResult<UserProfileDto>
    suspend fun updateProfile(request: UpdateProfileRequest): AuthResult<UserProfileDto>
    suspend fun changePassword(request: ChangePasswordRequest): AuthResult<Unit>
}
