package com.aarevalo.parking.authentication.presentation.login

/**
 * UI State for the Login screen.
 * Note: Errors are handled through LoginScreenEvent, not stored in state.
 */
data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isPasswordVisible: Boolean = false
)
