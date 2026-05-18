package com.example.feedlypet.data.network

import com.example.feedlypet.data.network.model.AuthResponse
import com.example.feedlypet.data.network.model.FcmTokenRequest
import com.example.feedlypet.data.network.model.ForgotPasswordRequest
import com.example.feedlypet.data.network.model.LoginRequest
import com.example.feedlypet.data.network.model.MessageResponse
import com.example.feedlypet.data.network.model.RefreshTokenRequest
import com.example.feedlypet.data.network.model.RefreshTokenResponse
import com.example.feedlypet.data.network.model.RegisterRequest
import com.example.feedlypet.data.network.model.ResendVerificationRequest
import com.example.feedlypet.data.network.model.ResetPasswordRequest
import com.example.feedlypet.data.network.model.VerifyEmailRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<MessageResponse>

    @POST("auth/resend-verification")
    suspend fun resendVerification(@Body request: ResendVerificationRequest): Response<MessageResponse>

    @POST("auth/refresh")
    suspend fun refresh(@Body request: RefreshTokenRequest): Response<RefreshTokenResponse>

    @POST("auth/verify-email")
    suspend fun verifyEmail(@Body request: VerifyEmailRequest): Response<MessageResponse>

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest): Response<MessageResponse>

    @POST("auth/fcm-token")
    suspend fun registerFcmToken(@Body request: FcmTokenRequest): Response<MessageResponse>
}
