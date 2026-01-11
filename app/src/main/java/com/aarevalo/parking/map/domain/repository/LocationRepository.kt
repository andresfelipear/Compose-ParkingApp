package com.aarevalo.parking.map.domain.repository

import com.aarevalo.parking.core.domain.util.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for location-related operations.
 */
interface LocationRepository {

    /**
     * Gets the current user location.
     * @return Flow emitting the user's current latitude and longitude
     */
    fun getCurrentLocation(): Flow<Resource<Pair<Double, Double>>>

    /**
     * Checks if location permission is granted.
     */
    fun hasLocationPermission(): Boolean

    /**
     * Checks if location services are enabled on the device.
     */
    fun isLocationEnabled(): Boolean
}
