package com.aarevalo.parking.authentication.presentation.forgotpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aarevalo.parking.authentication.domain.usecase.ForgotPasswordUseCase
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
 * ViewModel for the Forgot Password screen.
 */
@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val forgotPasswordUseCase: ForgotPasswordUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ForgotPasswordState())
    val state = _state.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<ForgotPasswordNavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun onEvent(event: ForgotPasswordEvent) {
        when (event) {
            is ForgotPasswordEvent.EmailChanged -> {
                _state.update { it.copy(email = event.email, emailError = null) }
            }

            is ForgotPasswordEvent.SendResetEmail -> {
                sendResetEmail()
            }

            is ForgotPasswordEvent.NavigateBack -> {
                viewModelScope.launch {
                    _navigationEvent.emit(ForgotPasswordNavigationEvent.NavigateBack)
                }
            }

            is ForgotPasswordEvent.ClearError -> {
                _state.update { it.copy(generalError = null) }
            }
        }
    }

    private fun sendResetEmail() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, generalError = null) }

            when (val result = forgotPasswordUseCase(state.value.email)) {
                is Resource.Success -> {
                    Timber.d("Password reset email sent")
                    _state.update { it.copy(isLoading = false, isEmailSent = true) }
                }

                is Resource.Error -> {
                    Timber.e("Error sending reset email: ${result.message}")
                    _state.update { it.copy(isLoading = false, generalError = result.message) }
                }

                is Resource.Loading -> {
                    // Already handled
                }
            }
        }
    }
}

sealed class ForgotPasswordNavigationEvent {
    data object NavigateBack : ForgotPasswordNavigationEvent()
}
