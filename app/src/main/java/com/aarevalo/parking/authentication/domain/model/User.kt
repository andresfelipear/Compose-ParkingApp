package com.aarevalo.parking.authentication.domain.model

/**
 * Domain model representing a user in the application.
 */
data class User(
    val id: String,
    val email: String,
    val displayName: String?,
    val photoUrl: String?,
    val isEmailVerified: Boolean = false
)
