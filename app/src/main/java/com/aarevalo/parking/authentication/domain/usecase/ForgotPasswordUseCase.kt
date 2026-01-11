package com.aarevalo.parking.authentication.domain.usecase

import com.aarevalo.parking.authentication.domain.repository.AuthRepository
import com.aarevalo.parking.core.domain.util.Resource
import javax.inject.Inject

/**
 * Use case for sending a password reset email.
 */
class ForgotPasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String): Resource<Unit> {
        if (email.isBlank()) {
            return Resource.Error("Email cannot be empty")
        }

        if (!isValidEmail(email)) {
            return Resource.Error("Please enter a valid email address")
        }

        return authRepository.sendPasswordResetEmail(email.trim())
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
