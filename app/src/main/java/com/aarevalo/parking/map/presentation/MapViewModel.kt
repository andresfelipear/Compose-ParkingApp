package com.aarevalo.parking.map.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aarevalo.parking.core.domain.model.ParkingMeter
import com.aarevalo.parking.core.domain.util.Resource
import com.aarevalo.parking.map.domain.model.MapViewState
import com.aarevalo.parking.map.domain.model.SortOption
import com.aarevalo.parking.map.domain.repository.LocationRepository
import com.aarevalo.parking.map.domain.usecase.GetParkingMetersUseCase
import com.aarevalo.parking.map.domain.usecase.RefreshParkingMetersUseCase
import com.aarevalo.parking.map.domain.usecase.SearchParkingMetersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for the Map screen.
 */
@HiltViewModel
class MapViewModel @Inject constructor(
    private val getParkingMetersUseCase: GetParkingMetersUseCase,
    private val searchParkingMetersUseCase: SearchParkingMetersUseCase,
    private val refreshParkingMetersUseCase: RefreshParkingMetersUseCase,
    private val locationRepository: LocationRepository
) : ViewModel() {

    private val _state = MutableStateFlow(MapUiState())
    val state = _state.asStateFlow()

    private val _sideEffect = MutableSharedFlow<MapSideEffect>()
    val sideEffect = _sideEffect.asSharedFlow()

    private var searchJob: Job? = null

    init {
        loadParkingMeters()
        checkAndLoadUserLocation()
    }

    fun onEvent(event: MapEvent) {
        when (event) {
            is MapEvent.SearchQueryChanged -> {
                _state.update { it.copy(searchQuery = event.query) }
                searchWithDebounce(event.query)
            }

            is MapEvent.SortOptionSelected -> {
                _state.update { it.copy(sortOption = event.sortOption, showSortDialog = false) }
                loadParkingMeters()
            }

            is MapEvent.MeterSelected -> {
                _state.update {
                    it.copy(
                        selectedMeter = event.meter,
                        showMeterDetails = true,
                        mapViewState = it.mapViewState.copy(
                            userLatitude = event.meter.latitude,
                            userLongitude = event.meter.longitude
                        )
                    )
                }
            }

            is MapEvent.MapCameraChanged -> {
                _state.update {
                    it.copy(
                        mapViewState = it.mapViewState.copy(
                            userLatitude = event.latitude,
                            userLongitude = event.longitude,
                            zoomLevel = event.zoom
                        )
                    )
                }
            }

            is MapEvent.ToggleSortDialog -> {
                _state.update { it.copy(showSortDialog = !it.showSortDialog) }
            }

            is MapEvent.ToggleViewMode -> {
                _state.update { it.copy(isListView = !it.isListView) }
            }

            is MapEvent.DismissMeterDetails -> {
                _state.update { it.copy(showMeterDetails = false, selectedMeter = null) }
            }

            is MapEvent.RefreshData -> {
                refreshData()
            }

            is MapEvent.RequestLocationPermission -> {
                viewModelScope.launch {
                    _sideEffect.emit(MapSideEffect.RequestLocationPermission)
                }
            }

            is MapEvent.CenterOnUserLocation -> {
                centerOnUserLocation()
            }

            is MapEvent.ClearError -> {
                _state.update { it.copy(error = null) }
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
                        isLoading = false,
                        error = null
                    )
                }
            }

            is Resource.Error -> {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = result.message
                    )
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
                    _state.update {
                        it.copy(
                            isRefreshing = false,
                            error = result.message
                        )
                    }
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
                        _sideEffect.emit(MapSideEffect.AnimateCameraToLocation(lat, lng))
                    }

                    is Resource.Error -> {
                        Timber.e("Error getting location: ${result.message}")
                        // Fall back to default Vancouver location
                        _state.update {
                            it.copy(
                                mapViewState = MapViewState()
                            )
                        }
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

/**
 * Side effects for the Map screen that should be handled once.
 */
sealed class MapSideEffect {
    data object RequestLocationPermission : MapSideEffect()
    data class AnimateCameraToLocation(val latitude: Double, val longitude: Double) : MapSideEffect()
    data class ShowError(val message: String) : MapSideEffect()
}
