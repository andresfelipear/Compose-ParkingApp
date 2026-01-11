package com.aarevalo.parking.authentication.presentation.util

import com.aarevalo.parking.R
import com.aarevalo.parking.authentication.domain.util.AuthValidationError
import com.aarevalo.parking.core.presentation.util.UiText

/**
 * Maps authentication validation errors to user-friendly UiText.
 */
fun AuthValidationError.toUiText(): UiText {
    return when (this) {
        AuthValidationError.EmptyEmail -> UiText.StringResource(R.string.error_email_empty)
        AuthValidationError.InvalidEmail -> UiText.StringResource(R.string.error_email_invalid)
        AuthValidationError.EmptyPassword -> UiText.StringResource(R.string.error_password_empty)
        AuthValidationError.PasswordTooShort -> UiText.StringResource(R.string.error_password_too_short)
        AuthValidationError.WeakPassword -> UiText.StringResource(R.string.error_password_weak)
        AuthValidationError.PasswordsDoNotMatch -> UiText.StringResource(R.string.error_passwords_dont_match)
    }
}
