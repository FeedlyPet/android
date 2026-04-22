package com.example.feedlypet.ui.devices.schedules

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.feedlypet.R
import com.example.feedlypet.data.network.model.ScheduleDto
import com.example.feedlypet.ui.components.ConfirmDialog
import com.example.feedlypet.ui.components.EmptyScreen
import com.example.feedlypet.ui.components.ErrorScreen
import com.example.feedlypet.ui.components.LoadingScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SchedulesScreen(
    viewModel: SchedulesViewModel = hiltViewModel(),
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val filtered = when (state.filter) {
        ScheduleFilter.ALL -> state.schedules
        ScheduleFilter.ACTIVE -> state.schedules.filter { it.isActive }
        ScheduleFilter.INACTIVE -> state.schedules.filter { !it.isActive }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.schedules_title)) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null) } }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { viewModel.showAddForm() }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.schedules_add))
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item { FilterChip(selected = state.filter == ScheduleFilter.ALL, onClick = { viewModel.setFilter(ScheduleFilter.ALL) }, label = { Text(stringResource(R.string.schedules_filter_all)) }) }
                item { FilterChip(selected = state.filter == ScheduleFilter.ACTIVE, onClick = { viewModel.setFilter(ScheduleFilter.ACTIVE) }, label = { Text(stringResource(R.string.schedules_filter_active)) }) }
                item { FilterChip(selected = state.filter == ScheduleFilter.INACTIVE, onClick = { viewModel.setFilter(ScheduleFilter.INACTIVE) }, label = { Text(stringResource(R.string.schedules_filter_inactive)) }) }
            }

            when {
                state.isLoading -> LoadingScreen()
                state.error != null && state.schedules.isEmpty() -> ErrorScreen(state.error!!, onRetry = { viewModel.loadSchedules() })
                filtered.isEmpty() -> EmptyScreen("📅", stringResource(R.string.schedules_empty))
                else -> LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filtered) { schedule ->
                        ScheduleCard(
                            schedule = schedule,
                            onToggle = { viewModel.toggleSchedule(schedule.id) },
                            onEdit = { viewModel.showEditForm(schedule) },
                            onDelete = { viewModel.showDeleteDialog(schedule) }
                        )
                    }
                }
            }
        }
    }

    if (state.isFormVisible) {
        ScheduleFormSheet(
            schedule = state.editingSchedule,
            onSave = { time, portion, days, active -> viewModel.saveSchedule(time, portion, days, active) },
            onDismiss = { viewModel.hideForm() }
        )
    }

    state.deleteCandidate?.let {
        ConfirmDialog(
            title = stringResource(R.string.schedule_delete_title),
            message = stringResource(R.string.schedule_delete_message),
            confirmLabel = stringResource(R.string.common_delete),
            destructive = true,
            onConfirm = { viewModel.deleteSchedule() },
            onDismiss = { viewModel.hideDeleteDialog() }
        )
    }
}

@Composable
private fun ScheduleCard(schedule: ScheduleDto, onToggle: () -> Unit, onEdit: () -> Unit, onDelete: () -> Unit) {
    var menuExpanded by remember { mutableStateOf(false) }
    val alpha = if (schedule.isActive) 1f else 0.5f
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column {
                Text(schedule.feedingTime, style = MaterialTheme.typography.headlineSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = alpha))
                Text(stringResource(R.string.schedules_portion, schedule.portionSize), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha))
                Text(schedule.daysOfWeek.joinToString(", "), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = alpha))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Switch(checked = schedule.isActive, onCheckedChange = { onToggle() })
                IconButton(onClick = { menuExpanded = true }) { Icon(Icons.Default.MoreVert, null) }
                DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false }) {
                    DropdownMenuItem(text = { Text(stringResource(R.string.common_edit)) }, onClick = { menuExpanded = false; onEdit() })
                    DropdownMenuItem(text = { Text(stringResource(R.string.common_delete), color = MaterialTheme.colorScheme.error) }, onClick = { menuExpanded = false; onDelete() })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScheduleFormSheet(schedule: ScheduleDto?, onSave: (String, Int, List<String>, Boolean) -> Unit, onDismiss: () -> Unit) {
    val allDays = listOf("mon", "tue", "wed", "thu", "fri", "sat", "sun")
    var feedingTime by remember { mutableStateOf(schedule?.feedingTime ?: "08:00") }
    var portionSize by remember { mutableIntStateOf(schedule?.portionSize ?: 100) }
    var selectedDays by remember { mutableStateOf(schedule?.daysOfWeek?.toSet() ?: allDays.toSet()) }
    var isActive by remember { mutableStateOf(schedule?.isActive ?: true) }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState()) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(if (schedule == null) stringResource(R.string.schedules_add) else stringResource(R.string.common_edit), style = MaterialTheme.typography.titleLarge)
            OutlinedTextField(value = feedingTime, onValueChange = { feedingTime = it }, label = { Text("Time (HH:MM)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = portionSize.toString(), onValueChange = { portionSize = it.toIntOrNull() ?: portionSize }, label = { Text(stringResource(R.string.device_portion_size, portionSize)) }, modifier = Modifier.fillMaxWidth())
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(allDays) { day ->
                    FilterChip(selected = day in selectedDays, onClick = {
                        selectedDays = if (day in selectedDays) selectedDays - day else selectedDays + day
                    }, label = { Text(day.take(2).replaceFirstChar { it.uppercase() }) })
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Active")
                Switch(checked = isActive, onCheckedChange = { isActive = it })
            }
            androidx.compose.material3.Button(
                onClick = { if (feedingTime.isNotBlank() && selectedDays.isNotEmpty()) onSave(feedingTime, portionSize, selectedDays.toList(), isActive) },
                modifier = Modifier.fillMaxWidth()
            ) { Text(stringResource(R.string.common_save)) }
        }
    }
}
