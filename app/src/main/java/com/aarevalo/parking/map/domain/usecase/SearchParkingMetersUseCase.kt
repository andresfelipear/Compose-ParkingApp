package com.aarevalo.parking.map.domain.usecase

import com.aarevalo.parking.core.data.repository.ParkingMeterRepository
import com.aarevalo.parking.core.domain.model.ParkingMeter
import com.aarevalo.parking.core.domain.util.Resource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for searching parking meters by query string.
 */
class SearchParkingMetersUseCase @Inject constructor(
    private val parkingMeterRepository: ParkingMeterRepository
) {
    operator fun invoke(query: String): Flow<Resource<List<ParkingMeter>>> {
        return parkingMeterRepository.searchParkingMeters(query.trim())
    }
}
