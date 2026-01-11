package com.aarevalo.parking.navigation

/**
 * Sealed class representing all navigation destinations in the app.
 */
sealed class Screen(val route: String) {
    // Authentication
    data object Login : Screen("login")
    data object SignUp : Screen("signup")
    data object ForgotPassword : Screen("forgot_password")

    // Main
    data object Map : Screen("map")
    data object MeterDetails : Screen("meter_details/{meterId}") {
        fun createRoute(meterId: String) = "meter_details/$meterId"
    }

    // Settings
    data object Settings : Screen("settings")
    data object Profile : Screen("profile")
}
