package com.example.feedlypet.ui.devices.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.feedlypet.R
import com.example.feedlypet.data.network.model.StatisticsDto
import com.example.feedlypet.ui.components.ErrorScreen
import com.example.feedlypet.ui.components.LoadingScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    viewModel: StatisticsViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.statistics_title)) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("week" to R.string.statistics_period_week, "month" to R.string.statistics_period_month, "year" to R.string.statistics_period_year).forEach { (period, labelRes) ->
                    FilterChip(selected = state.period == period, onClick = { viewModel.setPeriod(period) }, label = { Text(stringResource(labelRes)) })
                }
            }

            when {
                state.isLoading -> LoadingScreen()
                state.error != null -> ErrorScreen(state.error!!, onRetry = { viewModel.loadStatistics() })
                state.statistics != null -> StatisticsContent(state.statistics!!)
            }
        }
    }
}

@Composable
private fun StatisticsContent(stats: StatisticsDto) {
    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            androidx.compose.foundation.lazy.grid.LazyVerticalGrid(
                columns = androidx.compose.foundation.lazy.grid.GridCells.Fixed(2),
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                userScrollEnabled = false
            ) {
                item { StatCard(stringResource(R.string.statistics_total_feedings), stats.totalFeedings.toString()) }
                item { StatCard(stringResource(R.string.statistics_total_food), "${stats.totalFood}g") }
                item { StatCard(stringResource(R.string.statistics_avg_portion), "${stats.avgPortion.toInt()}g") }
                item { StatCard(stringResource(R.string.statistics_success_rate), "${(stats.successRate * 100).toInt()}%") }
                item { StatCard(stringResource(R.string.statistics_auto_feedings), stats.autoFeedings.toString()) }
                item { StatCard(stringResource(R.string.statistics_manual_feedings), stats.manualFeedings.toString()) }
            }
        }

        stats.changePercent?.let { change ->
            item {
                val color = if (change >= 0) androidx.compose.ui.graphics.Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
                val sign = if (change >= 0) "+" else ""
                Text("$sign${change.toInt()}% vs previous period", color = color, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

@Composable
private fun StatCard(label: String, value: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(value, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.primary)
            Text(label, style = MaterialTheme.typography.bodySmall)
        }
    }
}
