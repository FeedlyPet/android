package com.example.feedlypet.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import com.example.feedlypet.ui.components.AppSnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.feedlypet.R
import com.example.feedlypet.ui.auth.components.AuthTextField

@Composable
fun ResetPasswordScreen(
    token: String,
    onNavigateToLogin: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: ResetPasswordViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(state.event) {
        when (val event = state.event) {
            is ResetPasswordEvent.Success -> {
                snackbarHostState.showSnackbar(context.getString(R.string.auth_reset_password_success))
                onNavigateToLogin()
            }
            is ResetPasswordEvent.ShowError -> snackbarHostState.showSnackbar(event.message.resolve(context))
            null -> Unit
        }
        viewModel.clearEvent()
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AppSnackbarHost(snackbarHostState)
        Text(stringResource(R.string.auth_reset_password_title), style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(24.dp))

        AuthTextField(
            value = state.newPassword,
            onValueChange = { viewModel.onNewPasswordChange(it) },
            label = stringResource(R.string.auth_new_password),
            isPassword = true,
            error = state.newPasswordError?.let { context.getString(it) }
        )
        Spacer(Modifier.height(12.dp))
        AuthTextField(
            value = state.confirmPassword,
            onValueChange = { viewModel.onConfirmPasswordChange(it) },
            label = stringResource(R.string.auth_confirm_new_password),
            isPassword = true,
            error = state.confirmPasswordError?.let { context.getString(it) }
        )
        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { viewModel.resetPassword(token) },
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (state.isLoading) CircularProgressIndicator()
            else Text(stringResource(R.string.auth_reset_password_button))
        }
        Spacer(Modifier.height(8.dp))
        TextButton(onClick = onNavigateBack) { Text(stringResource(R.string.auth_back_to_login)) }
    }
}
