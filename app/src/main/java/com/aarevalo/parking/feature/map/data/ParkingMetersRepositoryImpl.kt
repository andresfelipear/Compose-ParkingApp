package com.aarevalo.parking.feature.map.data

import com.aarevalo.parking.core.data.local.ParkingMeterDao
import com.aarevalo.parking.feature.map.domain.ParkingMetersRepository
import com.aarevalo.parking.feature.map.domain.model.ParkingMeter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ParkingMetersRepositoryImpl @Inject constructor(
    private val dao: ParkingMeterDao,
) : ParkingMetersRepository {
    override fun observeMetersSortedByCostDesc(): Flow<List<ParkingMeter>> =
        dao.observeMetersSortedByCostDesc()
            .map { entities ->
                entities.map { e ->
                    ParkingMeter(
                        id = e.id,
                        latitude = e.latitude,
                        longitude = e.longitude,
                        centsPerHour = e.centsPerHour,
                    )
                }
            }
}

