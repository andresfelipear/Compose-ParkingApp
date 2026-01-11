package com.aarevalo.parking.authentication.domain.usecase

import com.aarevalo.parking.authentication.domain.model.User
import com.aarevalo.parking.authentication.domain.repository.AuthRepository
import com.aarevalo.parking.core.domain.util.Resource
import javax.inject.Inject

/**
 * Use case for creating a new user account.
 */
class SignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        confirmPassword: String,
        displayName: String?
    ): Resource<User> {
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

        if (password != confirmPassword) {
            return Resource.Error("Passwords do not match")
        }

        if (!isStrongPassword(password)) {
            return Resource.Error("Password must contain at least one letter and one number")
        }

        return authRepository.signUpWithEmail(
            email = email.trim(),
            password = password,
            displayName = displayName?.trim()?.takeIf { it.isNotBlank() }
        )
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isStrongPassword(password: String): Boolean {
        val hasLetter = password.any { it.isLetter() }
        val hasDigit = password.any { it.isDigit() }
        return hasLetter && hasDigit
    }
}
