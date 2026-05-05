package com.example.simplegymtracker.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simplegymtracker.data.entity.Exercise
import com.example.simplegymtracker.ui.theme.CardTintMint
import com.example.simplegymtracker.ui.theme.CardTintPeach
import com.example.simplegymtracker.ui.theme.CardTintRose
import com.example.simplegymtracker.ui.theme.CardTintSky
import com.example.simplegymtracker.ui.theme.CardTintYellow
import com.example.simplegymtracker.ui.theme.ElectricBlue
import com.example.simplegymtracker.ui.viewmodel.ExerciseViewModel
import com.example.simplegymtracker.ui.viewmodel.ExerciseViewModelFactory

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

    val filteredExercises = exercises.filter {
        it.name?.contains(searchQuery, ignoreCase = true) ?: false ||
        it.category?.contains(searchQuery, ignoreCase = true) ?: false
    }

    val displayExercises = if (searchQuery.isEmpty()) exercises else filteredExercises

    // Animate list entrance
    val visibleIndices = remember { mutableStateListOf<Int>() }
    LaunchedEffect(displayExercises.size, searchQuery) {
        visibleIndices.clear()
        displayExercises.indices.forEach { index ->
            kotlinx.coroutines.delay(30L * index.coerceAtMost(15)) // cap delay for large lists
            visibleIndices.add(index)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Select Exercise",
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
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

            if (displayExercises.isEmpty()) {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(tween(300)) + slideInVertically(tween(300)) { it / 3 }
                ) {
                    EmptyExerciseState(searchQuery = searchQuery)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(displayExercises) { index, exercise ->
                        AnimatedVisibility(
                            visible = index in visibleIndices,
                            enter = fadeIn(tween(250)) + slideInVertically(tween(250)) { 30 }
                        ) {
                            ExerciseCard(
                                exercise = exercise,
                                onClick = { onExerciseSelected(exercise.exerciseId) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddExerciseDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, category ->
                exerciseViewModel.addExercise(name, category, createdByUser = true)
                showAddDialog = false
            }
        )
    }
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
    val categoryColor = when (exercise.category?.lowercase()) {
        "chest" -> CardTintPeach
        "legs" -> CardTintMint
        "back" -> CardTintSky
        "shoulders" -> CardTintRose
        "arms" -> CardTintYellow
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
                Text(
                    text = exercise.category ?: "General",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
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
    onConfirm: (String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }

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
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Category (e.g. Chest, Legs)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            androidx.compose.material3.TextButton(
                onClick = { if (name.isNotBlank()) onConfirm(name, category) }
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
