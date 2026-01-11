package com.aarevalo.parking.feature.map.domain.model

data class ParkingMeter(
    val id: String,
    val latitude: Double,
    val longitude: Double,
    val centsPerHour: Int,
)

