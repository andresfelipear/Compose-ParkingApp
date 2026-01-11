package com.aarevalo.parking.authentication.domain.model

/**
 * Sealed class representing the authentication state.
 */
sealed class AuthState {
    data object Loading : AuthState()
    data object Unauthenticated : AuthState()
    data class Authenticated(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
}
