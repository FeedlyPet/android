package com.example.feedlypet.data.repository

import com.example.feedlypet.data.network.model.AuthResponse
import com.example.feedlypet.data.network.model.MessageResponse
import com.example.feedlypet.domain.model.AuthResult

interface AuthRepository {
    suspend fun login(email: String, password: String): AuthResult<AuthResponse>
    suspend fun register(email: String, password: String, name: String): AuthResult<AuthResponse>
    suspend fun forgotPassword(email: String): AuthResult<MessageResponse>
    suspend fun resendVerification(email: String): AuthResult<MessageResponse>
    suspend fun verifyEmail(token: String): AuthResult<MessageResponse>
    suspend fun resetPassword(token: String, newPassword: String): AuthResult<MessageResponse>
    suspend fun registerFcmToken(token: String): AuthResult<MessageResponse>
}
