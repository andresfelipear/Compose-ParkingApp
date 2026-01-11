package com.aarevalo.parking.authentication.domain.usecase

import com.aarevalo.parking.authentication.domain.repository.AuthRepository
import com.aarevalo.parking.authentication.domain.util.AuthValidationError
import com.aarevalo.parking.authentication.domain.util.EmailValidator
import com.aarevalo.parking.authentication.domain.util.ValidationResult
import com.aarevalo.parking.core.domain.util.Resource
import javax.inject.Inject

/**
 * Use case for sending a password reset email.
 */
class ForgotPasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String): ForgotPasswordResult {
        // Validate email
        val emailValidation = validateEmail(email)
        if (emailValidation is ValidationResult.Invalid) {
            return ForgotPasswordResult.ValidationError(emailValidation.error)
        }

        // Send password reset email
        return when (val result = authRepository.sendPasswordResetEmail(email.trim())) {
            is Resource.Success -> ForgotPasswordResult.Success
            is Resource.Error -> ForgotPasswordResult.Error(result.message)
            is Resource.Loading -> ForgotPasswordResult.Error("Unexpected loading state")
        }
    }

    private fun validateEmail(email: String): ValidationResult {
        return when {
            email.isBlank() -> ValidationResult.Invalid(AuthValidationError.EmptyEmail)
            !EmailValidator.isValid(email) -> ValidationResult.Invalid(AuthValidationError.InvalidEmail)
            else -> ValidationResult.Valid
        }
    }
}

/**
 * Result of forgot password operation.
 */
sealed class ForgotPasswordResult {
    data object Success : ForgotPasswordResult()
    data class ValidationError(val error: AuthValidationError) : ForgotPasswordResult()
    data class Error(val message: String) : ForgotPasswordResult()
}
