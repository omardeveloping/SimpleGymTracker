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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Timer
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simplegymtracker.data.entity.Exercise
import com.example.simplegymtracker.data.entity.ExerciseLog
import com.example.simplegymtracker.data.entity.Set
import com.example.simplegymtracker.ui.theme.CardTintMint
import com.example.simplegymtracker.ui.theme.ElectricBlue
import com.example.simplegymtracker.ui.theme.RestTimerBg
import com.example.simplegymtracker.ui.theme.SetComplete
import com.example.simplegymtracker.ui.theme.SetPending
import com.example.simplegymtracker.ui.viewmodel.ExerciseViewModel
import com.example.simplegymtracker.ui.viewmodel.ExerciseViewModelFactory
import com.example.simplegymtracker.ui.viewmodel.WorkoutViewModel
import com.example.simplegymtracker.ui.viewmodel.WorkoutViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveWorkoutScreen(
    sessionId: Long,
    selectedExerciseId: Int? = null,
    workoutViewModelFactory: WorkoutViewModelFactory,
    exerciseViewModelFactory: ExerciseViewModelFactory,
    onAddExercise: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToTimer: () -> Unit
) {
    val workoutViewModel: WorkoutViewModel = viewModel(factory = workoutViewModelFactory)
    val exerciseViewModel: ExerciseViewModel = viewModel(factory = exerciseViewModelFactory)

    // Handle selected exercise from ExerciseSelector
    LaunchedEffect(selectedExerciseId) {
        if (selectedExerciseId != null && selectedExerciseId != -1) {
            workoutViewModel.addExerciseToSession(sessionId.toInt(), selectedExerciseId)
        }
    }

    val logs by workoutViewModel.getLogsForSession(sessionId.toInt()).collectAsState(initial = emptyList())
    val exercises by exerciseViewModel.allExercises.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Active Workout", fontSize = 20.sp, fontWeight = FontWeight.SemiBold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToTimer) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Timer",
                            tint = ElectricBlue
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddExercise,
                containerColor = ElectricBlue,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Exercise",
                    tint = androidx.compose.ui.graphics.Color.White
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                if (logs.isEmpty()) {
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
                                tint = SetPending,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No exercises yet",
                                fontSize = 16.sp,
                                color = androidx.compose.ui.graphics.Color(0xFF5D5B54)
                            )
                            Text(
                                text = "Tap + to add an exercise",
                                fontSize = 14.sp,
                                color = androidx.compose.ui.graphics.Color(0xFF787671)
                            )
                        }
                    }
                }
            }

            items(logs) { log ->
                val exercise = exercises.find { it.exerciseId == log.exerciseId }
                ExerciseLogCard(
                    log = log,
                    exercise = exercise,
                    workoutViewModel = workoutViewModel,
                    onDeleteLog = { workoutViewModel.deleteLog(log) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ExerciseLogCard(
    log: ExerciseLog,
    exercise: Exercise?,
    workoutViewModel: WorkoutViewModel,
    onDeleteLog: () -> Unit
) {
    var expanded by remember { mutableStateOf(true) }
    val sets by workoutViewModel.getSetsForLog(log.exerciseLogId).collectAsState(initial = emptyList())
    var showAddSet by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = androidx.compose.ui.graphics.Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Exercise Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(CardTintMint),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FitnessCenter,
                        contentDescription = null,
                        tint = ElectricBlue,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = exercise?.name ?: "Unknown Exercise",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${sets.size} sets",
                        fontSize = 13.sp,
                        color = androidx.compose.ui.graphics.Color(0xFF5D5B54)
                    )
                }
                IconButton(onClick = onDeleteLog) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = androidx.compose.ui.graphics.Color(0xFFE03131)
                    )
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))

                // Sets List
                sets.forEachIndexed { index, set ->
                    SetRow(
                        set = set,
                        setNumber = index + 1,
                        onUpdate = { updatedSet ->
                            workoutViewModel.updateSet(updatedSet)
                        }
                    )
                    if (index < sets.size - 1) {
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }

                if (showAddSet) {
                    AddSetForm(
                        onAdd = { weight, reps, type ->
                            workoutViewModel.addSetToLog(
                                exerciseLogId = log.exerciseLogId,
                                weight = weight,
                                reps = reps,
                                order = sets.size + 1,
                                type = type
                            )
                            showAddSet = false
                        },
                        onCancel = { showAddSet = false }
                    )
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "+ Add Set",
                        color = ElectricBlue,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { showAddSet = true }
                    )
                }
            }
        }
    }
}

@Composable
fun SetRow(
    set: Set,
    setNumber: Int,
    onUpdate: (Set) -> Unit
) {
    var isComplete by remember { mutableStateOf(false) }
    val statusColor = if (isComplete) SetComplete else SetPending

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                androidx.compose.ui.graphics.Color(0xFFFAFAF9),
                RoundedCornerShape(8.dp)
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$setNumber",
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = ElectricBlue,
            modifier = Modifier.width(24.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "${set.weight}kg",
            fontSize = 14.sp,
            modifier = Modifier.width(60.dp)
        )
        Text(
            text = "${set.repetitions} reps",
            fontSize = 14.sp,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = set.type,
            fontSize = 12.sp,
            color = androidx.compose.ui.graphics.Color(0xFF787671),
            modifier = Modifier.width(80.dp)
        )
        IconButton(
            onClick = { isComplete = !isComplete },
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = "Complete",
                tint = statusColor,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun AddSetForm(
    onAdd: (Float, Int, String) -> Unit,
    onCancel: () -> Unit
) {
    var weight by remember { mutableStateOf("") }
    var reps by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Working Set") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(RestTimerBg, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = weight,
                onValueChange = { weight = it },
                label = { Text("Weight (kg)") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = reps,
                onValueChange = { reps = it },
                label = { Text("Reps") },
                singleLine = true,
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SetTypeChip(
                label = "Warm-up",
                selected = type == "Warm-up",
                onClick = { type = "Warm-up" }
            )
            SetTypeChip(
                label = "Working Set",
                selected = type == "Working Set",
                onClick = { type = "Working Set" }
            )
            SetTypeChip(
                label = "Drop Set",
                selected = type == "Drop Set",
                onClick = { type = "Drop Set" }
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row {
            Text(
                text = "Cancel",
                color = androidx.compose.ui.graphics.Color(0xFF5D5B54),
                modifier = Modifier
                    .clickable { onCancel() }
                    .padding(8.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Add Set",
                color = ElectricBlue,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .clickable {
                        val w = weight.toFloatOrNull() ?: 0f
                        val r = reps.toIntOrNull() ?: 0
                        if (w > 0 && r > 0) onAdd(w, r, type)
                    }
                    .padding(8.dp)
            )
        }
    }
}

@Composable
fun SetTypeChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (selected) ElectricBlue else androidx.compose.ui.graphics.Color(0xFFF0EEEC)
    val textColor = if (selected) androidx.compose.ui.graphics.Color.White else androidx.compose.ui.graphics.Color(0xFF37352F)

    Text(
        text = label,
        fontSize = 12.sp,
        fontWeight = if (selected) FontWeight.Medium else FontWeight.Normal,
        color = textColor,
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    )
}
