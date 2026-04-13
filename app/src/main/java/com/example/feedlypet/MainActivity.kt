package com.example.feedlypet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.compose.rememberNavController
import com.example.feedlypet.data.local.TokenDataStore
import com.example.feedlypet.navigation.NavGraph
import com.example.feedlypet.navigation.Screen
import com.example.feedlypet.ui.theme.FeedlyPetTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tokenDataStore = TokenDataStore(applicationContext)
        enableEdgeToEdge()
        setContent {
            FeedlyPetTheme {
                val token by tokenDataStore.accessToken.collectAsState(initial = null)
                // null = still loading, "" or value = determined
                val startDestination = if (token != null && token!!.isNotBlank()) {
                    Screen.Home.route
                } else {
                    Screen.Login.route
                }
                val navController = rememberNavController()
                NavGraph(
                    navController = navController,
                    startDestination = startDestination,
                    tokenDataStore = tokenDataStore
                )
            }
        }
    }
}
