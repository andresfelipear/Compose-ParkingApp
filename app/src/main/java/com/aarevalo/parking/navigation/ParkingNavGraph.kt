package com.aarevalo.parking.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aarevalo.parking.authentication.domain.model.AuthState
import com.aarevalo.parking.authentication.presentation.forgotpassword.ForgotPasswordScreenRoot
import com.aarevalo.parking.authentication.presentation.login.LoginScreenRoot
import com.aarevalo.parking.authentication.presentation.signup.SignUpScreenRoot
import com.aarevalo.parking.map.presentation.MapScreenRoot

/**
 * Main navigation graph for the Parking app.
 */
@Composable
fun ParkingNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthStateViewModel = hiltViewModel()
) {
    val authState by authViewModel.authState.collectAsStateWithLifecycle()

    // Determine start destination based on auth state
    val startDestination = when (authState) {
        is AuthState.Authenticated -> Screen.Map.route
        else -> Screen.Login.route
    }

    // Handle auth state changes for navigation
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> {
                // Navigate to map if currently on auth screens
                val currentRoute = navController.currentDestination?.route
                if (currentRoute in listOf(
                        Screen.Login.route,
                        Screen.SignUp.route,
                        Screen.ForgotPassword.route
                    )
                ) {
                    navController.navigate(Screen.Map.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }

            is AuthState.Unauthenticated -> {
                // Navigate to login if currently on protected screens
                val currentRoute = navController.currentDestination?.route
                if (currentRoute !in listOf(
                        Screen.Login.route,
                        Screen.SignUp.route,
                        Screen.ForgotPassword.route
                    ) && currentRoute != null
                ) {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            }

            else -> { /* Loading or Error - do nothing */ }
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        // Authentication Flow
        composable(route = Screen.Login.route) {
            LoginScreenRoot(
                onNavigateToSignUp = {
                    navController.navigate(Screen.SignUp.route)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Screen.ForgotPassword.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.Map.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(route = Screen.SignUp.route) {
            SignUpScreenRoot(
                onNavigateToLogin = {
                    navController.popBackStack()
                },
                onSignUpSuccess = {
                    navController.navigate(Screen.Map.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(route = Screen.ForgotPassword.route) {
            ForgotPasswordScreenRoot(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // Main Flow
        composable(route = Screen.Map.route) {
            MapScreenRoot(
                onMeterClick = { meter ->
                    // Optional: Navigate to meter details
                    // navController.navigate(Screen.MeterDetails.createRoute(meter.meterId))
                }
            )
        }

        // Meter Details
        composable(route = Screen.MeterDetails.route) { backStackEntry ->
            val meterId = backStackEntry.arguments?.getString("meterId")
            // TODO: Implement MeterDetailsScreen
        }

        // Settings
        composable(route = Screen.Settings.route) {
            // TODO: Implement SettingsScreen
        }

        composable(route = Screen.Profile.route) {
            // TODO: Implement ProfileScreen
        }
    }
}
