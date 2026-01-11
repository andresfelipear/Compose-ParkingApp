package com.aarevalo.parking.map.domain.usecase

import com.aarevalo.parking.core.data.repository.ParkingMeterRepository
import com.aarevalo.parking.core.domain.util.Resource
import javax.inject.Inject

/**
 * Use case for refreshing parking meters from the remote data source.
 */
class RefreshParkingMetersUseCase @Inject constructor(
    private val parkingMeterRepository: ParkingMeterRepository
) {
    suspend operator fun invoke(): Resource<Unit> {
        return parkingMeterRepository.refreshParkingMeters()
    }
}
