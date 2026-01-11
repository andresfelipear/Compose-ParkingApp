package com.aarevalo.parking.authentication.presentation.forgotpassword

import com.aarevalo.parking.core.presentation.util.UiText

/**
 * UI State for the Forgot Password screen.
 */
data class ForgotPasswordState(
    val email: String = "",
    val isLoading: Boolean = false,
    val emailError: UiText? = null,
    val generalError: UiText? = null,
    val isEmailSent: Boolean = false
)
