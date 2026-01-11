package com.aarevalo.parking.authentication.domain.usecase

import com.aarevalo.parking.authentication.domain.model.User
import com.aarevalo.parking.authentication.domain.repository.AuthRepository
import com.aarevalo.parking.authentication.domain.util.AuthValidationError
import com.aarevalo.parking.authentication.domain.util.EmailValidator
import com.aarevalo.parking.authentication.domain.util.PasswordValidator
import com.aarevalo.parking.authentication.domain.util.ValidationResult
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
    ): SignUpResult {
        // Validate email
        val emailValidation = validateEmail(email)
        if (emailValidation is ValidationResult.Invalid) {
            return SignUpResult.ValidationError(emailValidation.error)
        }

        // Validate password
        val passwordValidation = validatePassword(password, confirmPassword)
        if (passwordValidation is ValidationResult.Invalid) {
            return SignUpResult.ValidationError(passwordValidation.error)
        }

        // Attempt sign up
        val result = authRepository.signUpWithEmail(
            email = email.trim(),
            password = password,
            displayName = displayName?.trim()?.takeIf { it.isNotBlank() }
        )

        return when (result) {
            is Resource.Success -> SignUpResult.Success(result.data)
            is Resource.Error -> SignUpResult.Error(result.message)
            is Resource.Loading -> SignUpResult.Error("Unexpected loading state")
        }
    }

    private fun validateEmail(email: String): ValidationResult {
        return when {
            email.isBlank() -> ValidationResult.Invalid(AuthValidationError.EmptyEmail)
            !EmailValidator.isValid(email) -> ValidationResult.Invalid(AuthValidationError.InvalidEmail)
            else -> ValidationResult.Valid
        }
    }

    private fun validatePassword(password: String, confirmPassword: String): ValidationResult {
        return when {
            password.isBlank() -> ValidationResult.Invalid(AuthValidationError.EmptyPassword)
            !PasswordValidator.isLongEnough(password) -> ValidationResult.Invalid(AuthValidationError.PasswordTooShort)
            password != confirmPassword -> ValidationResult.Invalid(AuthValidationError.PasswordsDoNotMatch)
            !PasswordValidator.isStrong(password) -> ValidationResult.Invalid(AuthValidationError.WeakPassword)
            else -> ValidationResult.Valid
        }
    }
}

/**
 * Result of sign up operation.
 */
sealed class SignUpResult {
    data class Success(val user: User) : SignUpResult()
    data class ValidationError(val error: AuthValidationError) : SignUpResult()
    data class Error(val message: String) : SignUpResult()
}
