package com.aarevalo.parking.map.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aarevalo.parking.R
import com.aarevalo.parking.core.domain.model.ParkingMeter
import com.aarevalo.parking.core.domain.util.Resource
import com.aarevalo.parking.core.presentation.util.UiText
import com.aarevalo.parking.map.domain.model.MapViewState
import com.aarevalo.parking.map.domain.model.SortOption
import com.aarevalo.parking.map.domain.repository.LocationRepository
import com.aarevalo.parking.map.domain.usecase.GetParkingMetersUseCase
import com.aarevalo.parking.map.domain.usecase.RefreshParkingMetersUseCase
import com.aarevalo.parking.map.domain.usecase.SearchParkingMetersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val getParkingMetersUseCase: GetParkingMetersUseCase,
    private val searchParkingMetersUseCase: SearchParkingMetersUseCase,
    private val refreshParkingMetersUseCase: RefreshParkingMetersUseCase,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MapUiState())
    val state = _state.asStateFlow()

    private val eventChannel = Channel<MapScreenEvent>()
    val events = eventChannel.receiveAsFlow()

    private var searchJob: Job? = null

    init {
        loadParkingMeters()
        checkAndLoadUserLocation()
    }

    fun onAction(action: MapAction) {
        when (action) {
            is MapAction.OnSearchQueryChanged -> {
                _state.update { it.copy(searchQuery = action.query) }
                searchWithDebounce(action.query)
            }

            is MapAction.OnSortOptionSelected -> {
                _state.update { it.copy(sortOption = action.sortOption, showSortDialog = false) }
                loadParkingMeters()
            }

            is MapAction.OnMeterSelected -> {
                _state.update {
                    it.copy(
                        selectedMeter = action.meter,
                        showMeterDetails = true,
                        mapViewState = it.mapViewState.copy(
                            userLatitude = action.meter.latitude,
                            userLongitude = action.meter.longitude
                        )
                    )
                }
            }

            is MapAction.OnMapCameraChanged -> {
                _state.update {
                    it.copy(
                        mapViewState = it.mapViewState.copy(
                            userLatitude = action.latitude,
                            userLongitude = action.longitude,
                            zoomLevel = action.zoom
                        )
                    )
                }
            }

            is MapAction.OnToggleSortDialog -> {
                _state.update { it.copy(showSortDialog = !it.showSortDialog) }
            }

            is MapAction.OnToggleViewMode -> {
                _state.update { it.copy(isListView = !it.isListView) }
            }

            is MapAction.OnDismissMeterDetails -> {
                _state.update { it.copy(showMeterDetails = false, selectedMeter = null) }
            }

            is MapAction.OnRefreshData -> {
                refreshData()
            }

            is MapAction.OnRequestLocationPermission -> {
                viewModelScope.launch {
                    eventChannel.send(MapScreenEvent.RequestLocationPermission)
                }
            }

            is MapAction.OnCenterOnUserLocation -> {
                centerOnUserLocation()
            }

            is MapAction.OnLocationPermissionGranted -> {
                centerOnUserLocation()
            }
        }
    }

    private fun loadParkingMeters() {
        viewModelScope.launch {
            val currentState = state.value
            getParkingMetersUseCase(
                sortOption = currentState.sortOption,
                userLat = currentState.mapViewState.userLatitude,
                userLng = currentState.mapViewState.userLongitude
            ).collectLatest { result ->
                handleParkingMetersResult(result)
            }
        }
    }

    private fun handleParkingMetersResult(result: Resource<List<ParkingMeter>>) {
        when (result) {
            is Resource.Success -> {
                _state.update {
                    it.copy(
                        parkingMeters = result.data,
                        filteredMeters = result.data,
                        isLoading = false
                    )
                }
            }

            is Resource.Error -> {
                _state.update { it.copy(isLoading = false) }
                viewModelScope.launch {
                    eventChannel.send(MapScreenEvent.Error(UiText.DynamicString(result.message)))
                }
                Timber.e("Error loading parking meters: ${result.message}")
            }

            is Resource.Loading -> {
                _state.update { it.copy(isLoading = true) }
            }
        }
    }

    private fun searchWithDebounce(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_MS)

            if (query.isBlank()) {
                _state.update { it.copy(filteredMeters = it.parkingMeters) }
                return@launch
            }

            searchParkingMetersUseCase(query).collectLatest { result ->
                when (result) {
                    is Resource.Success -> {
                        _state.update { it.copy(filteredMeters = result.data) }
                    }

                    is Resource.Error -> {
                        Timber.e("Error searching: ${result.message}")
                    }

                    is Resource.Loading -> {
                        // Optional: show search loading indicator
                    }
                }
            }
        }
    }

    private fun refreshData() {
        viewModelScope.launch {
            _state.update { it.copy(isRefreshing = true) }

            when (val result = refreshParkingMetersUseCase()) {
                is Resource.Success -> {
                    loadParkingMeters()
                    _state.update { it.copy(isRefreshing = false) }
                }

                is Resource.Error -> {
                    _state.update { it.copy(isRefreshing = false) }
                    eventChannel.send(MapScreenEvent.Error(UiText.DynamicString(result.message)))
                }

                is Resource.Loading -> {
                    // Already handled
                }
            }
        }
    }

    private fun checkAndLoadUserLocation() {
        if (locationRepository.hasLocationPermission()) {
            centerOnUserLocation()
        }
    }

    private fun centerOnUserLocation() {
        viewModelScope.launch {
            locationRepository.getCurrentLocation().collectLatest { result ->
                when (result) {
                    is Resource.Success -> {
                        val (lat, lng) = result.data
                        _state.update {
                            it.copy(
                                mapViewState = it.mapViewState.copy(
                                    userLatitude = lat,
                                    userLongitude = lng,
                                    isUserLocationEnabled = true
                                )
                            )
                        }
                        eventChannel.send(MapScreenEvent.AnimateCameraToLocation(lat, lng))
                    }

                    is Resource.Error -> {
                        Timber.e("Error getting location: ${result.message}")
                        // Fall back to default Vancouver location
                        _state.update {
                            it.copy(mapViewState = MapViewState())
                        }
                        eventChannel.send(
                            MapScreenEvent.Error(
                                UiText.StringResource(R.string.error_location_unavailable)
                            )
                        )
                    }

                    is Resource.Loading -> {
                        // Optional: show location loading
                    }
                }
            }
        }
    }

    companion object {
        private const val SEARCH_DEBOUNCE_MS = 300L
    }
}
