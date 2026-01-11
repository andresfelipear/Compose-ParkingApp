package com.aarevalo.parking.core.data.remote.dto

import com.aarevalo.parking.core.domain.model.ParkingMeter
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Data Transfer Object for parking meter API responses.
 * Maps to the Vancouver Open Data API response format.
 */
@JsonClass(generateAdapter = true)
data class ParkingMeterDto(
    @Json(name = "meterid")
    val meterId: String?,
    @Json(name = "meterhead")
    val meterHead: String?,
    @Json(name = "r_mf_9a_6p")
    val rateMf9a6p: Double?,
    @Json(name = "r_mf_6p_10")
    val rateMf6p10: Double?,
    @Json(name = "r_sa_9a_6p")
    val rateSa9a6p: Double?,
    @Json(name = "r_sa_6p_10")
    val rateSa6p10: Double?,
    @Json(name = "r_su_9a_6p")
    val rateSu9a6p: Double?,
    @Json(name = "r_su_6p_10")
    val rateSu6p10: Double?,
    @Json(name = "rate_misc")
    val rateMisc: String?,
    @Json(name = "time_misc")
    val timeMisc: String?,
    @Json(name = "t_mf_9a_6p")
    val timeMf9a6p: Int?,
    @Json(name = "t_mf_6p_10")
    val timeMf6p10: Int?,
    @Json(name = "t_sa_9a_6p")
    val timeSa9a6p: Int?,
    @Json(name = "t_sa_6p_10")
    val timeSa6p10: Int?,
    @Json(name = "t_su_9a_6p")
    val timeSu9a6p: Int?,
    @Json(name = "t_su_6p_10")
    val timeSu6p10: Int?,
    @Json(name = "time_in_effect")
    val timeInEffect: String?,
    @Json(name = "credit_card")
    val creditCard: String?,
    @Json(name = "pay_phone")
    val payPhone: String?,
    @Json(name = "geo_local_area")
    val geoLocalArea: String?,
    @Json(name = "geom")
    val geom: GeomDto?
) {
    fun toDomainModel(): ParkingMeter? {
        val coordinates = geom?.geometry?.coordinates
        if (meterId == null || coordinates == null || coordinates.size < 2) {
            return null
        }

        return ParkingMeter(
            meterId = meterId,
            meterHead = meterHead ?: "Unknown",
            rateArea = geoLocalArea ?: "Unknown",
            latitude = coordinates[1],
            longitude = coordinates[0],
            streetNum = 0, // Not directly available in this API format
            streetSide = "",
            streetName = geoLocalArea ?: "Unknown",
            timeLimit = timeMf9a6p ?: timeSa9a6p ?: timeSu9a6p ?: 120,
            rateDays = "Mon-Sun",
            rateStart = "9:00 AM",
            rateEnd = "10:00 PM",
            rate = rateMf9a6p ?: rateSa9a6p ?: rateSu9a6p ?: 0.0,
            payByPhone = payPhone?.equals("Yes", ignoreCase = true) ?: false
        )
    }
}

@JsonClass(generateAdapter = true)
data class GeomDto(
    @Json(name = "geometry")
    val geometry: GeometryDto?
)

@JsonClass(generateAdapter = true)
data class GeometryDto(
    @Json(name = "type")
    val type: String?,
    @Json(name = "coordinates")
    val coordinates: List<Double>?
)

@JsonClass(generateAdapter = true)
data class ParkingMeterResponseDto(
    @Json(name = "total_count")
    val totalCount: Int?,
    @Json(name = "results")
    val results: List<ParkingMeterDto>?
)
