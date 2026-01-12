package com.aarevalo.parking.map.presentation

import com.aarevalo.parking.core.domain.model.ParkingMeter
import com.aarevalo.parking.map.domain.model.MapViewState
import com.aarevalo.parking.map.domain.model.SortOption

/**
 * UI State for the Map screen.
 * Note: Errors are handled through MapScreenEvent, not stored in state.
 */
data class MapUiState(
    val parkingMeters: List<ParkingMeter> = emptyList(),
    val filteredMeters: List<ParkingMeter> = emptyList(),
    val selectedMeter: ParkingMeter? = null,
    val mapViewState: MapViewState = MapViewState(),
    val sortOption: SortOption = SortOption.RATE_LOW_TO_HIGH,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val showSortDialog: Boolean = false,
    val showMeterDetails: Boolean = false,
    val isListView: Boolean = false
)
