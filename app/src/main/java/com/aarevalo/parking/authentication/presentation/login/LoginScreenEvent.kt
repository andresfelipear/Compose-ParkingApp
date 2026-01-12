package com.aarevalo.parking.authentication.presentation.login

import com.aarevalo.parking.core.presentation.util.UiText

/**
 * One-time events emitted by LoginViewModel to be handled by the UI.
 * These events are consumed once and won't be re-emitted on configuration changes.
 */
sealed interface LoginScreenEvent {
    data object Success : LoginScreenEvent
    data class Error(val message: UiText) : LoginScreenEvent
}
