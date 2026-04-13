package com.example.feedlypet.ui.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.feedlypet.data.repository.AuthRepository
import com.example.feedlypet.ui.auth.components.AuthButton
import com.example.feedlypet.ui.auth.components.AuthTextField
import com.example.feedlypet.ui.auth.components.PawLogo

@Composable
fun RegisterScreen(
    repository: AuthRepository,
    onRegisterSuccess: (email: String) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val vm: RegisterViewModel = viewModel(factory = RegisterViewModel.Factory(repository))
    val uiState by vm.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is AuthUiState.Success -> onRegisterSuccess(state.message)
            is AuthUiState.Error -> {
                snackbarHostState.showSnackbar(state.message)
                vm.resetState()
            }
            else -> Unit
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(56.dp))
            PawLogo()
            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Create account",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Join FeedlyPet today",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            AuthTextField(
                value = vm.name,
                onValueChange = vm::onNameChange,
                label = "Full Name",
                error = vm.nameError,
                imeAction = ImeAction.Next
            )
            Spacer(modifier = Modifier.height(12.dp))
            AuthTextField(
                value = vm.email,
                onValueChange = vm::onEmailChange,
                label = "Email",
                error = vm.emailError,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
            Spacer(modifier = Modifier.height(12.dp))
            AuthTextField(
                value = vm.password,
                onValueChange = vm::onPasswordChange,
                label = "Password",
                isPassword = true,
                error = vm.passwordError,
                imeAction = ImeAction.Done,
                onImeAction = { vm.register() }
            )

            Spacer(modifier = Modifier.height(24.dp))
            AuthButton(
                text = "Create Account",
                onClick = { vm.register() },
                isLoading = uiState is AuthUiState.Loading
            )

            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = onNavigateToLogin) {
                Text("Already have an account? Sign in")
            }
        }
    }
}
