package com.aarevalo.parking.authentication.presentation.signup

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
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val displayNameError: String? = null,
    val generalError: String? = null,
    val isSignUpSuccessful: Boolean = false
)
