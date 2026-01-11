package com.aarevalo.parking.feature.map.di

import com.aarevalo.parking.feature.map.data.ParkingMetersRepositoryImpl
import com.aarevalo.parking.feature.map.domain.ParkingMetersRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class MapModule {
    @Binds
    @Singleton
    abstract fun bindParkingMetersRepository(impl: ParkingMetersRepositoryImpl): ParkingMetersRepository
}

