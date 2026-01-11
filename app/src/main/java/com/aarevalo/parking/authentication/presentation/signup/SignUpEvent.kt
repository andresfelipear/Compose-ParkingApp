package com.aarevalo.parking.authentication.presentation.signup

/**
 * Events that can be triggered from the Sign Up screen.
 */
sealed class SignUpEvent {
    data class EmailChanged(val email: String) : SignUpEvent()
    data class PasswordChanged(val password: String) : SignUpEvent()
    data class ConfirmPasswordChanged(val confirmPassword: String) : SignUpEvent()
    data class DisplayNameChanged(val displayName: String) : SignUpEvent()
    data object TogglePasswordVisibility : SignUpEvent()
    data object ToggleConfirmPasswordVisibility : SignUpEvent()
    data object SignUp : SignUpEvent()
    data object NavigateToLogin : SignUpEvent()
    data object ClearError : SignUpEvent()
}
