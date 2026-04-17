package com.example.feedlypet.navigation

sealed class Screen(val route: String) {
    // Onboarding
    object Onboarding : Screen("onboarding")

    // Auth
    object Login : Screen("login")
    object Register : Screen("register")
    object ForgotPassword : Screen("forgot_password")
    object EmailVerification : Screen("email_verification/{email}") {
        fun createRoute(email: String) = "email_verification/$email"
    }

    object ResetPassword : Screen("reset_password/{token}") {
        fun createRoute(token: String) = "reset_password/$token"
    }

    object VerifyEmailDeepLink : Screen("verify_email_token/{token}") {
        fun createRoute(token: String) = "verify_email_token/$token"
    }

    // Main — bottom nav
    object Home : Screen("home")
    object Pets : Screen("pets")
    object Devices : Screen("devices")
    object Notifications : Screen("notifications")
    object Profile : Screen("profile")

    // Device sub-screens
    object DeviceDetail : Screen("device_detail/{deviceId}") {
        fun createRoute(deviceId: String) = "device_detail/$deviceId"
    }

    object ScheduleList : Screen("schedule_list/{deviceId}") {
        fun createRoute(deviceId: String) = "schedule_list/$deviceId"
    }

    object FeedingHistory : Screen("feeding_history/{deviceId}") {
        fun createRoute(deviceId: String) = "feeding_history/$deviceId"
    }

    object Statistics : Screen("statistics/{deviceId}") {
        fun createRoute(deviceId: String) = "statistics/$deviceId"
    }

    // Notification sub-screens
    object NotificationSettings : Screen("notification_settings")
}

const val GRAPH_ONBOARDING = "onboarding_graph"
const val GRAPH_AUTH = "auth_graph"
const val GRAPH_MAIN = "main_graph"
