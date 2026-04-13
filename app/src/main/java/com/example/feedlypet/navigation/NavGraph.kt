package com.example.feedlypet.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.feedlypet.data.local.TokenDataStore
import com.example.feedlypet.data.network.NetworkModule
import com.example.feedlypet.data.repository.AuthRepositoryImpl
import com.example.feedlypet.ui.auth.EmailVerificationScreen
import com.example.feedlypet.ui.auth.ForgotPasswordScreen
import com.example.feedlypet.ui.auth.LoginScreen
import com.example.feedlypet.ui.auth.RegisterScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String,
    tokenDataStore: TokenDataStore
) {
    val repository = AuthRepositoryImpl(NetworkModule.authApiService, tokenDataStore)

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Login.route) {
            LoginScreen(
                repository = repository,
                onLoginSuccess = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                repository = repository,
                onRegisterSuccess = { email ->
                    navController.navigate(Screen.EmailVerification.createRoute(email)) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = { navController.popBackStack() }
            )
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPasswordScreen(
                repository = repository,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.EmailVerification.route,
            arguments = listOf(navArgument("email") { type = NavType.StringType })
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            EmailVerificationScreen(
                email = email,
                repository = repository,
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    androidx.compose.material3.Text("Home Screen — Coming soon!")
                }
            }
        }
    }
}
