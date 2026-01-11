package com.aarevalo.parking.authentication.domain.util

/**
 * Sealed class representing authentication validation errors.
 * These are mapped to UiText in the presentation layer.
 */
sealed class AuthValidationError {
    data object EmptyEmail : AuthValidationError()
    data object InvalidEmail : AuthValidationError()
    data object EmptyPassword : AuthValidationError()
    data object PasswordTooShort : AuthValidationError()
    data object WeakPassword : AuthValidationError()
    data object PasswordsDoNotMatch : AuthValidationError()
}

/**
 * Result of validation operations.
 */
sealed class ValidationResult {
    data object Valid : ValidationResult()
    data class Invalid(val error: AuthValidationError) : ValidationResult()
}

/**
 * Utility object for email validation.
 */
object EmailValidator {
    private val EMAIL_REGEX = Regex(
        "[a-zA-Z0-9+._%\\-]{1,256}" +
                "@" +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" +
                "(" +
                "\\." +
                "[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25}" +
                ")+"
    )

    fun isValid(email: String): Boolean {
        return email.matches(EMAIL_REGEX)
    }
}

/**
 * Utility object for password validation.
 */
object PasswordValidator {
    const val MIN_PASSWORD_LENGTH = 6

    fun isLongEnough(password: String): Boolean {
        return password.length >= MIN_PASSWORD_LENGTH
    }

    fun isStrong(password: String): Boolean {
        val hasLetter = password.any { it.isLetter() }
        val hasDigit = password.any { it.isDigit() }
        return hasLetter && hasDigit
    }
}
