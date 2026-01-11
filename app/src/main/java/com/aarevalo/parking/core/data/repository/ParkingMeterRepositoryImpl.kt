package com.aarevalo.parking.core.data.repository

import com.aarevalo.parking.core.data.local.dao.ParkingMeterDao
import com.aarevalo.parking.core.data.local.entity.ParkingMeterEntity
import com.aarevalo.parking.core.data.remote.api.ParkingMeterApi
import com.aarevalo.parking.core.di.IoDispatcher
import com.aarevalo.parking.core.domain.model.ParkingMeter
import com.aarevalo.parking.core.domain.util.NetworkError
import com.aarevalo.parking.core.domain.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of ParkingMeterRepository that handles data from both
 * local database and remote API.
 */
@Singleton
class ParkingMeterRepositoryImpl @Inject constructor(
    private val parkingMeterApi: ParkingMeterApi,
    private val parkingMeterDao: ParkingMeterDao,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : ParkingMeterRepository {

    override fun getParkingMetersSortedByRate(ascending: Boolean): Flow<Resource<List<ParkingMeter>>> = flow {
        emit(Resource.Loading)

        val localData = if (ascending) {
            parkingMeterDao.getAllParkingMetersSortedByRate()
        } else {
            parkingMeterDao.getAllParkingMetersSortedByRateDescending()
        }

        localData.collect { entities ->
            emit(Resource.Success(entities.map { it.toDomainModel() }))
        }
    }.catch { e ->
        Timber.e(e, "Error fetching parking meters")
        emit(Resource.Error(e.message ?: "Unknown error", e))
    }.flowOn(ioDispatcher)

    override suspend fun getParkingMeterById(meterId: String): Resource<ParkingMeter> {
        return withContext(ioDispatcher) {
            try {
                val entity = parkingMeterDao.getParkingMeterById(meterId)
                if (entity != null) {
                    Resource.Success(entity.toDomainModel())
                } else {
                    Resource.Error("Parking meter not found")
                }
            } catch (e: Exception) {
                Timber.e(e, "Error fetching parking meter by ID: $meterId")
                Resource.Error(e.message ?: "Unknown error", e)
            }
        }
    }

    override fun searchParkingMeters(query: String): Flow<Resource<List<ParkingMeter>>> = 
        parkingMeterDao.searchParkingMeters(query)
            .map<List<ParkingMeterEntity>, Resource<List<ParkingMeter>>> { entities ->
                Resource.Success(entities.map { it.toDomainModel() })
            }
            .catch { e ->
                Timber.e(e, "Error searching parking meters")
                emit(Resource.Error(e.message ?: "Unknown error", e))
            }
            .flowOn(ioDispatcher)

    override fun getParkingMetersByRateRange(
        minRate: Double,
        maxRate: Double
    ): Flow<Resource<List<ParkingMeter>>> = 
        parkingMeterDao.getParkingMetersByRateRange(minRate, maxRate)
            .map<List<ParkingMeterEntity>, Resource<List<ParkingMeter>>> { entities ->
                Resource.Success(entities.map { it.toDomainModel() })
            }
            .catch { e ->
                Timber.e(e, "Error fetching parking meters by rate range")
                emit(Resource.Error(e.message ?: "Unknown error", e))
            }
            .flowOn(ioDispatcher)

    override fun getParkingMetersInBounds(
        minLat: Double,
        maxLat: Double,
        minLng: Double,
        maxLng: Double
    ): Flow<Resource<List<ParkingMeter>>> = 
        parkingMeterDao.getParkingMetersInBounds(minLat, maxLat, minLng, maxLng)
            .map<List<ParkingMeterEntity>, Resource<List<ParkingMeter>>> { entities ->
                Resource.Success(entities.map { it.toDomainModel() })
            }
            .catch { e ->
                Timber.e(e, "Error fetching parking meters in bounds")
                emit(Resource.Error(e.message ?: "Unknown error", e))
            }
            .flowOn(ioDispatcher)

    override suspend fun refreshParkingMeters(): Resource<Unit> {
        return withContext(ioDispatcher) {
            try {
                var offset = 0
                val allMeters = mutableListOf<ParkingMeter>()

                // Fetch all parking meters using pagination
                do {
                    val response = parkingMeterApi.getParkingMeters(
                        limit = ParkingMeterApi.DEFAULT_LIMIT,
                        offset = offset,
                        orderBy = ParkingMeterApi.ORDER_BY_RATE_ASC
                    )

                    val meters = response.results?.mapNotNull { it.toDomainModel() } ?: emptyList()
                    allMeters.addAll(meters)

                    offset += ParkingMeterApi.DEFAULT_LIMIT
                } while (response.results?.size == ParkingMeterApi.DEFAULT_LIMIT && offset < ParkingMeterApi.MAX_LIMIT)

                // Save to local database
                parkingMeterDao.deleteAllParkingMeters()
                parkingMeterDao.insertParkingMeters(allMeters.map { ParkingMeterEntity.fromDomainModel(it) })

                Timber.d("Refreshed ${allMeters.size} parking meters")
                Resource.Success(Unit)
            } catch (e: SocketTimeoutException) {
                Timber.e(e, "Timeout while refreshing parking meters")
                Resource.Error(NetworkError.Timeout.message, NetworkError.Timeout)
            } catch (e: IOException) {
                Timber.e(e, "Network error while refreshing parking meters")
                Resource.Error(NetworkError.NoInternet.message, NetworkError.NoInternet)
            } catch (e: Exception) {
                Timber.e(e, "Error refreshing parking meters")
                Resource.Error(e.message ?: "Unknown error", e)
            }
        }
    }

    override suspend fun getCachedParkingMeterCount(): Int {
        return withContext(ioDispatcher) {
            parkingMeterDao.getParkingMeterCount()
        }
    }
}
