package com.aarevalo.parking.authentication.data.repository

import com.aarevalo.parking.authentication.domain.model.AuthState
import com.aarevalo.parking.authentication.domain.model.User
import com.aarevalo.parking.authentication.domain.repository.AuthRepository
import com.aarevalo.parking.core.di.IoDispatcher
import com.aarevalo.parking.core.domain.util.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of AuthRepository using Firebase Authentication.
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : AuthRepository {

    override val authState: Flow<AuthState> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            val firebaseUser = auth.currentUser
            val state = if (firebaseUser != null) {
                AuthState.Authenticated(firebaseUser.toDomainUser())
            } else {
                AuthState.Unauthenticated
            }
            trySend(state)
        }

        firebaseAuth.addAuthStateListener(authStateListener)

        awaitClose {
            firebaseAuth.removeAuthStateListener(authStateListener)
        }
    }.flowOn(ioDispatcher)

    override val currentUser: User?
        get() = firebaseAuth.currentUser?.toDomainUser()

    override suspend fun signInWithEmail(email: String, password: String): Resource<User> {
        return withContext(ioDispatcher) {
            try {
                val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
                val firebaseUser = result.user

                if (firebaseUser != null) {
                    Timber.d("User signed in: ${firebaseUser.email}")
                    Resource.Success(firebaseUser.toDomainUser())
                } else {
                    Resource.Error("Sign in failed: User is null")
                }
            } catch (e: FirebaseAuthException) {
                Timber.e(e, "Firebase Auth error during sign in")
                Resource.Error(mapFirebaseAuthError(e.errorCode), e)
            } catch (e: Exception) {
                Timber.e(e, "Error during sign in")
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
                val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
                val firebaseUser = result.user

                if (firebaseUser != null) {
                    // Update display name if provided
                    if (!displayName.isNullOrBlank()) {
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(displayName)
                            .build()
                        firebaseUser.updateProfile(profileUpdates).await()
                    }

                    // Send email verification
                    firebaseUser.sendEmailVerification().await()
                    Timber.d("User signed up: ${firebaseUser.email}, verification email sent")

                    Resource.Success(firebaseUser.toDomainUser())
                } else {
                    Resource.Error("Sign up failed: User is null")
                }
            } catch (e: FirebaseAuthException) {
                Timber.e(e, "Firebase Auth error during sign up")
                Resource.Error(mapFirebaseAuthError(e.errorCode), e)
            } catch (e: Exception) {
                Timber.e(e, "Error during sign up")
                Resource.Error(e.message ?: "Sign up failed", e)
            }
        }
    }

    override suspend fun signOut(): Resource<Unit> {
        return withContext(ioDispatcher) {
            try {
                firebaseAuth.signOut()
                Timber.d("User signed out")
                Resource.Success(Unit)
            } catch (e: Exception) {
                Timber.e(e, "Error during sign out")
                Resource.Error(e.message ?: "Sign out failed", e)
            }
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): Resource<Unit> {
        return withContext(ioDispatcher) {
            try {
                firebaseAuth.sendPasswordResetEmail(email).await()
                Timber.d("Password reset email sent to: $email")
                Resource.Success(Unit)
            } catch (e: FirebaseAuthException) {
                Timber.e(e, "Firebase Auth error sending password reset email")
                Resource.Error(mapFirebaseAuthError(e.errorCode), e)
            } catch (e: Exception) {
                Timber.e(e, "Error sending password reset email")
                Resource.Error(e.message ?: "Failed to send password reset email", e)
            }
        }
    }

    override fun isUserSignedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    /**
     * Maps Firebase Auth error codes to user-friendly messages.
     */
    private fun mapFirebaseAuthError(errorCode: String): String {
        return when (errorCode) {
            "ERROR_INVALID_EMAIL" -> "Invalid email address"
            "ERROR_WRONG_PASSWORD" -> "Incorrect password"
            "ERROR_USER_NOT_FOUND" -> "No account found with this email"
            "ERROR_USER_DISABLED" -> "This account has been disabled"
            "ERROR_TOO_MANY_REQUESTS" -> "Too many attempts. Please try again later"
            "ERROR_EMAIL_ALREADY_IN_USE" -> "An account already exists with this email"
            "ERROR_WEAK_PASSWORD" -> "Password is too weak. Please use a stronger password"
            "ERROR_INVALID_CREDENTIAL" -> "Invalid credentials. Please check your email and password"
            "ERROR_OPERATION_NOT_ALLOWED" -> "This sign-in method is not enabled"
            "ERROR_NETWORK_REQUEST_FAILED" -> "Network error. Please check your connection"
            else -> "Authentication failed. Please try again"
        }
    }

    /**
     * Extension function to convert FirebaseUser to domain User model.
     */
    private fun FirebaseUser.toDomainUser(): User {
        return User(
            id = uid,
            email = email ?: "",
            displayName = displayName,
            photoUrl = photoUrl?.toString(),
            isEmailVerified = isEmailVerified
        )
    }
}
