package com.aarevalo.parking.authentication.presentation.forgotpassword

/**
 * UI State for the Forgot Password screen.
 * Note: Errors are handled through ForgotPasswordScreenEvent, not stored in state.
 */
data class ForgotPasswordState(
    val email: String = "",
    val isLoading: Boolean = false,
    val isEmailSent: Boolean = false
)
