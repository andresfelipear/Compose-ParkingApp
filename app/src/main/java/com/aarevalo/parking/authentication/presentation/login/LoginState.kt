package com.aarevalo.parking.authentication.presentation.login

import com.aarevalo.parking.core.presentation.util.UiText

/**
 * UI State for the Login screen.
 */
data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isPasswordVisible: Boolean = false,
    val emailError: UiText? = null,
    val passwordError: UiText? = null,
    val generalError: UiText? = null,
    val isLoginSuccessful: Boolean = false
)
