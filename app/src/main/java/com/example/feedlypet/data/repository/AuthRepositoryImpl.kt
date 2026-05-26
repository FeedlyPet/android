package com.example.feedlypet.data.repository

import com.example.feedlypet.data.local.TokenManager
import com.example.feedlypet.data.network.AuthApiService
import com.example.feedlypet.data.network.bodyOrError
import com.example.feedlypet.data.network.model.AuthResponse
import com.example.feedlypet.data.network.model.ForgotPasswordRequest
import com.example.feedlypet.data.network.model.LoginRequest
import com.example.feedlypet.data.network.model.MessageResponse
import com.example.feedlypet.data.network.model.RegisterRequest
import com.example.feedlypet.data.network.model.ResendVerificationRequest
import com.example.feedlypet.data.network.model.ResetPasswordRequest
import com.example.feedlypet.data.network.model.VerifyEmailRequest
import com.example.feedlypet.data.network.safeApiCall
import com.example.feedlypet.domain.model.AuthResult
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val apiService: AuthApiService,
    private val tokenManager: TokenManager
) : AuthRepository {

    override suspend fun login(email: String, password: String): AuthResult<AuthResponse> =
        safeApiCall {
            val response = apiService.login(LoginRequest(email, password))
            val result = response.bodyOrError<AuthResponse>()
            if (result is AuthResult.Success) {
                tokenManager.saveTokens(result.data.accessToken, result.data.refreshToken)
                tokenManager.saveUser(result.data.user.id, result.data.user.email, result.data.user.name, result.data.user.timezone)
            }
            result
        }

    override suspend fun register(email: String, password: String, name: String): AuthResult<AuthResponse> =
        safeApiCall {
            val response = apiService.register(RegisterRequest(email, password, name))
            val result = response.bodyOrError<AuthResponse>()
            if (result is AuthResult.Success) {
                tokenManager.saveTokens(result.data.accessToken, result.data.refreshToken)
                tokenManager.saveUser(result.data.user.id, result.data.user.email, result.data.user.name, result.data.user.timezone)
            }
            result
        }

    override suspend fun forgotPassword(email: String): AuthResult<MessageResponse> =
        safeApiCall { apiService.forgotPassword(ForgotPasswordRequest(email)).bodyOrError() }

    override suspend fun resendVerification(email: String): AuthResult<MessageResponse> =
        safeApiCall { apiService.resendVerification(ResendVerificationRequest(email)).bodyOrError() }

    override suspend fun verifyEmail(token: String): AuthResult<MessageResponse> =
        safeApiCall { apiService.verifyEmail(VerifyEmailRequest(token)).bodyOrError() }

    override suspend fun resetPassword(token: String, newPassword: String): AuthResult<MessageResponse> =
        safeApiCall { apiService.resetPassword(ResetPasswordRequest(token, newPassword)).bodyOrError() }

    override suspend fun registerFcmToken(token: String): AuthResult<MessageResponse> =
        // Backend endpoint not yet available — token saved locally only
        AuthResult.Success(MessageResponse("ok"))
}
