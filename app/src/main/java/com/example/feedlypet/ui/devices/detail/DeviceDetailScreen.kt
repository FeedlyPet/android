package com.example.feedlypet.ui.devices.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHostState
import com.example.feedlypet.ui.components.AppSnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.feedlypet.R
import com.example.feedlypet.ui.components.ConfirmDialog
import com.example.feedlypet.ui.components.ErrorScreen
import com.example.feedlypet.ui.components.FoodLevelBar
import com.example.feedlypet.ui.components.LoadingScreen
import com.example.feedlypet.ui.components.StatusChip
import com.example.feedlypet.ui.components.formatTimestamp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceDetailScreen(
    viewModel: DeviceDetailViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onNavigateToSchedules: (String) -> Unit,
    onNavigateToHistory: (String) -> Unit,
    onNavigateToStatistics: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    var menuExpanded by remember { mutableStateOf(false) }

    state.feedingSuccess?.let { msg ->
        LaunchedEffect(msg) {
            snackbarHostState.showSnackbar(msg.resolve(context))
            viewModel.clearFeedingSuccess()
        }
    }

    LaunchedEffect(state.deleted) {
        if (state.deleted) onBack()
    }

    Scaffold(
        snackbarHost = { AppSnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text(state.device?.name ?: stringResource(R.string.device_detail_title)) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null) } },
                actions = {
                    IconButton(onClick = { menuExpanded = true }) { Icon(Icons.Default.MoreVert, contentDescription = null) }
                    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                        DropdownMenuItem(text = { Text(stringResource(R.string.device_regen_password_title)) }, onClick = { menuExpanded = false; viewModel.showRegenDialog() })
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.device_delete_title), color = MaterialTheme.colorScheme.error) },
                            onClick = { menuExpanded = false; viewModel.showDeleteDialog() }
                        )
                    }
                }
            )
        }
    ) { padding ->
        when {
            state.isLoading -> LoadingScreen()
            state.error != null && state.device == null -> ErrorScreen(state.error!!, onRetry = { viewModel.loadData() })
            else -> {
                val device = state.device ?: return@Scaffold
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Text(stringResource(R.string.device_food_level), style = MaterialTheme.typography.titleMedium)
                                    StatusChip(isOnline = device.isOnline)
                                }
                                Text("${state.foodLevel}%", style = MaterialTheme.typography.headlineLarge, color = MaterialTheme.colorScheme.primary)
                                FoodLevelBar(level = state.foodLevel)
                            }
                        }
                    }

                    item {
                        Button(
                            onClick = { viewModel.showFeedDialog() },
                            enabled = device.isOnline && !state.isFeedingLoading,
                            modifier = Modifier.fillMaxWidth()
                        ) { Text(stringResource(R.string.device_feed_now)) }
                    }

                    if (state.activeSchedules.isNotEmpty()) {
                        item { Text(stringResource(R.string.schedules_title), style = MaterialTheme.typography.titleMedium) }
                        items(state.activeSchedules) { schedule ->
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(schedule.feedingTime, style = MaterialTheme.typography.titleMedium)
                                    Text(stringResource(R.string.schedules_portion, schedule.portionSize))
                                }
                            }
                        }
                        item {
                            TextButton(onClick = { onNavigateToSchedules(device.id) }) {
                                Text(stringResource(R.string.device_view_schedules))
                            }
                        }
                    }

                    if (state.recentEvents.isNotEmpty()) {
                        item { Text(stringResource(R.string.home_recent_events), style = MaterialTheme.typography.titleMedium) }
                        items(state.recentEvents) { event ->
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Row(modifier = Modifier.padding(12.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                    Column {
                                        Text(formatTimestamp(event.timestamp), style = MaterialTheme.typography.bodySmall)
                                        Text("${event.portionSize}g • ${event.type}")
                                    }
                                    Text(if (event.success) "✅" else "❌")
                                }
                            }
                        }
                        item {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                TextButton(onClick = { onNavigateToHistory(device.id) }) { Text(stringResource(R.string.device_view_history)) }
                                TextButton(onClick = { onNavigateToStatistics(device.id) }) { Text(stringResource(R.string.device_view_statistics)) }
                            }
                        }
                    }
                }
            }
        }
    }

    if (state.showFeedDialog) {
        var portion by remember { mutableFloatStateOf(100f) }
        AlertDialog(
            onDismissRequest = { viewModel.hideFeedDialog() },
            title = { Text(stringResource(R.string.device_feed_now)) },
            text = {
                Column {
                    Text(stringResource(R.string.device_portion_size, portion.toInt()))
                    Spacer(Modifier.height(8.dp))
                    Slider(value = portion, onValueChange = { portion = it }, valueRange = 10f..500f, steps = 48)
                }
            },
            confirmButton = { Button(onClick = { viewModel.feed(portion.toInt()) }) { Text(stringResource(R.string.common_confirm)) } },
            dismissButton = { TextButton(onClick = { viewModel.hideFeedDialog() }) { Text(stringResource(R.string.common_cancel)) } }
        )
    }

    if (state.showDeleteDialog) {
        ConfirmDialog(
            title = stringResource(R.string.device_delete_title),
            message = stringResource(R.string.device_delete_message, state.device?.name ?: ""),
            confirmLabel = stringResource(R.string.common_delete),
            destructive = true,
            onConfirm = { viewModel.deleteDevice() },
            onDismiss = { viewModel.hideDeleteDialog() }
        )
    }

    if (state.showRegenDialog) {
        ConfirmDialog(
            title = stringResource(R.string.device_regen_password_title),
            message = stringResource(R.string.device_regen_password_message),
            onConfirm = { viewModel.regeneratePassword() },
            onDismiss = { viewModel.hideRegenDialog() }
        )
    }

    state.newPassword?.let { password ->
        AlertDialog(
            onDismissRequest = { viewModel.clearNewPassword() },
            title = { Text(stringResource(R.string.device_regen_password_title)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(R.string.device_mqtt_password, password))
                    Text(stringResource(R.string.device_mqtt_warning), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            },
            confirmButton = { Button(onClick = { viewModel.clearNewPassword() }) { Text(stringResource(R.string.common_ok)) } }
        )
    }
}
