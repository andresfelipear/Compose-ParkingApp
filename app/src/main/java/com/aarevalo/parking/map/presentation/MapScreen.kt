package com.aarevalo.parking.map.presentation

import android.Manifest
import android.widget.Toast
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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aarevalo.parking.R
import com.aarevalo.parking.core.domain.model.ParkingMeter
import com.aarevalo.parking.core.presentation.components.ErrorMessage
import com.aarevalo.parking.core.presentation.components.LoadingIndicator
import com.aarevalo.parking.core.presentation.components.ParkingMeterCard
import com.aarevalo.parking.core.presentation.util.ObserveAsEvents
import com.aarevalo.parking.map.domain.model.SortOption
import com.aarevalo.parking.ui.theme.ParkingTheme
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun MapScreenRoot(
    onMeterClick: (ParkingMeter) -> Unit = {},
    viewModel: MapViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (fineLocationGranted || coarseLocationGranted) {
            viewModel.onAction(MapAction.OnLocationPermissionGranted)
        }
    }

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is MapScreenEvent.Error -> {
                Toast.makeText(
                    context,
                    event.message.asString(context),
                    Toast.LENGTH_LONG
                ).show()
            }

            is MapScreenEvent.RequestLocationPermission -> {
                locationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                )
            }

            is MapScreenEvent.AnimateCameraToLocation -> {
                // Camera animation handled by CameraPositionState in the screen
            }
        }
    }

    val state by viewModel.state.collectAsStateWithLifecycle()

    MapScreen(
        state = state,
        onAction = viewModel::onAction,
        onMeterClick = onMeterClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MapScreen(
    state: MapUiState,
    onAction: (MapAction) -> Unit,
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
                title = { Text(stringResource(R.string.vancouver_parking)) },
                actions = {
                    IconButton(onClick = { onAction(MapAction.OnToggleSortDialog) }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = stringResource(R.string.sort)
                        )
                    }
                    IconButton(onClick = { onAction(MapAction.OnToggleViewMode) }) {
                        Icon(
                            imageVector = if (state.isListView) Icons.Default.Map else Icons.Default.List,
                            contentDescription = if (state.isListView) {
                                stringResource(R.string.map_view)
                            } else {
                                stringResource(R.string.list_view)
                            }
                        )
                    }
                    IconButton(onClick = { onAction(MapAction.OnRefreshData) }) {
                        if (state.isRefreshing) {
                            CircularProgressIndicator(
                                modifier = Modifier.height(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = stringResource(R.string.refresh)
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (!state.isListView) {
                SmallFloatingActionButton(
                    onClick = { onAction(MapAction.OnCenterOnUserLocation) }
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = stringResource(R.string.my_location)
                    )
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
                onValueChange = { onAction(MapAction.OnSearchQueryChanged(it)) },
                placeholder = { Text(stringResource(R.string.search_parking_meters)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
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

                else -> {
                    if (state.isListView) {
                        ParkingMeterListView(
                            meters = state.filteredMeters,
                            isRefreshing = state.isRefreshing,
                            onRefresh = { onAction(MapAction.OnRefreshData) },
                            onMeterClick = { meter ->
                                onAction(MapAction.OnMeterSelected(meter))
                                onMeterClick(meter)
                            }
                        )
                    } else {
                        ParkingMeterMapView(
                            meters = state.filteredMeters,
                            cameraPositionState = cameraPositionState,
                            isUserLocationEnabled = state.mapViewState.isUserLocationEnabled,
                            onMeterClick = { meter ->
                                onAction(MapAction.OnMeterSelected(meter))
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
                onSortSelected = { onAction(MapAction.OnSortOptionSelected(it)) },
                onDismiss = { onAction(MapAction.OnToggleSortDialog) }
            )
        }

        // Meter details bottom sheet
        if (state.showMeterDetails && state.selectedMeter != null) {
            ModalBottomSheet(
                onDismissRequest = { onAction(MapAction.OnDismissMeterDetails) },
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
                    text = stringResource(R.string.no_parking_meters_found),
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
    cameraPositionState: CameraPositionState,
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
                title = stringResource(R.string.meter_id, meter.meterId),
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
        title = { Text(stringResource(R.string.sort_by)) },
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
                        Text(
                            text = when (option) {
                                SortOption.RATE_LOW_TO_HIGH -> stringResource(R.string.sort_price_low_high)
                                SortOption.RATE_HIGH_TO_LOW -> stringResource(R.string.sort_price_high_low)
                                SortOption.DISTANCE -> stringResource(R.string.sort_distance)
                                SortOption.TIME_LIMIT -> stringResource(R.string.sort_time_limit)
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun MapScreenPreview() {
    ParkingTheme {
        MapScreen(
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
            onAction = {},
            onMeterClick = {}
        )
    }
}
