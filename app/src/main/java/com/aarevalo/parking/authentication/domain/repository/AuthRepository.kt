package com.aarevalo.parking.authentication.domain.repository

import com.aarevalo.parking.authentication.domain.model.AuthState
import com.aarevalo.parking.authentication.domain.model.User
import com.aarevalo.parking.core.domain.util.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for authentication operations.
 */
interface AuthRepository {

    /**
     * Observable authentication state.
     */
    val authState: Flow<AuthState>

    /**
     * Gets the current user, if authenticated.
     */
    val currentUser: User?

    /**
     * Signs in a user with email and password.
     */
    suspend fun signInWithEmail(email: String, password: String): Resource<User>

    /**
     * Creates a new user account with email and password.
     */
    suspend fun signUpWithEmail(
        email: String,
        password: String,
        displayName: String?
    ): Resource<User>

    /**
     * Signs out the current user.
     */
    suspend fun signOut(): Resource<Unit>

    /**
     * Sends a password reset email.
     */
    suspend fun sendPasswordResetEmail(email: String): Resource<Unit>

    /**
     * Checks if a user is currently signed in.
     */
    fun isUserSignedIn(): Boolean
}
