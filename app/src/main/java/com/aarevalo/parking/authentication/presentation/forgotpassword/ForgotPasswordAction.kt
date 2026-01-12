package com.aarevalo.parking.authentication.presentation.forgotpassword

/**
 * User actions that can be performed on the Forgot Password screen.
 */
sealed interface ForgotPasswordAction {
    data class OnEmailChanged(val email: String) : ForgotPasswordAction
    data object OnSendResetClick : ForgotPasswordAction
}
