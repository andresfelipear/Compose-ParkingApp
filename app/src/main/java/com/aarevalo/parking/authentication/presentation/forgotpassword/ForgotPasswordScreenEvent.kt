package com.aarevalo.parking.authentication.presentation.forgotpassword

import com.aarevalo.parking.core.presentation.util.UiText

/**
 * One-time events emitted by ForgotPasswordViewModel to be handled by the UI.
 */
sealed interface ForgotPasswordScreenEvent {
    data object Success : ForgotPasswordScreenEvent
    data class Error(val message: UiText) : ForgotPasswordScreenEvent
}
