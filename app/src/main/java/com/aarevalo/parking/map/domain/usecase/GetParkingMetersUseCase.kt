package com.aarevalo.parking.map.domain.usecase

import com.aarevalo.parking.core.data.repository.ParkingMeterRepository
import com.aarevalo.parking.core.domain.model.ParkingMeter
import com.aarevalo.parking.core.domain.util.Resource
import com.aarevalo.parking.map.domain.model.SortOption
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Use case for getting parking meters with sorting and filtering options.
 */
class GetParkingMetersUseCase @Inject constructor(
    private val parkingMeterRepository: ParkingMeterRepository
) {
    operator fun invoke(
        sortOption: SortOption = SortOption.RATE_LOW_TO_HIGH,
        userLat: Double? = null,
        userLng: Double? = null
    ): Flow<Resource<List<ParkingMeter>>> {
        return when (sortOption) {
            SortOption.RATE_LOW_TO_HIGH -> {
                parkingMeterRepository.getParkingMetersSortedByRate(ascending = true)
            }

            SortOption.RATE_HIGH_TO_LOW -> {
                parkingMeterRepository.getParkingMetersSortedByRate(ascending = false)
            }

            SortOption.DISTANCE -> {
                if (userLat != null && userLng != null) {
                    parkingMeterRepository.getParkingMetersSortedByRate(ascending = true)
                        .map { resource ->
                            resource.map { meters ->
                                meters.sortedBy { meter ->
                                    calculateDistance(
                                        userLat, userLng,
                                        meter.latitude, meter.longitude
                                    )
                                }
                            }
                        }
                } else {
                    parkingMeterRepository.getParkingMetersSortedByRate(ascending = true)
                }
            }

            SortOption.TIME_LIMIT -> {
                parkingMeterRepository.getParkingMetersSortedByRate(ascending = true)
                    .map { resource ->
                        resource.map { meters ->
                            meters.sortedByDescending { it.timeLimit }
                        }
                    }
            }
        }
    }

    /**
     * Calculates the distance between two geographic coordinates using the Haversine formula.
     * @return Distance in kilometers
     */
    private fun calculateDistance(
        lat1: Double, lng1: Double,
        lat2: Double, lng2: Double
    ): Double {
        val earthRadius = 6371.0 // Earth's radius in kilometers

        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)

        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLng / 2) * sin(dLng / 2)

        val c = 2 * atan2(sqrt(a), sqrt(1 - a))

        return earthRadius * c
    }
}
