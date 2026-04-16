package com.example.feedlypet.data.network

import com.example.feedlypet.domain.model.AuthResult
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Response

const val EMPTY_RESPONSE = "Empty response"

suspend fun <T> safeApiCall(call: suspend () -> AuthResult<T>): AuthResult<T> =
    try {
        call()
    } catch (e: Exception) {
        AuthResult.NetworkError
    }

fun <T> Response<T>.parseError(): AuthResult<Nothing> {
    val message = try {
        val json = JSONObject(errorBody()?.string() ?: "")
        when (val msg = json.get("message")) {
            is String -> msg
            is JSONArray -> msg.getString(0)
            else -> "Unknown error"
        }
    } catch (e: Exception) {
        "Unknown error"
    }
    return AuthResult.Error(code(), message)
}

fun <T> Response<T>.bodyOrError(): AuthResult<T> {
    val b = body()
    return if (b != null) AuthResult.Success(b) else AuthResult.Error(code(), EMPTY_RESPONSE)
}
