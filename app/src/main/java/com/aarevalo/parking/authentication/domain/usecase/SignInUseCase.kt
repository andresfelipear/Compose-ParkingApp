package com.aarevalo.parking.authentication.domain.usecase

import com.aarevalo.parking.authentication.domain.model.User
import com.aarevalo.parking.authentication.domain.repository.AuthRepository
import com.aarevalo.parking.core.domain.util.Resource
import javax.inject.Inject

/**
 * Use case for signing in a user with email and password.
 */
class SignInUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Resource<User> {
        // Validate input
        if (email.isBlank()) {
            return Resource.Error("Email cannot be empty")
        }

        if (!isValidEmail(email)) {
            return Resource.Error("Please enter a valid email address")
        }

        if (password.isBlank()) {
            return Resource.Error("Password cannot be empty")
        }

        if (password.length < 6) {
            return Resource.Error("Password must be at least 6 characters")
        }

        return authRepository.signInWithEmail(email.trim(), password)
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
