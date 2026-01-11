package com.aarevalo.parking.feature.authentication.domain

interface AuthRepository {
    suspend fun isLoggedIn(): Boolean
}

