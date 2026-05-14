package com.example.feedlypet.data.repository

import com.example.feedlypet.data.network.ProfileApiService
import com.example.feedlypet.data.network.bodyOrError
import com.example.feedlypet.data.network.model.ChangePasswordRequest
import com.example.feedlypet.data.network.model.UpdateProfileRequest
import com.example.feedlypet.data.network.model.UserProfileDto
import com.example.feedlypet.data.network.safeApiCall
import com.example.feedlypet.domain.model.AuthResult
import javax.inject.Inject

class ProfileRepositoryImpl @Inject constructor(
    private val apiService: ProfileApiService
) : ProfileRepository {

    override suspend fun getProfile(): AuthResult<UserProfileDto> =
        safeApiCall { apiService.getProfile().bodyOrError() }

    override suspend fun updateProfile(request: UpdateProfileRequest): AuthResult<UserProfileDto> =
        safeApiCall { apiService.updateProfile(request).bodyOrError() }

    override suspend fun changePassword(request: ChangePasswordRequest): AuthResult<Unit> =
        safeApiCall { apiService.changePassword(request).bodyOrError() }
}
