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
 * Use case for signing in a user with email and password.
 * Returns either a validation error or delegates to the repository.
 */
class SignInUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): SignInResult {
        // Validate email
        val emailValidation = validateEmail(email)
        if (emailValidation is ValidationResult.Invalid) {
            return SignInResult.ValidationError(emailValidation.error)
        }

        // Validate password
        val passwordValidation = validatePassword(password)
        if (passwordValidation is ValidationResult.Invalid) {
            return SignInResult.ValidationError(passwordValidation.error)
        }

        // Attempt sign in
        return when (val result = authRepository.signInWithEmail(email.trim(), password)) {
            is Resource.Success -> SignInResult.Success(result.data)
            is Resource.Error -> SignInResult.Error(result.message)
            is Resource.Loading -> SignInResult.Error("Unexpected loading state")
        }
    }

    private fun validateEmail(email: String): ValidationResult {
        return when {
            email.isBlank() -> ValidationResult.Invalid(AuthValidationError.EmptyEmail)
            !EmailValidator.isValid(email) -> ValidationResult.Invalid(AuthValidationError.InvalidEmail)
            else -> ValidationResult.Valid
        }
    }

    private fun validatePassword(password: String): ValidationResult {
        return when {
            password.isBlank() -> ValidationResult.Invalid(AuthValidationError.EmptyPassword)
            !PasswordValidator.isLongEnough(password) -> ValidationResult.Invalid(AuthValidationError.PasswordTooShort)
            else -> ValidationResult.Valid
        }
    }
}

/**
 * Result of sign in operation.
 */
sealed class SignInResult {
    data class Success(val user: User) : SignInResult()
    data class ValidationError(val error: AuthValidationError) : SignInResult()
    data class Error(val message: String) : SignInResult()
}
