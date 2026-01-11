package com.aarevalo.parking.feature.authentication.data

import com.aarevalo.parking.feature.authentication.domain.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor() : AuthRepository {
    override suspend fun isLoggedIn(): Boolean = false
}

