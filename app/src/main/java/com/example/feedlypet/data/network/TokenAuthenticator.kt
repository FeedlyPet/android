package com.example.feedlypet.data.network

import com.example.feedlypet.data.local.TokenManager
import com.example.feedlypet.data.network.model.RefreshTokenRequest
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenAuthenticator @Inject constructor(
    private val tokenManager: TokenManager,
    private val authApiService: AuthApiService
) : Authenticator {

    @Synchronized
    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) return null

        val refreshToken = tokenManager.getRefreshToken() ?: run {
            tokenManager.clearTokensAndForceLogout()
            return null
        }

        val currentToken = tokenManager.getAccessToken()
        val requestToken = response.request.header("Authorization")?.removePrefix("Bearer ")
        if (currentToken != null && currentToken != requestToken) {
            return response.request.newBuilder()
                .header("Authorization", "Bearer $currentToken")
                .build()
        }

        val newTokens = runBlocking {
            try {
                authApiService.refresh(RefreshTokenRequest(refreshToken))
            } catch (e: Exception) {
                null
            }
        }

        return if (newTokens != null && newTokens.isSuccessful) {
            val body = newTokens.body() ?: run {
                tokenManager.clearTokens()
                return null
            }
            tokenManager.saveTokens(body.accessToken, body.refreshToken)
            response.request.newBuilder()
                .header("Authorization", "Bearer ${body.accessToken}")
                .build()
        } else {
            tokenManager.clearTokensAndForceLogout()
            null
        }
    }

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}
