package com.example.feedlypet

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.feedlypet.data.local.TokenManager
import com.example.feedlypet.data.repository.AuthRepository
import com.example.feedlypet.navigation.GRAPH_MAIN
import com.example.feedlypet.navigation.GRAPH_ONBOARDING
import com.example.feedlypet.navigation.NavGraph
import com.example.feedlypet.ui.AppSettingsViewModel
import com.example.feedlypet.ui.theme.FeedlyPetTheme
import com.google.firebase.messaging.FirebaseMessaging
import androidx.compose.foundation.isSystemInDarkTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var tokenManager: TokenManager
    @Inject
    lateinit var authRepository: AuthRepository

    private val settingsViewModel: AppSettingsViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        createNotificationChannels()
        requestNotificationPermission()

        val isLoggedIn = tokenManager.isLoggedIn()
        val startDestination = if (isLoggedIn) GRAPH_MAIN else GRAPH_ONBOARDING

        if (isLoggedIn) {
            registerFcmToken()
        }

        lifecycleScope.launch {
            settingsViewModel.language.drop(1).collectLatest { lang ->
                settingsViewModel.applyLocaleIfNeeded(this@MainActivity, lang)
            }
        }

        setContent {
            val userTheme by settingsViewModel.isDarkTheme.collectAsStateWithLifecycle()
            val systemDark = isSystemInDarkTheme()
            FeedlyPetTheme(darkTheme = userTheme ?: systemDark) {
                val navController = rememberNavController()

                // Force-logout: navigate to onboarding when refresh token expires
                val isForceLoggedOut by tokenManager.isForceLoggedOut.collectAsStateWithLifecycle()
                if (isForceLoggedOut) {
                    androidx.compose.runtime.LaunchedEffect(Unit) {
                        tokenManager.resetForceLogout()
                        navController.navigate(GRAPH_ONBOARDING) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }

                NavGraph(
                    navController = navController,
                    startDestination = startDestination,
                    settingsViewModel = settingsViewModel
                )
            }
        }
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val notificationManager = getSystemService(NotificationManager::class.java)
        val channels = listOf(
            NotificationChannel("feeding", getString(R.string.channel_feeding_name), NotificationManager.IMPORTANCE_HIGH),
            NotificationChannel("device_status", getString(R.string.channel_device_status_name), NotificationManager.IMPORTANCE_DEFAULT),
            NotificationChannel("food_level", getString(R.string.channel_food_level_name), NotificationManager.IMPORTANCE_HIGH)
        )
        channels.forEach { notificationManager.createNotificationChannel(it) }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun registerFcmToken() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val token = FirebaseMessaging.getInstance().token.await()
                tokenManager.saveFcmToken(token)
                authRepository.registerFcmToken(token)
            } catch (_: Exception) {
            }
        }
    }
}
