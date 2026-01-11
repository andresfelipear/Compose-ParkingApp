package com.aarevalo.parking.core.data.remote.api

import com.aarevalo.parking.core.data.remote.dto.ParkingMeterResponseDto
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit API interface for Vancouver Open Data parking meter endpoints.
 * Base URL: https://opendata.vancouver.ca/api/explore/v2.1/
 */
interface ParkingMeterApi {

    /**
     * Fetches parking meters from the Vancouver Open Data API.
     *
     * @param limit Maximum number of results to return
     * @param offset Offset for pagination
     * @param orderBy Field to order results by (e.g., "r_mf_9a_6p" for rate)
     * @return ParkingMeterResponseDto containing the list of parking meters
     */
    @GET("catalog/datasets/parking-meters/records")
    suspend fun getParkingMeters(
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0,
        @Query("order_by") orderBy: String? = null
    ): ParkingMeterResponseDto

    /**
     * Fetches parking meters within a specific geographic area.
     *
     * @param limit Maximum number of results to return
     * @param offset Offset for pagination
     * @param where SQL-like where clause for filtering
     * @return ParkingMeterResponseDto containing the filtered list of parking meters
     */
    @GET("catalog/datasets/parking-meters/records")
    suspend fun getParkingMetersWithFilter(
        @Query("limit") limit: Int = 100,
        @Query("offset") offset: Int = 0,
        @Query("where") where: String? = null,
        @Query("order_by") orderBy: String? = null
    ): ParkingMeterResponseDto

    companion object {
        const val DEFAULT_LIMIT = 100
        const val MAX_LIMIT = 1000
        const val ORDER_BY_RATE_ASC = "r_mf_9a_6p ASC"
        const val ORDER_BY_RATE_DESC = "r_mf_9a_6p DESC"
    }
}
