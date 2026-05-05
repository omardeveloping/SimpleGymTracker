package com.example.simplegymtracker.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simplegymtracker.data.entity.Exercise
import com.example.simplegymtracker.ui.components.GymTrackerAppBar
import com.example.simplegymtracker.ui.theme.CardTintMint
import com.example.simplegymtracker.ui.theme.CardTintPeach
import com.example.simplegymtracker.ui.theme.CardTintRose
import com.example.simplegymtracker.ui.theme.CardTintSky
import com.example.simplegymtracker.ui.theme.CardTintYellow
import com.example.simplegymtracker.ui.theme.ElectricBlue
import com.example.simplegymtracker.ui.viewmodel.ExerciseViewModel
import com.example.simplegymtracker.ui.viewmodel.ExerciseViewModelFactory

private val categories = listOf("Strength", "Cardio", "Flexibility", "Other")
private val equipmentTypes = listOf("Barbell", "Dumbbell", "Machine", "Cable", "Bodyweight", "Treadmill", "Bike", "Rowing", "Stairmaster")
private val muscleGroups = listOf("Chest", "Back", "Legs", "Shoulders", "Arms", "Core")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExerciseSelectorScreen(
    exerciseViewModelFactory: ExerciseViewModelFactory,
    onExerciseSelected: (Int) -> Unit,
    onNavigateBack: () -> Unit
) {
    val exerciseViewModel: ExerciseViewModel = viewModel(factory = exerciseViewModelFactory)
    val exercises by exerciseViewModel.allExercises.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    val filteredExercises = exercises.filter {
        val matchesSearch = it.name?.contains(searchQuery, ignoreCase = true) ?: false ||
                it.category?.contains(searchQuery, ignoreCase = true) ?: false
        val matchesCategory = selectedCategory == null || it.category == selectedCategory
        matchesSearch && matchesCategory
    }

    val displayExercises = if (searchQuery.isEmpty() && selectedCategory == null) exercises else filteredExercises

    Scaffold(
        topBar = {
            GymTrackerAppBar(
                title = "Select Exercise",
                onNavigateBack = onNavigateBack
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = ElectricBlue,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Custom Exercise",
                    tint = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search exercises...") },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    label = "All",
                    selected = selectedCategory == null,
                    onClick = { selectedCategory = null }
                )
                categories.forEach { category ->
                    FilterChip(
                        label = category,
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = if (selectedCategory == category) null else category }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (displayExercises.isEmpty()) {
                EmptyExerciseState(searchQuery = searchQuery)
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(displayExercises) { _, exercise ->
                        ExerciseCard(
                            exercise = exercise,
                            onClick = { onExerciseSelected(exercise.exerciseId) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddExerciseDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, category, equipment, muscleGroup ->
                exerciseViewModel.addExercise(name, category, equipment, muscleGroup, createdByUser = true)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (selected) ElectricBlue else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

    Text(
        text = label,
        style = MaterialTheme.typography.labelLarge,
        color = textColor,
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun EmptyExerciseState(searchQuery: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 64.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.FitnessCenter,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = if (searchQuery.isEmpty()) "No exercises found" else "No matches for \"$searchQuery\"",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = if (searchQuery.isEmpty()) "Add your first custom exercise" else "Try a different search",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ExerciseCard(
    exercise: Exercise,
    onClick: () -> Unit
) {
    val categoryColor = when (exercise.muscleGroup?.lowercase()) {
        "chest" -> CardTintPeach
        "legs" -> CardTintMint
        "back" -> CardTintSky
        "shoulders" -> CardTintRose
        "arms" -> CardTintYellow
        "core" -> CardTintMint
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(categoryColor, RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = ElectricBlue,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = exercise.name ?: "Unnamed Exercise",
                    style = MaterialTheme.typography.titleLarge
                )
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = exercise.category ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (exercise.equipment != null) {
                        Text(
                            text = "• ${exercise.equipment}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            if (exercise.createdByUser) {
                Text(
                    text = "Custom",
                    style = MaterialTheme.typography.labelMedium,
                    color = ElectricBlue,
                    modifier = Modifier
                        .background(CardTintSky, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@Composable
fun AddExerciseDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Strength") }
    var selectedEquipment by remember { mutableStateOf("Barbell") }
    var selectedMuscleGroup by remember { mutableStateOf("Chest") }
    var equipmentExpanded by remember { mutableStateOf(false) }
    var muscleGroupExpanded by remember { mutableStateOf(false) }

    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Custom Exercise", style = MaterialTheme.typography.headlineSmall) },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Exercise Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("Category", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { category ->
                        FilterChip(
                            label = category,
                            selected = selectedCategory == category,
                            onClick = { selectedCategory = category }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Equipment", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    equipmentTypes.forEach { equipment ->
                        FilterChip(
                            label = equipment,
                            selected = selectedEquipment == equipment,
                            onClick = { selectedEquipment = equipment }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Muscle Group", style = MaterialTheme.typography.labelMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    muscleGroups.forEach { muscle ->
                        FilterChip(
                            label = muscle,
                            selected = selectedMuscleGroup == muscle,
                            onClick = { selectedMuscleGroup = muscle }
                        )
                    }
                }
            }
        },
        confirmButton = {
            androidx.compose.material3.TextButton(
                onClick = {
                    if (name.isNotBlank()) {
                        onConfirm(name, selectedCategory, selectedEquipment, selectedMuscleGroup)
                    }
                }
            ) {
                Text("Add", color = ElectricBlue, style = MaterialTheme.typography.labelLarge)
            }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                Text("Cancel", style = MaterialTheme.typography.labelLarge)
            }
        }
    )
}