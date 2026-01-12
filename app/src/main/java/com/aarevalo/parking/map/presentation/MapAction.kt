package com.aarevalo.parking.map.presentation

import com.aarevalo.parking.core.domain.model.ParkingMeter
import com.aarevalo.parking.map.domain.model.SortOption

/**
 * User actions that can be performed on the Map screen.
 */
sealed interface MapAction {
    data class OnSearchQueryChanged(val query: String) : MapAction
    data class OnSortOptionSelected(val sortOption: SortOption) : MapAction
    data class OnMeterSelected(val meter: ParkingMeter) : MapAction
    data class OnMapCameraChanged(val latitude: Double, val longitude: Double, val zoom: Float) : MapAction
    data object OnToggleSortDialog : MapAction
    data object OnToggleViewMode : MapAction
    data object OnDismissMeterDetails : MapAction
    data object OnRefreshData : MapAction
    data object OnRequestLocationPermission : MapAction
    data object OnCenterOnUserLocation : MapAction
    data object OnLocationPermissionGranted : MapAction
}
