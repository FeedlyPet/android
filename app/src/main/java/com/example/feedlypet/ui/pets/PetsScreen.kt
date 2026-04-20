package com.example.feedlypet.ui.pets

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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.feedlypet.R
import com.example.feedlypet.data.network.model.PetDto
import com.example.feedlypet.ui.components.ConfirmDialog
import com.example.feedlypet.ui.components.EmptyScreen
import com.example.feedlypet.ui.components.ErrorScreen
import com.example.feedlypet.ui.components.LoadingScreen
import com.example.feedlypet.ui.components.speciesEmoji

@Composable
fun PetsScreen(viewModel: PetsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold { padding ->
        Column(
            Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Search + Add row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = state.searchQuery,
                    onValueChange = { viewModel.onSearchChange(it) },
                    placeholder = { Text(stringResource(R.string.pets_search)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                Button(
                    onClick = { viewModel.showAddForm() },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Text(stringResource(R.string.pets_add), modifier = Modifier.padding(start = 4.dp))
                }
            }

            when {
                state.isLoading -> LoadingScreen()
                state.error != null && state.pets.isEmpty() -> ErrorScreen(state.error!!, onRetry = { viewModel.loadPets() })
                state.pets.isEmpty() -> EmptyScreen("🐾", stringResource(R.string.pets_empty))
                else -> LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(state.pets) { pet ->
                        PetCard(
                            pet = pet,
                            onEdit = { viewModel.showEditForm(pet) },
                            onDelete = { viewModel.showDeleteDialog(pet) }
                        )
                    }
                }
            }
        }
    }

    if (state.isFormVisible) {
        PetFormSheet(
            pet = state.editingPet,
            onSave = { name, species, weight -> viewModel.savePet(name, species, weight) },
            onDismiss = { viewModel.hideForm() }
        )
    }

    state.deleteCandidate?.let { pet ->
        ConfirmDialog(
            title = stringResource(R.string.pet_delete_title),
            message = stringResource(R.string.pet_delete_message, pet.name),
            confirmLabel = stringResource(R.string.common_delete),
            destructive = true,
            onConfirm = { viewModel.deletePet() },
            onDismiss = { viewModel.hideDeleteDialog() }
        )
    }
}

@Composable
private fun PetCard(pet: PetDto, onEdit: () -> Unit, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Text(speciesEmoji(pet.species), style = MaterialTheme.typography.headlineMedium)
                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
            Text(pet.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Surface(shape = RoundedCornerShape(50), color = MaterialTheme.colorScheme.surfaceVariant) {
                    Text(pet.species, style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                }
                Surface(shape = RoundedCornerShape(50), color = MaterialTheme.colorScheme.surfaceVariant) {
                    Text("${pet.weight} Kg", style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PetFormSheet(pet: PetDto?, onSave: (String, String, Double) -> Unit, onDismiss: () -> Unit) {
    val species = listOf("dog", "cat", "bird", "other")
    var name by remember { mutableStateOf(pet?.name ?: "") }
    var selectedSpecies by remember { mutableStateOf(pet?.species ?: "dog") }
    var weight by remember { mutableDoubleStateOf(pet?.weight ?: 1.0) }
    var speciesExpanded by remember { mutableStateOf(false) }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = rememberModalBottomSheetState()) {
        Column(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(if (pet == null) stringResource(R.string.pets_add) else stringResource(R.string.common_edit),
                style = MaterialTheme.typography.titleLarge)

            OutlinedTextField(value = name, onValueChange = { name = it },
                label = { Text(stringResource(R.string.pet_name)) }, modifier = Modifier.fillMaxWidth())

            ExposedDropdownMenuBox(expanded = speciesExpanded, onExpandedChange = { speciesExpanded = it }) {
                OutlinedTextField(
                    value = selectedSpecies,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.pet_species)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = speciesExpanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor()
                )
                ExposedDropdownMenu(expanded = speciesExpanded, onDismissRequest = { speciesExpanded = false }) {
                    species.forEach { s ->
                        DropdownMenuItem(text = { Text("${speciesEmoji(s)} $s") },
                            onClick = { selectedSpecies = s; speciesExpanded = false })
                    }
                }
            }

            OutlinedTextField(
                value = weight.toString(),
                onValueChange = { weight = it.toDoubleOrNull() ?: weight },
                label = { Text(stringResource(R.string.pet_weight)) },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { if (name.isNotBlank()) onSave(name, selectedSpecies, weight) },
                modifier = Modifier.fillMaxWidth()
            ) { Text(stringResource(R.string.common_save)) }

            Spacer(Modifier.height(8.dp))
        }
    }
}
