package com.example.feedlypet.data.network

import com.example.feedlypet.data.network.model.ChangePasswordRequest
import com.example.feedlypet.data.network.model.UpdateProfileRequest
import com.example.feedlypet.data.network.model.UserProfileDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH

interface ProfileApiService {
    @GET("users/profile")
    suspend fun getProfile(): Response<UserProfileDto>

    @PATCH("users/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<UserProfileDto>

    @PATCH("users/password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<Unit>
}
