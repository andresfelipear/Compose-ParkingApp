package com.aarevalo.parking.core.domain.model

/**
 * Domain model representing a parking meter in Vancouver.
 *
 * @property meterId Unique identifier for the parking meter
 * @property meterHead Type of meter head (e.g., "Single", "Twin")
 * @property rateArea The rate area designation
 * @property latitude Geographic latitude coordinate
 * @property longitude Geographic longitude coordinate
 * @property streetNum Street number where the meter is located
 * @property streetSide Side of the street (e.g., "N", "S", "E", "W")
 * @property streetName Name of the street
 * @property timeLimit Maximum parking time in minutes
 * @property rateDays Days when rates apply
 * @property rateStart Start time for rate period (e.g., "9:00 AM")
 * @property rateEnd End time for rate period (e.g., "10:00 PM")
 * @property rate Hourly rate in dollars
 * @property payByPhone Whether pay-by-phone is available
 */
data class ParkingMeter(
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
    val payByPhone: Boolean
)
