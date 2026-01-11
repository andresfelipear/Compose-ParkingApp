package com.aarevalo.parking.authentication.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aarevalo.parking.authentication.domain.usecase.SignInUseCase
import com.aarevalo.parking.core.domain.util.Resource
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
 * ViewModel for the Login screen.
 */
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state = _state.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<LoginNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> {
                _state.update { it.copy(email = event.email, emailError = null) }
            }

            is LoginEvent.PasswordChanged -> {
                _state.update { it.copy(password = event.password, passwordError = null) }
            }

            is LoginEvent.TogglePasswordVisibility -> {
                _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            }

            is LoginEvent.Login -> {
                login()
            }

            is LoginEvent.NavigateToSignUp -> {
                viewModelScope.launch {
                    _navigationEvent.emit(LoginNavigationEvent.NavigateToSignUp)
                }
            }

            is LoginEvent.NavigateToForgotPassword -> {
                viewModelScope.launch {
                    _navigationEvent.emit(LoginNavigationEvent.NavigateToForgotPassword)
                }
            }

            is LoginEvent.ClearError -> {
                _state.update { it.copy(generalError = null) }
            }
        }
    }

    private fun login() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, generalError = null) }

            when (val result = signInUseCase(state.value.email, state.value.password)) {
                is Resource.Success -> {
                    Timber.d("Login successful")
                    _state.update { it.copy(isLoading = false, isLoginSuccessful = true) }
                    _navigationEvent.emit(LoginNavigationEvent.NavigateToHome)
                }

                is Resource.Error -> {
                    Timber.e("Login error: ${result.message}")
                    _state.update { it.copy(isLoading = false, generalError = result.message) }
                }

                is Resource.Loading -> {
                    // Already handled
                }
            }
        }
    }
}

sealed class LoginNavigationEvent {
    data object NavigateToSignUp : LoginNavigationEvent()
    data object NavigateToForgotPassword : LoginNavigationEvent()
    data object NavigateToHome : LoginNavigationEvent()
}
