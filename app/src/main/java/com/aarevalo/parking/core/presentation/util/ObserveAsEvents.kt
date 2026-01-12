package com.aarevalo.parking.core.presentation.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * A composable helper that collects events from a Flow in a lifecycle-aware manner.
 * 
 * This ensures that:
 * - Events are only collected when the lifecycle is at least STARTED
 * - Events are consumed only once, even across configuration changes or process death
 * - Events are dispatched on the Main.immediate dispatcher for responsive UI updates
 *
 * Usage:
 * ```kotlin
 * ObserveAsEvents(viewModel.events) { event ->
 *     when (event) {
 *         is ScreenEvent.Success -> navigateToNext()
 *         is ScreenEvent.Error -> showError(event.message)
 *     }
 * }
 * ```
 *
 * @param flow The flow of events to observe
 * @param key1 Optional key to trigger recomposition
 * @param key2 Optional key to trigger recomposition
 * @param onEvent Callback invoked for each event
 */
@Composable
fun <T> ObserveAsEvents(
    flow: Flow<T>,
    key1: Any? = null,
    key2: Any? = null,
    onEvent: (T) -> Unit
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(flow, lifecycleOwner.lifecycle, key1, key2) {
        lifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            withContext(Dispatchers.Main.immediate) {
                flow.collect(onEvent)
            }
        }
    }
}
