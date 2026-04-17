package com.example.feedlypet.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.feedlypet.R
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.feedlypet.ui.AppSettingsViewModel
import com.example.feedlypet.ui.auth.EmailVerificationScreen
import com.example.feedlypet.ui.auth.ForgotPasswordScreen
import com.example.feedlypet.ui.auth.LoginScreen
import com.example.feedlypet.ui.auth.RegisterScreen
import com.example.feedlypet.ui.auth.ResetPasswordScreen
import com.example.feedlypet.ui.auth.VerifyEmailTokenScreen
import com.example.feedlypet.ui.devices.DevicesScreen
import com.example.feedlypet.ui.devices.detail.DeviceDetailScreen
import com.example.feedlypet.ui.devices.history.HistoryScreen
import com.example.feedlypet.ui.devices.schedules.SchedulesScreen
import com.example.feedlypet.ui.devices.statistics.StatisticsScreen
import com.example.feedlypet.ui.home.HomeScreen
import com.example.feedlypet.ui.home.HomeViewModel
import com.example.feedlypet.ui.notifications.NotificationsScreen
import com.example.feedlypet.ui.notifications.settings.NotificationSettingsScreen
import com.example.feedlypet.ui.onboarding.OnboardingScreen
import com.example.feedlypet.ui.pets.PetsScreen
import com.example.feedlypet.ui.profile.ProfileScreen

private data class BottomNavItem(
    val screen: Screen,
    val labelRes: Int,
    val icon: ImageVector
)

private val bottomNavItems = listOf(
    BottomNavItem(Screen.Home, R.string.nav_home, Icons.Default.Home),
    BottomNavItem(Screen.Pets, R.string.nav_pets, Icons.Default.PlayArrow),
    BottomNavItem(Screen.Devices, R.string.nav_devices, Icons.Default.Settings),
    BottomNavItem(Screen.Notifications, R.string.nav_alerts, Icons.Default.Notifications),
    BottomNavItem(Screen.Profile, R.string.nav_profile, Icons.Default.Person)
)

