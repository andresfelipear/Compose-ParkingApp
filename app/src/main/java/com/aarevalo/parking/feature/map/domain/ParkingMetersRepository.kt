package com.aarevalo.parking.feature.map.domain

import com.aarevalo.parking.feature.map.domain.model.ParkingMeter
import kotlinx.coroutines.flow.Flow

interface ParkingMetersRepository {
    fun observeMetersSortedByCostDesc(): Flow<List<ParkingMeter>>
}

