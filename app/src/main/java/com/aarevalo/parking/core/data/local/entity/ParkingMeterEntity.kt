package com.aarevalo.parking.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.aarevalo.parking.core.domain.model.ParkingMeter

/**
 * Room entity representing a parking meter in the local database.
 */
@Entity(tableName = "parking_meters")
data class ParkingMeterEntity(
    @PrimaryKey
    val meterId: String,
    val meterHead: String,
    val rateArea: String,
    val latitude: Double,
    val longitude: Double,
    val streetNum: Int,
    val streetSide: String,
    val streetName: String,
    val timeLimit: Int,
    val rateDays: String,
    val rateStart: String,
    val rateEnd: String,
    val rate: Double,
    val payByPhone: Boolean,
    val lastUpdated: Long = System.currentTimeMillis()
) {
    fun toDomainModel(): ParkingMeter {
        return ParkingMeter(
            meterId = meterId,
            meterHead = meterHead,
            rateArea = rateArea,
            latitude = latitude,
            longitude = longitude,
            streetNum = streetNum,
            streetSide = streetSide,
            streetName = streetName,
            timeLimit = timeLimit,
            rateDays = rateDays,
            rateStart = rateStart,
            rateEnd = rateEnd,
            rate = rate,
            payByPhone = payByPhone
        )
    }

    companion object {
        fun fromDomainModel(parkingMeter: ParkingMeter): ParkingMeterEntity {
            return ParkingMeterEntity(
                meterId = parkingMeter.meterId,
                meterHead = parkingMeter.meterHead,
                rateArea = parkingMeter.rateArea,
                latitude = parkingMeter.latitude,
                longitude = parkingMeter.longitude,
                streetNum = parkingMeter.streetNum,
                streetSide = parkingMeter.streetSide,
                streetName = parkingMeter.streetName,
                timeLimit = parkingMeter.timeLimit,
                rateDays = parkingMeter.rateDays,
                rateStart = parkingMeter.rateStart,
                rateEnd = parkingMeter.rateEnd,
                rate = parkingMeter.rate,
                payByPhone = parkingMeter.payByPhone
            )
        }
    }
}
