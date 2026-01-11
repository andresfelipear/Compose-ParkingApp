package com.aarevalo.parking.map.presentation

import com.aarevalo.parking.core.domain.model.ParkingMeter
import com.aarevalo.parking.map.domain.model.SortOption

/**
 * Events that can be triggered from the Map screen.
 */
sealed class MapEvent {
    data class SearchQueryChanged(val query: String) : MapEvent()
    data class SortOptionSelected(val sortOption: SortOption) : MapEvent()
    data class MeterSelected(val meter: ParkingMeter) : MapEvent()
    data class MapCameraChanged(val latitude: Double, val longitude: Double, val zoom: Float) : MapEvent()
    data object ToggleSortDialog : MapEvent()
    data object ToggleViewMode : MapEvent()
    data object DismissMeterDetails : MapEvent()
    data object RefreshData : MapEvent()
    data object RequestLocationPermission : MapEvent()
    data object CenterOnUserLocation : MapEvent()
    data object ClearError : MapEvent()
}
