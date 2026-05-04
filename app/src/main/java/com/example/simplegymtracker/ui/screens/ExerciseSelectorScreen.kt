package com.example.simplegymtracker.ui.screens

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
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simplegymtracker.data.entity.Exercise
import com.example.simplegymtracker.ui.theme.CardTintMint
import com.example.simplegymtracker.ui.theme.CardTintPeach
import com.example.simplegymtracker.ui.theme.CardTintRose
import com.example.simplegymtracker.ui.theme.CardTintSky
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Select Exercise", fontSize = 20.sp, fontWeight = FontWeight.SemiBold) },
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
                    tint = androidx.compose.ui.graphics.Color.White
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

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search exercises...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = androidx.compose.ui.graphics.Color.Gray)
                },
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(displayExercises) { exercise ->
                    ExerciseCard(
                        exercise = exercise,
                        onClick = { onExerciseSelected(exercise.exerciseId) }
                    )
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
fun ExerciseCard(
    exercise: Exercise,
    onClick: () -> Unit
) {
    val categoryColor = when (exercise.category?.lowercase()) {
        "chest" -> CardTintPeach
        "legs" -> CardTintMint
        "back" -> CardTintSky
        "shoulders" -> CardTintRose
        "arms" -> androidx.compose.ui.graphics.Color(0xFFFEF7D6)
        else -> androidx.compose.ui.graphics.Color(0xFFF0EEEC)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = androidx.compose.ui.graphics.Color.White
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
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = exercise.category ?: "General",
                    fontSize = 13.sp,
                    color = androidx.compose.ui.graphics.Color(0xFF5D5B54)
                )
            }
            if (exercise.createdByUser) {
                Text(
                    text = "Custom",
                    fontSize = 11.sp,
                    color = ElectricBlue,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .background(
                            androidx.compose.ui.graphics.Color(0xFFDCECFA),
                            RoundedCornerShape(4.dp)
                        )
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
        title = { Text("Create Custom Exercise") },
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
                Text("Add", color = ElectricBlue)
            }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
