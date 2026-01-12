package com.aarevalo.parking.authentication.presentation.signup

/**
 * UI State for the Sign Up screen.
 * Note: Errors are handled through SignUpScreenEvent, not stored in state.
 */
data class SignUpState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val displayName: String = "",
    val isLoading: Boolean = false,
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false
)
