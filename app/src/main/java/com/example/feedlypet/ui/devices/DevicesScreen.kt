package com.example.feedlypet.ui.devices

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.feedlypet.R
import com.example.feedlypet.data.network.model.DeviceDto
import com.example.feedlypet.ui.components.EmptyScreen
import com.example.feedlypet.ui.components.ErrorScreen
import com.example.feedlypet.ui.components.FoodLevelBar
import com.example.feedlypet.ui.components.LoadingScreen
import com.example.feedlypet.ui.components.formatTimestamp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevicesScreen(
    viewModel: DevicesViewModel = hiltViewModel(),
    onNavigateToDetail: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold { padding ->
        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = { viewModel.loadDevices() },
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(R.string.nav_devices),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Button(
                        onClick = { viewModel.showRegisterDialog() },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Text(stringResource(R.string.devices_add), modifier = Modifier.padding(start = 4.dp))
                    }
                }

                when {
                    state.isLoading && state.devices.isEmpty() -> LoadingScreen()
                    state.error != null && state.devices.isEmpty() -> ErrorScreen(state.error!!, onRetry = { viewModel.loadDevices() })
                    state.devices.isEmpty() -> EmptyScreen("📡", stringResource(R.string.devices_empty))
                    else -> LazyColumn(
                        contentPadding = PaddingValues(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.devices) { device ->
                            DeviceCard(
                                device = device,
                                foodLevel = state.foodLevels[device.id],
                                onClick = { onNavigateToDetail(device.id) },
                                onFeed = { onNavigateToDetail(device.id) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (state.showRegisterDialog) {
        RegisterDeviceDialog(
            onRegister = { id, name, loc -> viewModel.registerDevice(id, name, loc) },
            onDismiss = { viewModel.hideRegisterDialog() }
        )
    }

    state.newDevicePassword?.let { password ->
        val clipboardManager = LocalClipboardManager.current
        AlertDialog(
            onDismissRequest = { viewModel.clearNewPassword() },
            title = { Text(stringResource(R.string.device_register_success)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(R.string.device_mqtt_password, password))
                    Text(stringResource(R.string.device_mqtt_warning),
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall)
                }
            },
            confirmButton = {
                Button(onClick = { viewModel.clearNewPassword() }) { Text(stringResource(R.string.common_ok)) }
            },
            dismissButton = {
                TextButton(onClick = { clipboardManager.setText(AnnotatedString(password)) }) {
                    Text(stringResource(R.string.common_copy))
                }
            }
        )
    }
}

@Composable
private fun DeviceCard(device: DeviceDto, foodLevel: Int?, onClick: () -> Unit, onFeed: () -> Unit) {
    val isOnline = device.isOnline
    val onlineColor = Color(0xFF4CAF50)
    val offlineColor = MaterialTheme.colorScheme.outline

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            // Online badge + Details link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (isOnline) onlineColor.copy(alpha = 0.15f) else offlineColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        if (isOnline) "● Online" else "● Offline",
                        color = if (isOnline) onlineColor else offlineColor,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
                TextButton(onClick = onClick) {
                    Text(
                        "Details",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null,
                        modifier = Modifier.padding(start = 2.dp),
                        tint = MaterialTheme.colorScheme.primary)
                }
            }

            // Device name
            Text(device.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)

            // Location
            device.location?.let {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("📍", style = MaterialTheme.typography.bodySmall)
                    Text(it, style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Food level
            FoodLevelBar(level = foodLevel ?: 0)

            // Last seen
            device.lastSeen?.let {
                Text(
                    stringResource(R.string.devices_last_seen, formatTimestamp(it)),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            // Feed now button
            if (isOnline) {
                Button(onClick = onFeed, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.device_feed_now))
                }
            }
        }
    }
}

@Composable
private fun RegisterDeviceDialog(onRegister: (String, String, String?) -> Unit, onDismiss: () -> Unit) {
    var deviceId by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.device_register_title)) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = deviceId, onValueChange = { deviceId = it },
                    label = { Text(stringResource(R.string.device_hardware_id)) },
                    modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = name, onValueChange = { name = it },
                    label = { Text(stringResource(R.string.device_name)) },
                    modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = location, onValueChange = { location = it },
                    label = { Text(stringResource(R.string.device_location_hint)) },
                    modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = {
                if (deviceId.isNotBlank() && name.isNotBlank()) onRegister(deviceId, name, location)
            }) { Text(stringResource(R.string.common_confirm)) }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.common_cancel)) }
        }
    )
}
