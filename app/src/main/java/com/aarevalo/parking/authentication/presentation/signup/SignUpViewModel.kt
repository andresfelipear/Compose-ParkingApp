package com.aarevalo.parking.authentication.presentation.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aarevalo.parking.authentication.domain.usecase.SignUpResult
import com.aarevalo.parking.authentication.domain.usecase.SignUpUseCase
import com.aarevalo.parking.authentication.presentation.util.toUiText
import com.aarevalo.parking.core.presentation.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val signUpUseCase: SignUpUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SignUpState())
    val state = _state.asStateFlow()

    private val eventChannel = Channel<SignUpScreenEvent>()
    val events = eventChannel.receiveAsFlow()

    fun onAction(action: SignUpAction) {
        when (action) {
            is SignUpAction.OnEmailChanged -> {
                _state.update { it.copy(email = action.email) }
            }

            is SignUpAction.OnPasswordChanged -> {
                _state.update { it.copy(password = action.password) }
            }

            is SignUpAction.OnConfirmPasswordChanged -> {
                _state.update { it.copy(confirmPassword = action.confirmPassword) }
            }

            is SignUpAction.OnDisplayNameChanged -> {
                _state.update { it.copy(displayName = action.displayName) }
            }

            is SignUpAction.OnTogglePasswordVisibility -> {
                _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            }

            is SignUpAction.OnToggleConfirmPasswordVisibility -> {
                _state.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
            }

            is SignUpAction.OnSignUpClick -> {
                signUp()
            }

            is SignUpAction.OnLoginClick -> {
                // Handled by the screen directly for navigation
            }
        }
    }

    private fun signUp() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val result = signUpUseCase(
                email = state.value.email,
                password = state.value.password,
                confirmPassword = state.value.confirmPassword,
                displayName = state.value.displayName.takeIf { it.isNotBlank() }
            )

            _state.update { it.copy(isLoading = false) }

            when (result) {
                is SignUpResult.Success -> {
                    Timber.d("Sign up successful for user: ${result.user.email}")
                    eventChannel.send(SignUpScreenEvent.Success)
                }

                is SignUpResult.ValidationError -> {
                    Timber.d("Sign up validation error: ${result.error}")
                    eventChannel.send(SignUpScreenEvent.Error(result.error.toUiText()))
                }

                is SignUpResult.Error -> {
                    Timber.e("Sign up error: ${result.message}")
                    eventChannel.send(SignUpScreenEvent.Error(UiText.DynamicString(result.message)))
                }
            }
        }
    }
}
