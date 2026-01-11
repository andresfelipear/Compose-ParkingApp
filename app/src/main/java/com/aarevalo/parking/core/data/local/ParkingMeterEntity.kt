package com.aarevalo.parking.core.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "parking_meters")
data class ParkingMeterEntity(
    @PrimaryKey val id: String,
    val latitude: Double,
    val longitude: Double,
    val centsPerHour: Int,
)

