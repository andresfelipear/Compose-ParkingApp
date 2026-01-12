package com.aarevalo.parking.authentication.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aarevalo.parking.authentication.domain.usecase.SignInResult
import com.aarevalo.parking.authentication.domain.usecase.SignInUseCase
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
class LoginViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    private val eventChannel = Channel<LoginScreenEvent>()
    val events = eventChannel.receiveAsFlow()

    fun onAction(action: LoginAction) {
        when (action) {
            is LoginAction.OnEmailChanged -> {
                _state.update { it.copy(email = action.email) }
            }

            is LoginAction.OnPasswordChanged -> {
                _state.update { it.copy(password = action.password) }
            }

            is LoginAction.OnTogglePasswordVisibility -> {
                _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            }

            is LoginAction.OnLoginClick -> {
                login()
            }

            is LoginAction.OnSignUpClick -> {
                // Handled by the screen directly for navigation
            }

            is LoginAction.OnForgotPasswordClick -> {
                // Handled by the screen directly for navigation
            }
        }
    }

    private fun login() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val result = signInUseCase(
                email = state.value.email,
                password = state.value.password
            )

            _state.update { it.copy(isLoading = false) }

            when (result) {
                is SignInResult.Success -> {
                    Timber.d("Login successful for user: ${result.user.email}")
                    eventChannel.send(LoginScreenEvent.Success)
                }

                is SignInResult.ValidationError -> {
                    Timber.d("Login validation error: ${result.error}")
                    eventChannel.send(LoginScreenEvent.Error(result.error.toUiText()))
                }

                is SignInResult.Error -> {
                    Timber.e("Login error: ${result.message}")
                    eventChannel.send(LoginScreenEvent.Error(UiText.DynamicString(result.message)))
                }
            }
        }
    }
}
