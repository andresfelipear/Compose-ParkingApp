package com.aarevalo.parking.map.presentation

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aarevalo.parking.core.domain.model.ParkingMeter
import com.aarevalo.parking.core.presentation.components.ErrorMessage
import com.aarevalo.parking.core.presentation.components.LoadingIndicator
import com.aarevalo.parking.core.presentation.components.ParkingMeterCard
import com.aarevalo.parking.map.domain.model.SortOption
import com.aarevalo.parking.ui.theme.ParkingTheme
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onMeterClick: (ParkingMeter) -> Unit = {},
    viewModel: MapViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (fineLocationGranted || coarseLocationGranted) {
            viewModel.onEvent(MapEvent.CenterOnUserLocation)
        }
    }

    LaunchedEffect(key1 = true) {
        viewModel.sideEffect.collectLatest { effect ->
            when (effect) {
                is MapSideEffect.RequestLocationPermission -> {
                    locationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }

                is MapSideEffect.AnimateCameraToLocation -> {
                    // Camera animation handled by CameraPositionState
                }

                is MapSideEffect.ShowError -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    LaunchedEffect(key1 = state.error) {
        state.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.onEvent(MapEvent.ClearError)
        }
    }

    MapScreenContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onEvent = viewModel::onEvent,
        onMeterClick = onMeterClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MapScreenContent(
    state: MapUiState,
    snackbarHostState: SnackbarHostState,
    onEvent: (MapEvent) -> Unit,
    onMeterClick: (ParkingMeter) -> Unit
) {
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(state.mapViewState.userLatitude, state.mapViewState.userLongitude),
            state.mapViewState.zoomLevel
        )
    }

    val bottomSheetState = rememberModalBottomSheetState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vancouver Parking") },
                actions = {
                    IconButton(onClick = { onEvent(MapEvent.ToggleSortDialog) }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Sort"
                        )
                    }
                    IconButton(onClick = { onEvent(MapEvent.ToggleViewMode) }) {
                        Icon(
                            imageVector = if (state.isListView) Icons.Default.Map else Icons.Default.List,
                            contentDescription = if (state.isListView) "Map View" else "List View"
                        )
                    }
                    IconButton(onClick = { onEvent(MapEvent.RefreshData) }) {
                        if (state.isRefreshing) {
                            CircularProgressIndicator(
                                modifier = Modifier.height(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh"
                            )
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (!state.isListView) {
                Column {
                    SmallFloatingActionButton(
                        onClick = { onEvent(MapEvent.CenterOnUserLocation) }
                    ) {
                        Icon(
                            imageVector = Icons.Default.MyLocation,
                            contentDescription = "My Location"
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search bar
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = { onEvent(MapEvent.SearchQueryChanged(it)) },
                placeholder = { Text("Search parking meters...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Content
            when {
                state.isLoading && state.parkingMeters.isEmpty() -> {
                    LoadingIndicator()
                }

                state.error != null && state.parkingMeters.isEmpty() -> {
                    ErrorMessage(
                        message = state.error,
                        onRetry = { onEvent(MapEvent.RefreshData) }
                    )
                }

                else -> {
                    if (state.isListView) {
                        ParkingMeterListView(
                            meters = state.filteredMeters,
                            isRefreshing = state.isRefreshing,
                            onRefresh = { onEvent(MapEvent.RefreshData) },
                            onMeterClick = { meter ->
                                onEvent(MapEvent.MeterSelected(meter))
                                onMeterClick(meter)
                            }
                        )
                    } else {
                        ParkingMeterMapView(
                            meters = state.filteredMeters,
                            cameraPositionState = cameraPositionState,
                            isUserLocationEnabled = state.mapViewState.isUserLocationEnabled,
                            onMeterClick = { meter ->
                                onEvent(MapEvent.MeterSelected(meter))
                                onMeterClick(meter)
                            }
                        )
                    }
                }
            }
        }

        // Sort dialog
        if (state.showSortDialog) {
            SortDialog(
                currentSort = state.sortOption,
                onSortSelected = { onEvent(MapEvent.SortOptionSelected(it)) },
                onDismiss = { onEvent(MapEvent.ToggleSortDialog) }
            )
        }

        // Meter details bottom sheet
        if (state.showMeterDetails && state.selectedMeter != null) {
            ModalBottomSheet(
                onDismissRequest = { onEvent(MapEvent.DismissMeterDetails) },
                sheetState = bottomSheetState
            ) {
                ParkingMeterCard(
                    parkingMeter = state.selectedMeter,
                    modifier = Modifier.padding(16.dp)
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ParkingMeterListView(
    meters: List<ParkingMeter>,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onMeterClick: (ParkingMeter) -> Unit
) {
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = onRefresh,
        modifier = Modifier.fillMaxSize()
    ) {
        if (meters.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No parking meters found",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = meters,
                    key = { it.meterId }
                ) { meter ->
                    ParkingMeterCard(
                        parkingMeter = meter,
                        onClick = { onMeterClick(meter) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ParkingMeterMapView(
    meters: List<ParkingMeter>,
    cameraPositionState: com.google.maps.android.compose.CameraPositionState,
    isUserLocationEnabled: Boolean,
    onMeterClick: (ParkingMeter) -> Unit
) {
    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = MapProperties(
            isMyLocationEnabled = isUserLocationEnabled
        ),
        uiSettings = MapUiSettings(
            zoomControlsEnabled = true,
            myLocationButtonEnabled = false // We use our own FAB
        )
    ) {
        meters.forEach { meter ->
            Marker(
                state = MarkerState(position = LatLng(meter.latitude, meter.longitude)),
                title = "Meter ${meter.meterId}",
                snippet = "$${meter.rate}/hr - ${meter.timeLimit} min",
                onClick = {
                    onMeterClick(meter)
                    true
                }
            )
        }
    }
}

@Composable
private fun SortDialog(
    currentSort: SortOption,
    onSortSelected: (SortOption) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Sort by") },
        text = {
            Column {
                SortOption.entries.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = option == currentSort,
                            onClick = { onSortSelected(option) }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(option.displayName)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun MapScreenPreview() {
    ParkingTheme {
        MapScreenContent(
            state = MapUiState(
                parkingMeters = listOf(
                    ParkingMeter(
                        meterId = "1",
                        meterHead = "Single",
                        rateArea = "Downtown",
                        latitude = 49.2827,
                        longitude = -123.1207,
                        streetNum = 100,
                        streetSide = "N",
                        streetName = "West Georgia St",
                        timeLimit = 120,
                        rateDays = "Mon-Sat",
                        rateStart = "9:00 AM",
                        rateEnd = "10:00 PM",
                        rate = 6.0,
                        payByPhone = true
                    )
                ),
                filteredMeters = listOf(
                    ParkingMeter(
                        meterId = "1",
                        meterHead = "Single",
                        rateArea = "Downtown",
                        latitude = 49.2827,
                        longitude = -123.1207,
                        streetNum = 100,
                        streetSide = "N",
                        streetName = "West Georgia St",
                        timeLimit = 120,
                        rateDays = "Mon-Sat",
                        rateStart = "9:00 AM",
                        rateEnd = "10:00 PM",
                        rate = 6.0,
                        payByPhone = true
                    )
                ),
                isListView = true
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onEvent = {},
            onMeterClick = {}
        )
    }
}
