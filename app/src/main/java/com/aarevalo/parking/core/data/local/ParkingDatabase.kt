package com.aarevalo.parking.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        ParkingMeterEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class ParkingDatabase : RoomDatabase() {
    abstract fun parkingMeterDao(): ParkingMeterDao
}

