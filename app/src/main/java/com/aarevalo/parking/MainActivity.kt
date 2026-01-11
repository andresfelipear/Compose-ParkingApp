package com.aarevalo.parking

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.aarevalo.parking.authentication.domain.model.AuthState
import com.aarevalo.parking.authentication.domain.repository.AuthRepository
import com.aarevalo.parking.navigation.ParkingNavGraph
import com.aarevalo.parking.ui.theme.ParkingTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Main entry point for the Parking app.
 * Annotated with @AndroidEntryPoint to enable Hilt injection.
 */
@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var authRepository: AuthRepository

    // Track whether we've determined the initial auth state
    private var isAuthStateReady by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen before calling super.onCreate()
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        // Keep splash screen visible until auth state is determined
        splashScreen.setKeepOnScreenCondition { !isAuthStateReady }

        // Check initial auth state
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Wait for the first emission of auth state
                val initialState = authRepository.authState.first { state ->
                    state !is AuthState.Loading
                }
                isAuthStateReady = true
            }
        }

        enableEdgeToEdge()

        setContent {
            ParkingTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Only show nav graph when auth state is ready
                    if (isAuthStateReady) {
                        ParkingNavGraph()
                    }
                }
            }
        }
    }
}
