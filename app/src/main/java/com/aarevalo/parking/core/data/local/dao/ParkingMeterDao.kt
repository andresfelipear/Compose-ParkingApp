package com.aarevalo.parking.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.aarevalo.parking.core.data.local.entity.ParkingMeterEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for parking meter operations.
 */
@Dao
interface ParkingMeterDao {

    @Query("SELECT * FROM parking_meters ORDER BY rate ASC")
    fun getAllParkingMetersSortedByRate(): Flow<List<ParkingMeterEntity>>

    @Query("SELECT * FROM parking_meters ORDER BY rate DESC")
    fun getAllParkingMetersSortedByRateDescending(): Flow<List<ParkingMeterEntity>>

    @Query("SELECT * FROM parking_meters WHERE meterId = :meterId")
    suspend fun getParkingMeterById(meterId: String): ParkingMeterEntity?

    @Query("""
        SELECT * FROM parking_meters 
        WHERE streetName LIKE '%' || :query || '%' 
        OR meterHead LIKE '%' || :query || '%'
        ORDER BY rate ASC
    """)
    fun searchParkingMeters(query: String): Flow<List<ParkingMeterEntity>>

    @Query("""
        SELECT * FROM parking_meters 
        WHERE rate BETWEEN :minRate AND :maxRate
        ORDER BY rate ASC
    """)
    fun getParkingMetersByRateRange(minRate: Double, maxRate: Double): Flow<List<ParkingMeterEntity>>

    @Query("""
        SELECT * FROM parking_meters 
        WHERE latitude BETWEEN :minLat AND :maxLat
        AND longitude BETWEEN :minLng AND :maxLng
        ORDER BY rate ASC
    """)
    fun getParkingMetersInBounds(
        minLat: Double,
        maxLat: Double,
        minLng: Double,
        maxLng: Double
    ): Flow<List<ParkingMeterEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParkingMeters(parkingMeters: List<ParkingMeterEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParkingMeter(parkingMeter: ParkingMeterEntity)

    @Query("DELETE FROM parking_meters")
    suspend fun deleteAllParkingMeters()

    @Query("SELECT COUNT(*) FROM parking_meters")
    suspend fun getParkingMeterCount(): Int
}
