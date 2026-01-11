package com.aarevalo.parking.authentication.presentation.forgotpassword

/**
 * UI State for the Forgot Password screen.
 */
data class ForgotPasswordState(
    val email: String = "",
    val isLoading: Boolean = false,
    val emailError: String? = null,
    val generalError: String? = null,
    val isEmailSent: Boolean = false
)
