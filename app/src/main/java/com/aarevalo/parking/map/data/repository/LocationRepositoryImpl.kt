package com.aarevalo.parking.map.data.repository

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import androidx.core.content.ContextCompat
import com.aarevalo.parking.core.di.IoDispatcher
import com.aarevalo.parking.core.domain.util.Resource
import com.aarevalo.parking.map.domain.repository.LocationRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.suspendCancellableCoroutine
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * Implementation of LocationRepository using Google Play Services.
 */
@Singleton
class LocationRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val fusedLocationClient: FusedLocationProviderClient,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : LocationRepository {

    override fun getCurrentLocation(): Flow<Resource<Pair<Double, Double>>> = flow {
        emit(Resource.Loading)

        if (!hasLocationPermission()) {
            emit(Resource.Error("Location permission not granted"))
            return@flow
        }

        if (!isLocationEnabled()) {
            emit(Resource.Error("Location services are disabled"))
            return@flow
        }

        try {
            val location = suspendCancellableCoroutine { continuation ->
                val cancellationTokenSource = CancellationTokenSource()

                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_HIGH_ACCURACY,
                    cancellationTokenSource.token
                ).addOnSuccessListener { location ->
                    if (location != null) {
                        continuation.resume(location)
                    } else {
                        // Try to get last known location
                        fusedLocationClient.lastLocation.addOnSuccessListener { lastLocation ->
                            continuation.resume(lastLocation)
                        }.addOnFailureListener {
                            continuation.resume(null)
                        }
                    }
                }.addOnFailureListener { exception ->
                    Timber.e(exception, "Error getting current location")
                    continuation.resume(null)
                }

                continuation.invokeOnCancellation {
                    cancellationTokenSource.cancel()
                }
            }

            if (location != null) {
                emit(Resource.Success(Pair(location.latitude, location.longitude)))
            } else {
                emit(Resource.Error("Unable to get current location"))
            }
        } catch (e: SecurityException) {
            Timber.e(e, "Security exception when getting location")
            emit(Resource.Error("Location permission denied"))
        } catch (e: Exception) {
            Timber.e(e, "Error getting location")
            emit(Resource.Error(e.message ?: "Unknown error getting location"))
        }
    }.flowOn(ioDispatcher)

    override fun hasLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    override fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }
}
