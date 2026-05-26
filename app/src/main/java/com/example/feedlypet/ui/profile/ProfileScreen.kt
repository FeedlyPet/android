package com.example.feedlypet.ui.profile

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.feedlypet.R
import com.example.feedlypet.ui.AppSettingsViewModel
import com.example.feedlypet.ui.components.AppSnackbarHost
import com.example.feedlypet.ui.components.ConfirmDialog
import com.example.feedlypet.ui.components.LoadingScreen
import com.example.feedlypet.ui.theme.BrandCaramel
import com.example.feedlypet.ui.theme.BrandDarkBrown

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    settingsViewModel: AppSettingsViewModel,
    onLogout: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val isDarkTheme by settingsViewModel.isDarkTheme.collectAsStateWithLifecycle()
    val language by settingsViewModel.language.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(state.loggedOut) {
        if (state.loggedOut) onLogout()
    }

    state.successMessage?.let { msg ->
        LaunchedEffect(msg) {
            snackbarHostState.showSnackbar(msg.resolve(context))
            viewModel.clearSuccessMessage()
        }
    }

    Scaffold(snackbarHost = { AppSnackbarHost(snackbarHostState) }) { padding ->
        if (state.isLoading) {
            LoadingScreen()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp, vertical = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    stringResource(R.string.nav_profile),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                // Personal information card
                state.profile?.let { profile ->
                    var name by rememberSaveable { mutableStateOf(profile.name) }
                    var email by rememberSaveable { mutableStateOf(profile.email) }

                    ProfileSectionCard {
                        Text(
                            stringResource(R.string.profile_edit),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                        Spacer(Modifier.height(8.dp))

                        FlatTextField(
                            label = stringResource(R.string.auth_name),
                            value = name,
                            onValueChange = { name = it }
                        )
                        FlatTextField(
                            label = stringResource(R.string.auth_email),
                            value = email,
                            onValueChange = { email = it }
                        )

                        Spacer(Modifier.height(4.dp))
                        Button(
                            onClick = { viewModel.saveProfile(name, email) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(stringResource(R.string.common_save))
                        }
                    }
                }

                // Change password card
                ChangePasswordCard(onChangePassword = { current, new -> viewModel.changePassword(current, new) })

                // App settings card
                ProfileSectionCard {
                    Text(
                        stringResource(R.string.profile_app_settings),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    Spacer(Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            stringResource(R.string.profile_dark_theme),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Switch(
                            checked = isDarkTheme ?: isSystemInDarkTheme(),
                            onCheckedChange = { settingsViewModel.setDarkTheme(it) }
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            stringResource(R.string.profile_language),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = language == "en",
                                onClick = { settingsViewModel.setLanguage("en") },
                                label = { Text("EN") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BrandCaramel,
                                    selectedLabelColor = BrandDarkBrown,
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                            FilterChip(
                                selected = language == "uk",
                                onClick = { settingsViewModel.setLanguage("uk") },
                                label = { Text("UK") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BrandCaramel,
                                    selectedLabelColor = BrandDarkBrown,
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }

                // Logout button
                Button(
                    onClick = { viewModel.showLogoutDialog() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text(
                        stringResource(R.string.profile_logout),
                        color = MaterialTheme.colorScheme.onError
                    )
                }

                Spacer(Modifier.height(16.dp))
            }
        }
    }

    if (state.showLogoutDialog) {
        ConfirmDialog(
            title = stringResource(R.string.profile_logout),
            message = stringResource(R.string.profile_logout_confirm),
            confirmLabel = stringResource(R.string.profile_logout),
            onConfirm = { viewModel.logout() },
            onDismiss = { viewModel.hideLogoutDialog() }
        )
    }
}

@Composable
private fun ProfileSectionCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
            content = content
        )
    }
}

@Composable
private fun FlatTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isPassword: Boolean = false
) {
    Column(modifier = Modifier.padding(bottom = 12.dp)) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                disabledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.outline,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
            ),
            textStyle = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
private fun ChangePasswordCard(onChangePassword: (String, String) -> Unit) {
    var currentPassword by rememberSaveable { mutableStateOf("") }
    var newPassword by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }

    ProfileSectionCard {
        Text(
            stringResource(R.string.profile_change_password),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Spacer(Modifier.height(8.dp))

        FlatTextField(
            label = stringResource(R.string.profile_current_password),
            value = currentPassword,
            onValueChange = { currentPassword = it },
            isPassword = true
        )
        FlatTextField(
            label = stringResource(R.string.profile_new_password),
            value = newPassword,
            onValueChange = { newPassword = it },
            isPassword = true
        )
        FlatTextField(
            label = stringResource(R.string.profile_confirm_new_password),
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            isPassword = true
        )

        Spacer(Modifier.height(4.dp))
        Button(
            onClick = {
                if (currentPassword.isNotBlank() && newPassword.length >= 8 && newPassword == confirmPassword) {
                    onChangePassword(currentPassword, newPassword)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(stringResource(R.string.profile_change_password))
        }
    }
}
