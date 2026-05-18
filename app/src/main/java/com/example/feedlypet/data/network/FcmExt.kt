package com.example.feedlypet.data.network

import com.example.feedlypet.data.repository.AuthRepository

suspend fun AuthRepository.tryRegisterFcmToken(fcmToken: String) {
    try {
        registerFcmToken(fcmToken)
    } catch (e: Exception) {
        // best-effort, ignore failures
    }
}
