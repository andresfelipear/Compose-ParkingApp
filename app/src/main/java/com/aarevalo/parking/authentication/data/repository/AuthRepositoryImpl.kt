package com.aarevalo.parking.authentication.data.repository

import com.aarevalo.parking.authentication.domain.model.AuthState
import com.aarevalo.parking.authentication.domain.model.User
import com.aarevalo.parking.authentication.domain.repository.AuthRepository
import com.aarevalo.parking.core.di.IoDispatcher
import com.aarevalo.parking.core.domain.util.Resource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of AuthRepository.
 * 
 * Note: This is a placeholder implementation. In a production app,
 * you would integrate with Firebase Auth, your own backend, or another
 * authentication provider.
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : AuthRepository {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    override val authState: Flow<AuthState> = _authState.asStateFlow()

    private var _currentUser: User? = null
    override val currentUser: User?
        get() = _currentUser

    override suspend fun signInWithEmail(email: String, password: String): Resource<User> {
        return withContext(ioDispatcher) {
            try {
                _authState.value = AuthState.Loading

                // TODO: Replace with actual authentication logic
                // This is a placeholder that simulates a network call
                delay(1000)

                // Simulate successful login
                val user = User(
                    id = "user_${System.currentTimeMillis()}",
                    email = email,
                    displayName = email.substringBefore("@"),
                    photoUrl = null,
                    isEmailVerified = true
                )

                _currentUser = user
                _authState.value = AuthState.Authenticated(user)

                Timber.d("User signed in: ${user.email}")
                Resource.Success(user)
            } catch (e: Exception) {
                Timber.e(e, "Error signing in")
                _authState.value = AuthState.Error(e.message ?: "Sign in failed")
                Resource.Error(e.message ?: "Sign in failed", e)
            }
        }
    }

    override suspend fun signUpWithEmail(
        email: String,
        password: String,
        displayName: String?
    ): Resource<User> {
        return withContext(ioDispatcher) {
            try {
                _authState.value = AuthState.Loading

                // TODO: Replace with actual registration logic
                delay(1500)

                val user = User(
                    id = "user_${System.currentTimeMillis()}",
                    email = email,
                    displayName = displayName ?: email.substringBefore("@"),
                    photoUrl = null,
                    isEmailVerified = false
                )

                _currentUser = user
                _authState.value = AuthState.Authenticated(user)

                Timber.d("User signed up: ${user.email}")
                Resource.Success(user)
            } catch (e: Exception) {
                Timber.e(e, "Error signing up")
                _authState.value = AuthState.Error(e.message ?: "Sign up failed")
                Resource.Error(e.message ?: "Sign up failed", e)
            }
        }
    }

    override suspend fun signOut(): Resource<Unit> {
        return withContext(ioDispatcher) {
            try {
                _currentUser = null
                _authState.value = AuthState.Unauthenticated
                Timber.d("User signed out")
                Resource.Success(Unit)
            } catch (e: Exception) {
                Timber.e(e, "Error signing out")
                Resource.Error(e.message ?: "Sign out failed", e)
            }
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): Resource<Unit> {
        return withContext(ioDispatcher) {
            try {
                // TODO: Replace with actual password reset logic
                delay(1000)
                Timber.d("Password reset email sent to: $email")
                Resource.Success(Unit)
            } catch (e: Exception) {
                Timber.e(e, "Error sending password reset email")
                Resource.Error(e.message ?: "Failed to send password reset email", e)
            }
        }
    }

    override fun isUserSignedIn(): Boolean {
        return _currentUser != null
    }
}
