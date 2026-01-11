package com.aarevalo.parking.authentication.presentation.login

/**
 * Events that can be triggered from the Login screen.
 */
sealed class LoginEvent {
    data class EmailChanged(val email: String) : LoginEvent()
    data class PasswordChanged(val password: String) : LoginEvent()
    data object TogglePasswordVisibility : LoginEvent()
    data object Login : LoginEvent()
    data object NavigateToSignUp : LoginEvent()
    data object NavigateToForgotPassword : LoginEvent()
    data object ClearError : LoginEvent()
}