private val mainNavRoutes = setOf(
    Screen.Home.route,
    Screen.Pets.route,
    Screen.Devices.route,
    Screen.Notifications.route,
    Screen.Profile.route
)

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String,
    settingsViewModel: AppSettingsViewModel
) {
    val homeViewModel: HomeViewModel = hiltViewModel()
    val homeState by homeViewModel.uiState.collectAsStateWithLifecycle()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in mainNavRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(
                    currentRoute = currentRoute,
                    unreadCount = homeState.unreadNotifications,
                    onNavigate = { route ->
                        if (currentRoute != route) {
                            navController.navigate(route) {
                                popUpTo(Screen.Home.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            navigation(
                startDestination = Screen.Onboarding.route,
                route = GRAPH_ONBOARDING
            ) {
                composable(Screen.Onboarding.route) {
                    OnboardingScreen(
                        onNavigateToLogin = {
                            navController.navigate(GRAPH_AUTH) {
                                popUpTo(GRAPH_ONBOARDING) { inclusive = true }
                            }
                        },
                        onNavigateToRegister = {
                            navController.navigate(GRAPH_AUTH) {
                                popUpTo(GRAPH_ONBOARDING) { inclusive = true }
                            }
                            navController.navigate(Screen.Register.route)
                        }
                    )
                }
            }

            navigation(
                startDestination = Screen.Login.route,
                route = GRAPH_AUTH
            ) {
                composable(Screen.Login.route) {
                    LoginScreen(
                        onLoginSuccess = {
                            navController.navigate(GRAPH_MAIN) {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                        onNavigateToForgotPassword = { navController.navigate(Screen.ForgotPassword.route) }
                    )
                }

                composable(Screen.Register.route) {
                    RegisterScreen(
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
                        onNavigateToLogin = {
                            navController.navigate(GRAPH_AUTH) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }

                composable(
                    route = Screen.ResetPassword.route,
                    arguments = listOf(navArgument("token") { type = NavType.StringType }),
                    deepLinks = listOf(navDeepLink {
                        uriPattern = "feedlypet://reset-password?token={token}"
                    })
                ) { backStackEntry ->
                    val token = backStackEntry.arguments?.getString("token") ?: ""
                    if (token.isBlank()) {
                        navController.popBackStack()
                        return@composable
                    }
                    ResetPasswordScreen(
                        token = token,
                        onNavigateToLogin = {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        },
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(
                    route = Screen.VerifyEmailDeepLink.route,
                    arguments = listOf(navArgument("token") { type = NavType.StringType }),
                    deepLinks = listOf(navDeepLink {
                        uriPattern = "feedlypet://verify-email?token={token}"
                    })
                ) { backStackEntry ->
                    val token = backStackEntry.arguments?.getString("token") ?: ""
                    if (token.isBlank()) {
                        navController.popBackStack()
                        return@composable
                    }
                    VerifyEmailTokenScreen(
                        token = token,
                        onNavigateToLogin = {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }
            }

            navigation(
                startDestination = Screen.Home.route,
                route = GRAPH_MAIN
            ) {
                composable(Screen.Home.route) {
                    HomeScreen(
                        viewModel = homeViewModel,
                        onNavigateToNotifications = { navController.navigate(Screen.Notifications.route) }
                    )
                }

                composable(Screen.Pets.route) {
                    PetsScreen()
                }

                composable(Screen.Devices.route) {
                    DevicesScreen(
                        onNavigateToDetail = { deviceId ->
                            navController.navigate(Screen.DeviceDetail.createRoute(deviceId))
                        }
                    )
                }

                composable(Screen.Notifications.route) {
                    NotificationsScreen(
                        onNavigateToSettings = { navController.navigate(Screen.NotificationSettings.route) }
                    )
                }

                composable(Screen.Profile.route) {
                    ProfileScreen(
                        settingsViewModel = settingsViewModel,
                        onLogout = {
                            navController.navigate(GRAPH_ONBOARDING) {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    )
                }

                composable(
                    route = Screen.DeviceDetail.route,
                    arguments = listOf(navArgument("deviceId") { type = NavType.StringType })
                ) {
                    DeviceDetailScreen(
                        onBack = { navController.popBackStack() },
                        onNavigateToSchedules = { deviceId ->
                            navController.navigate(Screen.ScheduleList.createRoute(deviceId))
                        },
                        onNavigateToHistory = { deviceId ->
                            navController.navigate(Screen.FeedingHistory.createRoute(deviceId))
                        },
                        onNavigateToStatistics = { deviceId ->
                            navController.navigate(Screen.Statistics.createRoute(deviceId))
                        }
                    )
                }

                composable(
                    route = Screen.ScheduleList.route,
                    arguments = listOf(navArgument("deviceId") { type = NavType.StringType })
                ) {
                    SchedulesScreen(onBack = { navController.popBackStack() })
                }

                composable(
                    route = Screen.FeedingHistory.route,
                    arguments = listOf(navArgument("deviceId") { type = NavType.StringType })
                ) {
                    HistoryScreen(onBack = { navController.popBackStack() })
                }

                composable(
                    route = Screen.Statistics.route,
                    arguments = listOf(navArgument("deviceId") { type = NavType.StringType })
                ) {
                    StatisticsScreen(onBack = { navController.popBackStack() })
                }

                composable(Screen.NotificationSettings.route) {
                    NotificationSettingsScreen(onBack = { navController.popBackStack() })
                }
            }
        }
    }
}

@Composable
private fun BottomNavigationBar(
    currentRoute: String?,
    unreadCount: Int,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = Dp(0f)
    ) {
        bottomNavItems.forEach { item ->
            val selected = currentRoute == item.screen.route
            NavigationBarItem(
                selected = selected,
                onClick = { onNavigate(item.screen.route) },
                icon = {
                    val label = stringResource(item.labelRes)
                    if (item.screen == Screen.Notifications && unreadCount > 0) {
                        BadgedBox(badge = {
                            Badge(containerColor = MaterialTheme.colorScheme.primary) {
                                Text(
                                    if (unreadCount > 99) "99+" else unreadCount.toString(),
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }) {
                            Icon(item.icon, contentDescription = label)
                        }
                    } else {
                        Icon(item.icon, contentDescription = label)
                    }
                },
                label = { Text(stringResource(item.labelRes)) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
