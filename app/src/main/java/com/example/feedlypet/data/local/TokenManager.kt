package com.example.feedlypet.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager @Inject constructor(@ApplicationContext context: Context) {

    private val _isForceLoggedOut = MutableStateFlow(false)
    val isForceLoggedOut: StateFlow<Boolean> = _isForceLoggedOut.asStateFlow()

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        "feedlypet_secure_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveTokens(accessToken: String, refreshToken: String) {
        prefs.edit()
            .putString(KEY_ACCESS_TOKEN, accessToken)
            .putString(KEY_REFRESH_TOKEN, refreshToken)
            .apply()
    }

    fun saveUser(id: String, email: String, name: String) {
        prefs.edit()
            .putString(KEY_USER_ID, id)
            .putString(KEY_USER_EMAIL, email)
            .putString(KEY_USER_NAME, name)
            .apply()
    }

    fun getAccessToken(): String? = prefs.getString(KEY_ACCESS_TOKEN, null)
    fun getRefreshToken(): String? = prefs.getString(KEY_REFRESH_TOKEN, null)
    fun getUserId(): String? = prefs.getString(KEY_USER_ID, null)
    fun getUserEmail(): String? = prefs.getString(KEY_USER_EMAIL, null)
    fun getUserName(): String? = prefs.getString(KEY_USER_NAME, null)

    fun isLoggedIn(): Boolean = !getAccessToken().isNullOrBlank()

    fun saveFcmToken(token: String) {
        prefs.edit().putString(KEY_FCM_TOKEN, token).apply()
    }

    fun getFcmToken(): String? = prefs.getString(KEY_FCM_TOKEN, null)

    fun clearTokens() {
        val theme = getUserTheme()
        val language = getLanguage()
        prefs.edit().clear().apply()
        if (theme != null) saveTheme(theme)
        saveLanguage(language)
    }

    /** Called by TokenAuthenticator when refresh fails — clears tokens and signals forced logout. */
    fun clearTokensAndForceLogout() {
        clearTokens()
        _isForceLoggedOut.value = true
    }

    fun resetForceLogout() {
        _isForceLoggedOut.value = false
    }

    fun saveTheme(isDark: Boolean) {
        prefs.edit()
            .putBoolean(KEY_THEME_DARK, isDark)
            .putBoolean(KEY_THEME_USER_SET, true)
            .apply()
    }

    // Returns null if user never set theme (follow system)
    fun getUserTheme(): Boolean? {
        if (!prefs.getBoolean(KEY_THEME_USER_SET, false)) return null
        return prefs.getBoolean(KEY_THEME_DARK, false)
    }

    fun saveLanguage(code: String) {
        prefs.edit().putString(KEY_LANGUAGE, code).apply()
    }

    fun getLanguage(): String = prefs.getString(KEY_LANGUAGE, "en") ?: "en"

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_FCM_TOKEN = "fcm_token"
        private const val KEY_THEME_DARK = "theme_dark"
        private const val KEY_THEME_USER_SET = "theme_user_set"
        private const val KEY_LANGUAGE = "language"
    }
}
