package com.example.feedlypet.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHostState
import com.example.feedlypet.ui.components.AppSnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.feedlypet.R
import com.example.feedlypet.data.network.model.DeviceDto
import com.example.feedlypet.data.network.model.FeedingEventDto
import com.example.feedlypet.data.network.model.StatisticsDto
import com.example.feedlypet.ui.components.ErrorScreen
import com.example.feedlypet.ui.components.FoodLevelBar
import com.example.feedlypet.ui.components.LoadingScreen
import com.example.feedlypet.ui.components.StatusChip
import com.example.feedlypet.ui.components.formatTimestamp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onUnreadCountChange: (Int) -> Unit = {},
    onNavigateToNotifications: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    LaunchedEffect(lifecycle) {
        lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.loadData()
        }
    }

    LaunchedEffect(state.unreadNotifications) {
        onUnreadCountChange(state.unreadNotifications)
    }
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    state.feedingSuccess?.let { msg ->
        LaunchedEffect(msg) {
            snackbarHostState.showSnackbar(msg.resolve(context))
            viewModel.clearFeedingSuccess()
        }
    }

    Scaffold(snackbarHost = { AppSnackbarHost(snackbarHostState) }) { padding ->
        PullToRefreshBox(
            isRefreshing = state.isLoading,
            onRefresh = { viewModel.loadData() },
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            when {
                state.isLoading && state.devices.isEmpty() -> LoadingScreen()
                state.error != null && state.devices.isEmpty() -> ErrorScreen(
                    message = state.error!!,
                    onRetry = { viewModel.loadData() }
                )
                else -> HomeContent(
                    state = state,
                    onFeed = { deviceId, portion -> viewModel.feed(deviceId, portion) }
                )
            }
        }
    }
}

@Composable
private fun HomeContent(
    state: HomeUiState,
    onFeed: (String, Int) -> Unit
) {
    var feedingDevice by remember { mutableStateOf<DeviceDto?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                stringResource(R.string.home_title),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // Summary cards — 4 в ряд как на вебе
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SummaryCard(
                    label = stringResource(R.string.home_pets_count),
                    value = state.petsCount.toString(),
                    icon = Icons.Default.Settings,
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    label = stringResource(R.string.home_devices_status),
                    value = "${state.devicesOnline} / ${state.devicesOnline + state.devicesOffline}",
                    icon = Icons.Default.Settings,
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    label = stringResource(R.string.home_unread_notifications),
                    value = state.unreadNotifications.toString(),
                    icon = Icons.Default.Notifications,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // This week stats
        state.weekStats?.let { stats ->
            item { WeekStatsSection(stats) }
        }

        // Devices
        if (state.devices.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        stringResource(R.string.home_devices_section),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            items(state.devices.take(5)) { device ->
                DeviceCard(device = device, foodLevel = state.foodLevels[device.id], onFeed = { feedingDevice = device })
            }
        }

        // Recent Events
        if (state.recentEvents.isNotEmpty()) {
            item {
                Text(
                    stringResource(R.string.home_recent_events),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            item {
                Card(modifier = Modifier.fillMaxWidth()) {
                    // Header row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Time", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1.5f))
                        Text("Type", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f))
                        Text("Portion", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f))
                        Text("Status", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(0.6f))
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    state.recentEvents.take(5).forEachIndexed { index, event ->
                        EventRow(event)
                        if (index < state.recentEvents.size - 1) {
                            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f))
                        }
                    }
                }
            }
        }
    }

    feedingDevice?.let { device ->
        FeedPortionDialog(
            deviceName = device.name,
            onConfirm = { portion ->
                onFeed(device.id, portion)
                feedingDevice = null
            },
            onDismiss = { feedingDevice = null }
        )
    }
}

@Composable
private fun SummaryCard(label: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun WeekStatsSection(stats: StatisticsDto) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
                stringResource(R.string.home_this_week),
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                WeekStatItem(
                    label = stringResource(R.string.stats_feedings),
                    value = stats.totalFeedings.toString(),
                    modifier = Modifier.weight(1f)
                )
                WeekStatItem(
                    label = stringResource(R.string.stats_food_served),
                    value = "${stats.totalFood}g",
                    modifier = Modifier.weight(1f)
                )
                WeekStatItem(
                    label = stringResource(R.string.stats_avg_portion),
                    value = if (stats.avgPortion > 0) "${stats.avgPortion.toInt()}g" else "—",
                    modifier = Modifier.weight(1f)
                )
                WeekStatItem(
                    label = stringResource(R.string.stats_success_rate),
                    value = if (stats.successRate > 0) "${(stats.successRate * 100).toInt()}%" else "—",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun WeekStatItem(label: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Text(value, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun DeviceCard(device: DeviceDto, foodLevel: Int?, onFeed: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(device.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                StatusChip(isOnline = device.isOnline)
            }
            if (foodLevel != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(stringResource(R.string.device_food_level), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("$foodLevel%", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                FoodLevelBar(level = foodLevel)
            }
            if (device.isOnline) {
                Button(onClick = onFeed, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.device_feed_now))
                }
            }
        }
    }
}

@Composable
private fun EventRow(event: FeedingEventDto) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(formatTimestamp(event.timestamp), style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1.5f))
        Surface(
            shape = RoundedCornerShape(50),
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
            modifier = Modifier.weight(1f)
        ) {
            Text(
                event.type,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }
        Text("${event.portionSize}g", style = MaterialTheme.typography.bodySmall, modifier = Modifier.weight(1f))
        Text(if (event.success) "✓" else "✗", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold,
            color = if (event.success) androidx.compose.ui.graphics.Color(0xFF4CAF50) else MaterialTheme.colorScheme.error,
            modifier = Modifier.weight(0.6f))
    }
}

@Composable
private fun FeedPortionDialog(deviceName: String, onConfirm: (Int) -> Unit, onDismiss: () -> Unit) {
    var portion by remember { mutableFloatStateOf(100f) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(deviceName) },
        text = {
            Column {
                Text(stringResource(R.string.device_portion_size, portion.toInt()))
                Spacer(Modifier.height(8.dp))
                Slider(value = portion, onValueChange = { portion = it }, valueRange = 10f..500f, steps = 48)
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(portion.toInt()) }) {
                Text(stringResource(R.string.common_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(stringResource(R.string.common_cancel)) }
        }
    )
}
