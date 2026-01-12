package com.aarevalo.parking.map.presentation

import com.aarevalo.parking.core.presentation.util.UiText

/**
 * One-time events emitted by MapViewModel to be handled by the UI.
 */
sealed interface MapScreenEvent {
    data class Error(val message: UiText) : MapScreenEvent
    data object RequestLocationPermission : MapScreenEvent
    data class AnimateCameraToLocation(val latitude: Double, val longitude: Double) : MapScreenEvent
}
