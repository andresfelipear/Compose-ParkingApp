package com.aarevalo.parking.authentication.presentation.login

/**
 * User actions that can be performed on the Login screen.
 */
sealed interface LoginAction {
    data class OnEmailChanged(val email: String) : LoginAction
    data class OnPasswordChanged(val password: String) : LoginAction
    data object OnTogglePasswordVisibility : LoginAction
    data object OnLoginClick : LoginAction
    data object OnSignUpClick : LoginAction
    data object OnForgotPasswordClick : LoginAction
}
