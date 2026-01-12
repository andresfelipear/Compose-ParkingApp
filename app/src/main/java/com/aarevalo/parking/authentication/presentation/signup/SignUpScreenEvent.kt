package com.aarevalo.parking.authentication.presentation.signup

import com.aarevalo.parking.core.presentation.util.UiText

/**
 * One-time events emitted by SignUpViewModel to be handled by the UI.
 */
sealed interface SignUpScreenEvent {
    data object Success : SignUpScreenEvent
    data class Error(val message: UiText) : SignUpScreenEvent
}
