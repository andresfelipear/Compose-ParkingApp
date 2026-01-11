package com.aarevalo.parking.core.di

import android.content.Context
import androidx.room.Room
import com.aarevalo.parking.core.data.local.ParkingDatabase
import com.aarevalo.parking.core.data.local.ParkingMeterDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ParkingDatabase =
        Room.databaseBuilder(context, ParkingDatabase::class.java, "parking.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideParkingMeterDao(db: ParkingDatabase): ParkingMeterDao = db.parkingMeterDao()
}

