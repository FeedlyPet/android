package com.example.feedlypet.data.repository

import com.example.feedlypet.data.local.TokenDataStore
import com.example.feedlypet.data.network.AuthApiService
import com.example.feedlypet.data.network.model.AuthResponse
import com.example.feedlypet.data.network.model.ForgotPasswordRequest
import com.example.feedlypet.data.network.model.LoginRequest
import com.example.feedlypet.data.network.model.MessageResponse
import com.example.feedlypet.data.network.model.RegisterRequest
import com.example.feedlypet.data.network.model.ResendVerificationRequest
import com.example.feedlypet.domain.model.AuthResult
import org.json.JSONObject
import retrofit2.Response
import java.io.IOException

class AuthRepositoryImpl(
    private val apiService: AuthApiService,
    private val tokenDataStore: TokenDataStore
) : AuthRepository {

    override suspend fun login(email: String, password: String): AuthResult<AuthResponse> =
        safeApiCall {
            val response = apiService.login(LoginRequest(email, password))
            if (response.isSuccessful) {
                val body = response.body()!!
                tokenDataStore.saveTokens(body.accessToken, body.refreshToken)
                tokenDataStore.saveUser(body.user.id, body.user.email, body.user.name)
                AuthResult.Success(body)
            } else {
                parseError(response)
            }
        }

    override suspend fun register(email: String, password: String, name: String): AuthResult<AuthResponse> =
        safeApiCall {
            val response = apiService.register(RegisterRequest(email, password, name))
            if (response.isSuccessful) {
                val body = response.body()!!
                tokenDataStore.saveTokens(body.accessToken, body.refreshToken)
                tokenDataStore.saveUser(body.user.id, body.user.email, body.user.name)
                AuthResult.Success(body)
            } else {
                parseError(response)
            }
        }

    override suspend fun forgotPassword(email: String): AuthResult<MessageResponse> =
        safeApiCall {
            val response = apiService.forgotPassword(ForgotPasswordRequest(email))
            if (response.isSuccessful) {
                AuthResult.Success(response.body()!!)
            } else {
                parseError(response)
            }
        }

    override suspend fun resendVerification(email: String): AuthResult<MessageResponse> =
        safeApiCall {
            val response = apiService.resendVerification(ResendVerificationRequest(email))
            if (response.isSuccessful) {
                AuthResult.Success(response.body()!!)
            } else {
                parseError(response)
            }
        }

    private suspend fun <T> safeApiCall(call: suspend () -> AuthResult<T>): AuthResult<T> {
        return try {
            call()
        } catch (e: IOException) {
            AuthResult.NetworkError
        }
    }

    private fun <T> parseError(response: Response<T>): AuthResult<Nothing> {
        val message = try {
            val errorBody = response.errorBody()?.string() ?: ""
            val json = JSONObject(errorBody)
            when {
                json.has("message") -> {
                    val msg = json.get("message")
                    if (msg is String) msg else (msg as? org.json.JSONArray)?.getString(0) ?: "Unknown error"
                }
                else -> "Unknown error"
            }
        } catch (e: Exception) {
            "Unknown error"
        }
        return AuthResult.Error(response.code(), message)
    }
}
