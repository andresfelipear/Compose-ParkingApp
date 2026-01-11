package com.aarevalo.parking.authentication.presentation.forgotpassword

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aarevalo.parking.ui.theme.ParkingTheme
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ForgotPasswordScreen(
    onNavigateBack: () -> Unit,
    viewModel: ForgotPasswordViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.navigationEvent.collectLatest { event ->
            when (event) {
                ForgotPasswordNavigationEvent.NavigateBack -> onNavigateBack()
            }
        }
    }

    LaunchedEffect(key1 = state.generalError) {
        state.generalError?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.onEvent(ForgotPasswordEvent.ClearError)
        }
    }

    ForgotPasswordScreenContent(
        state = state,
        snackbarHostState = snackbarHostState,
        onNavigateBack = onNavigateBack,
        onEvent = viewModel::onEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ForgotPasswordScreenContent(
    state: ForgotPasswordState,
    snackbarHostState: SnackbarHostState,
    onNavigateBack: () -> Unit,
    onEvent: (ForgotPasswordEvent) -> Unit
) {
    val focusManager = LocalFocusManager.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reset Password") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (state.isEmailSent) {
                // Success state
                EmailSentContent(
                    email = state.email,
                    onBackToLogin = onNavigateBack
                )
            } else {
                // Input state
                ResetPasswordForm(
                    state = state,
                    onEmailChanged = { onEvent(ForgotPasswordEvent.EmailChanged(it)) },
                    onSendResetEmail = {
                        focusManager.clearFocus()
                        onEvent(ForgotPasswordEvent.SendResetEmail)
                    }
                )
            }
        }
    }
}

@Composable
private fun ResetPasswordForm(
    state: ForgotPasswordState,
    onEmailChanged: (String) -> Unit,
    onSendResetEmail: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    Spacer(modifier = Modifier.height(24.dp))

    Text(
        text = "Forgot your password?",
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.primary
    )

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "Enter your email address and we'll send you a link to reset your password.",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(32.dp))

    OutlinedTextField(
        value = state.email,
        onValueChange = onEmailChanged,
        label = { Text("Email") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Email,
                contentDescription = "Email"
            )
        },
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Done
        ),
        keyboardActions = KeyboardActions(
            onDone = {
                focusManager.clearFocus()
                onSendResetEmail()
            }
        ),
        isError = state.emailError != null,
        supportingText = state.emailError?.let { { Text(it) } },
        singleLine = true,
        modifier = Modifier.fillMaxWidth()
    )

    Spacer(modifier = Modifier.height(24.dp))

    Button(
        onClick = onSendResetEmail,
        enabled = !state.isLoading && state.email.isNotBlank(),
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.height(24.dp)
            )
        } else {
            Text("Send Reset Link")
        }
    }

    Spacer(modifier = Modifier.height(48.dp))
}

@Composable
private fun EmailSentContent(
    email: String,
    onBackToLogin: () -> Unit
) {
    Icon(
        imageVector = Icons.Default.CheckCircle,
        contentDescription = "Success",
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(80.dp)
    )

    Spacer(modifier = Modifier.height(24.dp))

    Text(
        text = "Check your email",
        style = MaterialTheme.typography.headlineMedium,
        color = MaterialTheme.colorScheme.primary
    )

    Spacer(modifier = Modifier.height(16.dp))

    Text(
        text = "We've sent a password reset link to:",
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
        text = email,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(32.dp))

    Text(
        text = "Didn't receive the email? Check your spam folder or try again with a different email address.",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(32.dp))

    TextButton(onClick = onBackToLogin) {
        Text("Back to Sign In")
    }
}

@Preview(showBackground = true)
@Composable
private fun ForgotPasswordScreenPreview() {
    ParkingTheme {
        ForgotPasswordScreenContent(
            state = ForgotPasswordState(),
            snackbarHostState = remember { SnackbarHostState() },
            onNavigateBack = {},
            onEvent = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ForgotPasswordSuccessPreview() {
    ParkingTheme {
        ForgotPasswordScreenContent(
            state = ForgotPasswordState(
                email = "user@example.com",
                isEmailSent = true
            ),
            snackbarHostState = remember { SnackbarHostState() },
            onNavigateBack = {},
            onEvent = {}
        )
    }
}
