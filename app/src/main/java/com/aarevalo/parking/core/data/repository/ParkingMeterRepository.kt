package com.aarevalo.parking.core.data.repository

import com.aarevalo.parking.core.domain.model.ParkingMeter
import com.aarevalo.parking.core.domain.util.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for parking meter data operations.
 * Abstracts the data sources from the domain layer.
 */
interface ParkingMeterRepository {

    /**
     * Gets all parking meters sorted by rate in ascending order.
     */
    fun getParkingMetersSortedByRate(ascending: Boolean = true): Flow<Resource<List<ParkingMeter>>>

    /**
     * Gets a single parking meter by its ID.
     */
    suspend fun getParkingMeterById(meterId: String): Resource<ParkingMeter>

    /**
     * Searches parking meters by query string.
     */
    fun searchParkingMeters(query: String): Flow<Resource<List<ParkingMeter>>>

    /**
     * Gets parking meters within a specific rate range.
     */
    fun getParkingMetersByRateRange(
        minRate: Double,
        maxRate: Double
    ): Flow<Resource<List<ParkingMeter>>>

    /**
     * Gets parking meters within geographic bounds.
     */
    fun getParkingMetersInBounds(
        minLat: Double,
        maxLat: Double,
        minLng: Double,
        maxLng: Double
    ): Flow<Resource<List<ParkingMeter>>>

    /**
     * Refreshes parking meters from the remote data source.
     */
    suspend fun refreshParkingMeters(): Resource<Unit>

    /**
     * Gets the count of cached parking meters.
     */
    suspend fun getCachedParkingMeterCount(): Int
}
