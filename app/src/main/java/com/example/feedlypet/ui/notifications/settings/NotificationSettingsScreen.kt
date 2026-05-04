package com.example.feedlypet.ui.notifications.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import com.example.feedlypet.ui.components.AppSnackbarHost
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.feedlypet.R
import com.example.feedlypet.ui.components.LoadingScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingsScreen(
    viewModel: NotificationSettingsViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    state.savedMessage?.let { msg ->
        LaunchedEffect(msg) {
            snackbarHostState.showSnackbar(msg.resolve(context))
            viewModel.clearSavedMessage()
        }
    }

    Scaffold(
        snackbarHost = { AppSnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.notifications_settings_title)) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }
            )
        }
    ) { padding ->
        if (state.isLoading) {
            LoadingScreen()
        } else {
            Column(Modifier.fillMaxSize().padding(padding)) {
                SettingItem(stringResource(R.string.notifications_setting_feeding_success), state.feedingSuccess) { viewModel.updateSetting("feedingSuccess", it) }
                HorizontalDivider()
                SettingItem(stringResource(R.string.notifications_setting_feeding_failed), state.feedingFailed) { viewModel.updateSetting("feedingFailed", it) }
                HorizontalDivider()
                SettingItem(stringResource(R.string.notifications_setting_low_food), state.lowFoodLevel) { viewModel.updateSetting("lowFoodLevel", it) }
                HorizontalDivider()
                SettingItem(stringResource(R.string.notifications_setting_device_status), state.deviceStatus) { viewModel.updateSetting("deviceStatus", it) }
            }
        }
    }
}

@Composable
private fun SettingItem(label: String, checked: Boolean, onToggle: (Boolean) -> Unit) {
    ListItem(
        headlineContent = { Text(label) },
        trailingContent = { Switch(checked = checked, onCheckedChange = onToggle) }
    )
}
