package com.example.feedlypet.ui.devices.history

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.feedlypet.R
import com.example.feedlypet.data.network.model.FeedingEventDto
import com.example.feedlypet.ui.components.EmptyScreen
import com.example.feedlypet.ui.components.ErrorScreen
import com.example.feedlypet.ui.components.LoadingScreen
import com.example.feedlypet.ui.components.formatTimestamp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.history_title)) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            LazyRow(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item { FilterChip(selected = state.typeFilter == null, onClick = { viewModel.setTypeFilter(null) }, label = { Text(stringResource(R.string.history_type_all)) }) }
                item { FilterChip(selected = state.typeFilter == "automatic", onClick = { viewModel.setTypeFilter("automatic") }, label = { Text(stringResource(R.string.history_type_auto)) }) }
                item { FilterChip(selected = state.typeFilter == "manual", onClick = { viewModel.setTypeFilter("manual") }, label = { Text(stringResource(R.string.history_type_manual)) }) }
                item { FilterChip(selected = state.successFilter == true, onClick = { viewModel.setSuccessFilter(if (state.successFilter == true) null else true) }, label = { Text(stringResource(R.string.history_status_success)) }) }
                item { FilterChip(selected = state.successFilter == false, onClick = { viewModel.setSuccessFilter(if (state.successFilter == false) null else false) }, label = { Text(stringResource(R.string.history_status_failed)) }) }
            }

            when {
                state.isLoading -> LoadingScreen()
                state.error != null -> ErrorScreen(state.error!!, onRetry = { viewModel.loadEvents() })
                state.events.isEmpty() -> EmptyScreen("📋", stringResource(R.string.history_empty))
                else -> LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.events) { event -> EventCard(event) }
                }
            }
        }
    }
}

@Composable
private fun EventCard(event: FeedingEventDto) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(formatTimestamp(event.timestamp), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("${event.portionSize}g", style = MaterialTheme.typography.titleMedium)
                Text(
                    when (event.type.lowercase()) {
                        "manual" -> stringResource(R.string.history_type_manual_label)
                        "automatic" -> stringResource(R.string.history_type_auto_label)
                        else -> event.type
                    },
                    style = MaterialTheme.typography.bodySmall
                )
                event.errorMessage?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.error) }
            }
            Text(if (event.success) "✅" else "❌", style = MaterialTheme.typography.headlineSmall)
        }
    }
}
