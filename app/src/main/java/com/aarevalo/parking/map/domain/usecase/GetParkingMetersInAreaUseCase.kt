package com.aarevalo.parking.map.domain.usecase

import com.aarevalo.parking.core.data.repository.ParkingMeterRepository
import com.aarevalo.parking.core.domain.model.ParkingMeter
import com.aarevalo.parking.core.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting parking meters within a specific geographic area.
 */
class GetParkingMetersInAreaUseCase @Inject constructor(
    private val parkingMeterRepository: ParkingMeterRepository
) {
    operator fun invoke(
        centerLat: Double,
        centerLng: Double,
        radiusKm: Double = DEFAULT_RADIUS_KM
    ): Flow<Resource<List<ParkingMeter>>> {
        // Calculate bounds from center point and radius
        val latOffset = radiusKm / LAT_KM_RATIO
        val lngOffset = radiusKm / (LNG_KM_RATIO * kotlin.math.cos(Math.toRadians(centerLat)))

        return parkingMeterRepository.getParkingMetersInBounds(
            minLat = centerLat - latOffset,
            maxLat = centerLat + latOffset,
            minLng = centerLng - lngOffset,
            maxLng = centerLng + lngOffset
        )
    }

    companion object {
        private const val DEFAULT_RADIUS_KM = 1.0
        private const val LAT_KM_RATIO = 111.0 // Approximate km per degree latitude
        private const val LNG_KM_RATIO = 111.0 // Approximate km per degree longitude at equator
    }
}
