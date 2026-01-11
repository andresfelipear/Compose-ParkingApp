package com.aarevalo.parking.authentication.presentation.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aarevalo.parking.authentication.domain.usecase.SignUpResult
import com.aarevalo.parking.authentication.domain.usecase.SignUpUseCase
import com.aarevalo.parking.authentication.presentation.util.toUiText
import com.aarevalo.parking.core.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * ViewModel for the Sign Up screen.
 */
@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SignUpState())
    val state = _state.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<SignUpNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun onEvent(event: SignUpEvent) {
        when (event) {
            is SignUpEvent.EmailChanged -> {
                _state.update { it.copy(email = event.email, emailError = null) }
            }

            is SignUpEvent.PasswordChanged -> {
                _state.update { it.copy(password = event.password, passwordError = null) }
            }

            is SignUpEvent.ConfirmPasswordChanged -> {
                _state.update {
                    it.copy(
                        confirmPassword = event.confirmPassword,
                        confirmPasswordError = null
                    )
                }
            }

            is SignUpEvent.DisplayNameChanged -> {
                _state.update {
                    it.copy(
                        displayName = event.displayName,
                        displayNameError = null
                    )
                }
            }

            is SignUpEvent.TogglePasswordVisibility -> {
                _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            }

            is SignUpEvent.ToggleConfirmPasswordVisibility -> {
                _state.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
            }

            is SignUpEvent.SignUp -> {
                signUp()
            }

            is SignUpEvent.NavigateToLogin -> {
                viewModelScope.launch {
                    _navigationEvent.emit(SignUpNavigationEvent.NavigateToLogin)
                }
            }

            is SignUpEvent.ClearError -> {
                _state.update { it.copy(generalError = null) }
            }
        }
    }

    private fun signUp() {
        viewModelScope.launch {
            _state.update { 
                it.copy(
                    isLoading = true, 
                    generalError = null,
                    emailError = null,
                    passwordError = null,
                    confirmPasswordError = null
                ) 
            }

            val result = signUpUseCase(
                email = state.value.email,
                password = state.value.password,
                confirmPassword = state.value.confirmPassword,
                displayName = state.value.displayName.takeIf { it.isNotBlank() }
            )

            when (result) {
                is SignUpResult.Success -> {
                    Timber.d("Sign up successful")
                    _state.update { it.copy(isLoading = false, isSignUpSuccessful = true) }
                    _navigationEvent.emit(SignUpNavigationEvent.NavigateToHome)
                }

                is SignUpResult.ValidationError -> {
                    Timber.d("Validation error: ${result.error}")
                    _state.update {
                        it.copy(
                            isLoading = false,
                            generalError = result.error.toUiText()
                        )
                    }
                }

                is SignUpResult.Error -> {
                    Timber.e("Sign up error: ${result.message}")
                    _state.update {
                        it.copy(
                            isLoading = false,
                            generalError = UiText.DynamicString(result.message)
                        )
                    }
                }
            }
        }
    }
}

sealed class SignUpNavigationEvent {
    data object NavigateToLogin : SignUpNavigationEvent()
    data object NavigateToHome : SignUpNavigationEvent()
}
