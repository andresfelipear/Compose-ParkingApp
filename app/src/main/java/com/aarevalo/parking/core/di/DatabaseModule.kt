package com.aarevalo.parking.core.di

import android.content.Context
import androidx.room.Room
import com.aarevalo.parking.core.data.local.ParkingDatabase
import com.aarevalo.parking.core.data.local.dao.ParkingMeterDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    private const val DATABASE_NAME = "parking_database"

    @Provides
    @Singleton
    fun provideParkingDatabase(
        @ApplicationContext context: Context
    ): ParkingDatabase {
        return Room.databaseBuilder(
            context,
            ParkingDatabase::class.java,
            DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideParkingMeterDao(database: ParkingDatabase): ParkingMeterDao {
        return database.parkingMeterDao()
    }
}
