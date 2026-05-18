package com.example.feedlypet.ui

import android.app.Activity
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.ViewModel
import com.example.feedlypet.data.local.TokenManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class AppSettingsViewModel @Inject constructor(
    private val tokenManager: TokenManager
) : ViewModel() {

    // null = follow system, true/false = user override
    private val _isDarkTheme = MutableStateFlow(tokenManager.getUserTheme())
    val isDarkTheme: StateFlow<Boolean?> = _isDarkTheme

    private val _language = MutableStateFlow(tokenManager.getLanguage())
    val language: StateFlow<String> = _language

    fun setDarkTheme(dark: Boolean) {
        _isDarkTheme.value = dark
        tokenManager.saveTheme(dark)
    }

    fun setLanguage(lang: String) {
        if (_language.value == lang) return
        _language.value = lang
        tokenManager.saveLanguage(lang)
        val localeList = LocaleListCompat.forLanguageTags(lang)
        AppCompatDelegate.setApplicationLocales(localeList)
    }

    fun applyLocaleIfNeeded(activity: Activity, lang: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            val currentLang = activity.resources.configuration.locales[0].language
            if (currentLang != lang) {
                activity.recreate()
            }
        }
    }
}
