package com.example.feedlypet.ui.notifications

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.feedlypet.R
import com.example.feedlypet.data.network.model.NotificationDto
import com.example.feedlypet.ui.components.EmptyScreen
import com.example.feedlypet.ui.components.ErrorScreen
import com.example.feedlypet.ui.components.LoadingScreen
import com.example.feedlypet.ui.components.formatTimestamp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    viewModel: NotificationsViewModel = hiltViewModel(),
    onUnreadCountChange: (Int) -> Unit = {},
    onNavigateToSettings: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var menuExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(state.notifications) {
        onUnreadCountChange(state.notifications.count { !it.isRead })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.notifications_title)) },
                actions = {
                    IconButton(onClick = { menuExpanded = true }) { Icon(Icons.Default.MoreVert, null) }
                    DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                        DropdownMenuItem(text = { Text(stringResource(R.string.notifications_mark_all_read)) }, onClick = { menuExpanded = false; viewModel.markAllAsRead() })
                        DropdownMenuItem(text = { Text(stringResource(R.string.notifications_settings_title)) }, onClick = { menuExpanded = false; onNavigateToSettings() })
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            LazyRow(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item { FilterChip(selected = !state.unreadOnly, onClick = { viewModel.setUnreadOnly(false) }, label = { Text(stringResource(R.string.notifications_filter_all)) }) }
                item { FilterChip(selected = state.unreadOnly, onClick = { viewModel.setUnreadOnly(true) }, label = { Text(stringResource(R.string.notifications_filter_unread)) }) }
            }

            when {
                state.isLoading -> LoadingScreen()
                state.error != null -> ErrorScreen(state.error!!, onRetry = { viewModel.loadNotifications() })
                state.notifications.isEmpty() -> EmptyScreen("🔔", stringResource(R.string.notifications_empty))
                else -> LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.notifications) { notification ->
                        NotificationCard(
                            notification = notification,
                            onTap = { viewModel.markAsRead(notification.id) },
                            onDelete = { viewModel.deleteNotification(notification.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationCard(notification: NotificationDto, onTap: () -> Unit, onDelete: () -> Unit) {
    val containerColor = if (!notification.isRead) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
    else MaterialTheme.colorScheme.surface

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onTap,
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f)) {
                Text(notification.title, style = MaterialTheme.typography.titleSmall)
                Text(notification.message, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(formatTimestamp(notification.createdAt), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Text(
                when (notification.type) {
                    "FEEDING_SUCCESS" -> "✅"
                    "FEEDING_FAILED" -> "❌"
                    "LOW_FOOD_LEVEL" -> "⚠️"
                    "DEVICE_STATUS" -> "📡"
                    else -> "🔔"
                },
                style = MaterialTheme.typography.headlineSmall
            )
        }
    }
}
