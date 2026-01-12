package com.aarevalo.parking.authentication.presentation.forgotpassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aarevalo.parking.authentication.domain.usecase.ForgotPasswordResult
import com.aarevalo.parking.authentication.domain.usecase.ForgotPasswordUseCase
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
class ForgotPasswordViewModel @Inject constructor(
    private val forgotPasswordUseCase: ForgotPasswordUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ForgotPasswordState())
    val state = _state.asStateFlow()

    private val eventChannel = Channel<ForgotPasswordScreenEvent>()
    val events = eventChannel.receiveAsFlow()

    fun onAction(action: ForgotPasswordAction) {
        when (action) {
            is ForgotPasswordAction.OnEmailChanged -> {
                _state.update { it.copy(email = action.email) }
            }

            is ForgotPasswordAction.OnSendResetClick -> {
                sendResetEmail()
            }
        }
    }

    private fun sendResetEmail() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            val result = forgotPasswordUseCase(state.value.email)

            _state.update { it.copy(isLoading = false) }

            when (result) {
                is ForgotPasswordResult.Success -> {
                    Timber.d("Password reset email sent to: ${state.value.email}")
                    _state.update { it.copy(isEmailSent = true) }
                    eventChannel.send(ForgotPasswordScreenEvent.Success)
                }

                is ForgotPasswordResult.ValidationError -> {
                    Timber.d("Forgot password validation error: ${result.error}")
                    eventChannel.send(ForgotPasswordScreenEvent.Error(result.error.toUiText()))
                }

                is ForgotPasswordResult.Error -> {
                    Timber.e("Forgot password error: ${result.message}")
                    eventChannel.send(ForgotPasswordScreenEvent.Error(UiText.DynamicString(result.message)))
                }
            }
        }
    }
}
