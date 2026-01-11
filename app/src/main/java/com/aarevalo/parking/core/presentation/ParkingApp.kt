package com.aarevalo.parking.core.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.aarevalo.parking.feature.authentication.presentation.AuthScreen
import com.aarevalo.parking.feature.map.presentation.MapScreen

@Composable
fun ParkingApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.Auth,
    ) {
        composable(Routes.Auth) { AuthScreen(onContinue = { navController.navigate(Routes.Map) }) }
        composable(Routes.Map) { MapScreen() }
    }
}

object Routes {
    const val Auth = "auth"
    const val Map = "map"
}

