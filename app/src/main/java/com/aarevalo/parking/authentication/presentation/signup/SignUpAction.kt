package com.aarevalo.parking.authentication.presentation.signup

/**
 * User actions that can be performed on the Sign Up screen.
 */
sealed interface SignUpAction {
    data class OnEmailChanged(val email: String) : SignUpAction
    data class OnPasswordChanged(val password: String) : SignUpAction
    data class OnConfirmPasswordChanged(val confirmPassword: String) : SignUpAction
    data class OnDisplayNameChanged(val displayName: String) : SignUpAction
    data object OnTogglePasswordVisibility : SignUpAction
    data object OnToggleConfirmPasswordVisibility : SignUpAction
    data object OnSignUpClick : SignUpAction
    data object OnLoginClick : SignUpAction
}
