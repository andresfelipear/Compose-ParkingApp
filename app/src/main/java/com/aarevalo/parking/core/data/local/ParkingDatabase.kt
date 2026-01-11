package com.aarevalo.parking.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.aarevalo.parking.core.data.local.dao.ParkingMeterDao
import com.aarevalo.parking.core.data.local.entity.ParkingMeterEntity

/**
 * Room database for the Parking application.
 */
@Database(
    entities = [ParkingMeterEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ParkingDatabase : RoomDatabase() {

    abstract fun parkingMeterDao(): ParkingMeterDao

    companion object {
        const val DATABASE_NAME = "parking_database"
    }
}
