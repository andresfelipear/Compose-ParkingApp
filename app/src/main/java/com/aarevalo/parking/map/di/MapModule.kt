package com.aarevalo.parking.map.di

import android.content.Context
import com.aarevalo.parking.core.data.remote.api.ParkingMeterApi
import com.aarevalo.parking.core.data.repository.ParkingMeterRepository
import com.aarevalo.parking.core.data.repository.ParkingMeterRepositoryImpl
import com.aarevalo.parking.map.data.repository.LocationRepositoryImpl
import com.aarevalo.parking.map.domain.repository.LocationRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MapModule {

    @Binds
    @Singleton
    abstract fun bindParkingMeterRepository(
        parkingMeterRepositoryImpl: ParkingMeterRepositoryImpl
    ): ParkingMeterRepository

    @Binds
    @Singleton
    abstract fun bindLocationRepository(
        locationRepositoryImpl: LocationRepositoryImpl
    ): LocationRepository

    companion object {
        @Provides
        @Singleton
        fun provideParkingMeterApi(retrofit: Retrofit): ParkingMeterApi {
            return retrofit.create(ParkingMeterApi::class.java)
        }

        @Provides
        @Singleton
        fun provideFusedLocationProviderClient(
            @ApplicationContext context: Context
        ): FusedLocationProviderClient {
            return LocationServices.getFusedLocationProviderClient(context)
        }
    }
}
