package com.aarevalo.parking.feature.authentication.di

import com.aarevalo.parking.feature.authentication.data.AuthRepositoryImpl
import com.aarevalo.parking.feature.authentication.domain.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AuthenticationModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
}

