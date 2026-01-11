package com.aarevalo.parking.authentication.presentation.forgotpassword

/**
 * Events that can be triggered from the Forgot Password screen.
 */
sealed class ForgotPasswordEvent {
    data class EmailChanged(val email: String) : ForgotPasswordEvent()
    data object SendResetEmail : ForgotPasswordEvent()
    data object NavigateBack : ForgotPasswordEvent()
    data object ClearError : ForgotPasswordEvent()
}
