package com.aarevalo.parking.authentication.presentation.signup

import com.aarevalo.parking.core.presentation.util.UiText

/**
 * UI State for the Sign Up screen.
 */
data class SignUpState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val displayName: String = "",
    val isLoading: Boolean = false,
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val emailError: UiText? = null,
    val passwordError: UiText? = null,
    val confirmPasswordError: UiText? = null,
    val displayNameError: UiText? = null,
    val generalError: UiText? = null,
    val isSignUpSuccessful: Boolean = false
)
