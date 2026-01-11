package com.aarevalo.parking.map.domain.model

import com.aarevalo.parking.core.domain.model.ParkingMeter

/**
 * Represents the current state of the map view.
 */
data class MapViewState(
    val userLatitude: Double = VANCOUVER_DEFAULT_LAT,
    val userLongitude: Double = VANCOUVER_DEFAULT_LNG,
    val zoomLevel: Float = DEFAULT_ZOOM,
    val selectedMeter: ParkingMeter? = null,
    val isUserLocationEnabled: Boolean = false
) {
    companion object {
        // Downtown Vancouver coordinates
        const val VANCOUVER_DEFAULT_LAT = 49.2827
        const val VANCOUVER_DEFAULT_LNG = -123.1207
        const val DEFAULT_ZOOM = 14f
        const val MIN_ZOOM = 10f
        const val MAX_ZOOM = 20f
    }
}
