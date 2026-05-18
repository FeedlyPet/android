package com.example.feedlypet.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import com.example.feedlypet.ui.components.AppSnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.feedlypet.R

@Composable
fun VerifyEmailTokenScreen(
    token: String,
    onNavigateToLogin: () -> Unit,
    viewModel: VerifyEmailTokenViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.verifyEmail(token)
    }

    LaunchedEffect(state.event) {
        when (val event = state.event) {
            is VerifyEmailTokenEvent.Success -> {
                snackbarHostState.showSnackbar(context.getString(R.string.auth_reset_password_success))
                onNavigateToLogin()
            }
            is VerifyEmailTokenEvent.ShowError -> snackbarHostState.showSnackbar(event.message.resolve(context))
            null -> Unit
        }
        viewModel.clearEvent()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AppSnackbarHost(snackbarHostState)
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (state.isLoading) {
                CircularProgressIndicator()
                Text(stringResource(R.string.auth_verify_email_title), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(top = 16.dp))
            } else {
                Text(stringResource(R.string.auth_verify_email_message), style = MaterialTheme.typography.bodyLarge, textAlign = TextAlign.Center)
            }
        }
    }
}
