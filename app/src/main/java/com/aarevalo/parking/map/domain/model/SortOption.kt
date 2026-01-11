package com.aarevalo.parking.map.domain.model

/**
 * Enum representing the available sorting options for parking meters.
 */
enum class SortOption(val displayName: String) {
    RATE_LOW_TO_HIGH("Price: Low to High"),
    RATE_HIGH_TO_LOW("Price: High to Low"),
    DISTANCE("Distance"),
    TIME_LIMIT("Time Limit")
}
