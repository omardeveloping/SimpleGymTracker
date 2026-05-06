package com.example.simplegymtracker.ui.screens

import android.view.HapticFeedbackConstants
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
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
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simplegymtracker.data.entity.Exercise
import com.example.simplegymtracker.data.entity.ExerciseLog
import com.example.simplegymtracker.data.entity.Set
import com.example.simplegymtracker.ui.components.GymTrackerAppBar
import com.example.simplegymtracker.ui.components.SwipeableSetRow
import com.example.simplegymtracker.ui.components.UnitToggle
import com.example.simplegymtracker.ui.theme.ElectricBlue
import com.example.simplegymtracker.ui.theme.LocalGymTrackerColors
import com.example.simplegymtracker.ui.theme.SetComplete
import com.example.simplegymtracker.ui.theme.SetPending
import com.example.simplegymtracker.ui.theme.WarningOrange
import com.example.simplegymtracker.ui.viewmodel.ExerciseViewModel
import com.example.simplegymtracker.ui.viewmodel.ExerciseViewModelFactory
import com.example.simplegymtracker.ui.viewmodel.WorkoutViewModel
import com.example.simplegymtracker.ui.viewmodel.WorkoutViewModelFactory
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActiveWorkoutScreen(
    sessionId: Long,
    selectedExerciseId: Int? = null,
    workoutViewModelFactory: WorkoutViewModelFactory,
    exerciseViewModelFactory: ExerciseViewModelFactory,
    onAddExercise: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToTimer: () -> Unit,
    onFinishWorkout: () -> Unit,
    preferredUnit: String = "kg"
) {
    val workoutViewModel: WorkoutViewModel = viewModel(factory = workoutViewModelFactory)
    val exerciseViewModel: ExerciseViewModel = viewModel(factory = exerciseViewModelFactory)

    LaunchedEffect(selectedExerciseId) {
        if (selectedExerciseId != null && selectedExerciseId != -1) {
            workoutViewModel.addExerciseToSession(sessionId.toInt(), selectedExerciseId)
        }
    }

    val logs by workoutViewModel.getLogsForSession(sessionId.toInt()).collectAsState(initial = emptyList())
    val exercises by exerciseViewModel.allExercises.collectAsState()
    var showFinishDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            GymTrackerAppBar(
                title = "Active Workout",
                onNavigateBack = onNavigateBack,
                actions = {
                    IconButton(onClick = onNavigateToTimer) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Timer",
                            tint = ElectricBlue
                        )
                    }
                    IconButton(onClick = { showFinishDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Finish Workout",
                            tint = SetComplete
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddExercise,
                containerColor = ElectricBlue,
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                },
                text = {
                    Text(
                        text = "Add Exercise",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            )
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
                    EmptyWorkoutState()
                }
            }

            items(count = logs.size, key = { index -> logs[index].exerciseLogId }) { index ->
                val log = logs[index]
                val exercise = exercises.find { it.exerciseId == log.exerciseId }
                ExerciseLogCard(
                    log = log,
                    exercise = exercise,
                    workoutViewModel = workoutViewModel,
                    preferredUnit = preferredUnit,
                    onDeleteLog = { workoutViewModel.deleteLog(log) }
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }

    if (showFinishDialog) {
        FinishWorkoutDialog(
            sessionId = sessionId.toInt(),
            workoutViewModel = workoutViewModel,
            onDismiss = { showFinishDialog = false },
            onConfirm = {
                showFinishDialog = false
                onFinishWorkout()
            }
        )
    }
}

@Composable
private fun EmptyWorkoutState() {
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
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Tap + to add an exercise",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ExerciseLogCard(
    log: ExerciseLog,
    exercise: Exercise?,
    workoutViewModel: WorkoutViewModel,
    preferredUnit: String = "kg",
    onDeleteLog: () -> Unit
) {
    val colors = LocalGymTrackerColors.current
    var expanded by remember { mutableStateOf(true) }
    val sets by workoutViewModel.getSetsForLog(log.exerciseLogId).collectAsState(initial = emptyList())
    var showAddSet by remember { mutableStateOf(false) }
    var lastSetWeight by remember { mutableFloatStateOf(0f) }
    var lastSetReps by remember { mutableIntStateOf(8) }

    LaunchedEffect(exercise?.exerciseId) {
        if (exercise?.exerciseId != null) {
            workoutViewModel.getLastSetForExercise(exercise.exerciseId) { set ->
                set?.let {
                    lastSetWeight = it.weight
                    lastSetReps = it.repetitions
                }
            }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(colors.cardTintMint),
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
                        text = exercise?.name ?: "Unknown Exercise",
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${sets.size} sets",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                IconButton(onClick = onDeleteLog) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn(tween(250)) + slideInVertically(tween(250)) { -20 },
                exit = fadeOut(tween(200)) + slideOutVertically(tween(200)) { -20 }
            ) {
                Column {
                    Spacer(modifier = Modifier.height(12.dp))

                    sets.forEachIndexed { index, set ->
                        SetRowWithSwipe(
                            set = set,
                            setNumber = index + 1,
                            exerciseCategory = exercise?.category,
                            preferredUnit = preferredUnit,
                            onComplete = { workoutViewModel.completeSet(set.setId, true) },
                            onDelete = { workoutViewModel.deleteSet(set) }
                        )
                        if (index < sets.size - 1) {
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }

                    AnimatedVisibility(
                        visible = showAddSet,
                        enter = fadeIn(tween(250)) + slideInVertically(tween(250)) { 30 },
                        exit = fadeOut(tween(200)) + slideOutVertically(tween(200)) { 30 }
                    ) {
                        AddSetForm(
                            initialWeight = lastSetWeight,
                            initialReps = lastSetReps,
                            exerciseCategory = exercise?.category,
                            preferredUnit = preferredUnit,
                            onAdd = { weight, reps, type ->
                                workoutViewModel.addSetToLog(
                                    exerciseLogId = log.exerciseLogId,
                                    weight = weight,
                                    reps = reps,
                                    order = sets.size + 1,
                                    type = type
                                )
                                lastSetWeight = weight
                                lastSetReps = reps
                                showAddSet = false
                            },
                            onCancel = { showAddSet = false }
                        )
                    }

                    AnimatedVisibility(
                        visible = !showAddSet,
                        enter = fadeIn(tween(200)),
                        exit = fadeOut(tween(150))
                    ) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "+ Add Set",
                            color = ElectricBlue,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier
                                .clickable { showAddSet = true }
                                .padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SetRowWithSwipe(
    set: Set,
    setNumber: Int,
    exerciseCategory: String? = null,
    preferredUnit: String = "kg",
    onComplete: () -> Unit,
    onDelete: () -> Unit
) {
    val view = LocalView.current
    var isComplete by remember { mutableStateOf(set.isCompleted) }
    val isCardio = exerciseCategory == "Cardio"

    SwipeableSetRow(
        onSwipeComplete = {
            if (!isComplete) {
                isComplete = true
                view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                onComplete()
            }
        },
        onSwipeDelete = {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            onDelete()
        }
    ) { _, _ ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    if (isComplete) SetComplete.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$setNumber",
                style = MaterialTheme.typography.labelLarge,
                color = if (isComplete) SetComplete else ElectricBlue,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(28.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            if (isCardio) {
                Text(
                    text = "${set.weight}km",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.width(60.dp)
                )
                Text(
                    text = "${set.repetitions}min",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
            } else {
                Text(
                    text = "${set.weight}$preferredUnit",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.width(60.dp)
                )
                Text(
                    text = "${set.repetitions} reps",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
            }
            Text(
                text = set.type,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.width(80.dp)
            )

            if (isComplete) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(SetComplete.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Completed",
                        tint = SetComplete,
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(ElectricBlue.copy(alpha = 0.12f))
                        .clickable {
                            isComplete = true
                            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                            onComplete()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CheckCircle,
                        contentDescription = "Tap to complete",
                        tint = ElectricBlue,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun AddSetForm(
    initialWeight: Float = 0f,
    initialReps: Int = 8,
    exerciseCategory: String? = null,
    preferredUnit: String = "kg",
    onAdd: (Float, Int, String) -> Unit,
    onCancel: () -> Unit
) {
    val colors = LocalGymTrackerColors.current
    val isCardio = exerciseCategory == "Cardio"
    val unitType = if (isCardio) "distance" else "weight"
    val defaultDistance = if (preferredUnit == "mi") 1.0f else 1.6f
    val initialWeightInUnit = if (!isCardio) {
        if (preferredUnit == "lb") initialWeight / 0.453592f else initialWeight
    } else {
        if (preferredUnit == "mi") initialWeight / 1.60934f else initialWeight
    }
    val roundedInitial = "%.1f".format(initialWeightInUnit).toFloat()
    var weightInUnit by remember { mutableFloatStateOf(if (roundedInitial > 0) roundedInitial else if (isCardio) defaultDistance else 20f) }
    var reps by remember { mutableIntStateOf(if (initialReps > 0) initialReps else 8) }
    var type by remember { mutableStateOf("Working Set") }
    var selectedUnit by remember { mutableStateOf(if (isCardio) "km" else preferredUnit) }

    var weightText by remember { mutableStateOf("%.1f".format(weightInUnit).trimEnd('0').trimEnd('.')) }
    var repsText by remember { mutableStateOf(reps.toString()) }

    fun onUnitChange(newUnit: String) {
        if (newUnit == selectedUnit) return
        if (isCardio) {
            val currentKm = if (selectedUnit == "mi") weightInUnit * 1.60934f else weightInUnit
            val converted = if (newUnit == "mi") currentKm / 1.60934f else currentKm
            val rounded = "%.1f".format(converted).toFloat()
            weightInUnit = rounded
            weightText = "%.1f".format(rounded).trimEnd('0').trimEnd('.')
        } else {
            val currentKg = if (selectedUnit == "lb") weightInUnit * 0.453592f else weightInUnit
            val converted = if (newUnit == "lb") currentKg / 0.453592f else currentKg
            val rounded = "%.1f".format(converted).toFloat()
            weightInUnit = rounded
            weightText = "%.1f".format(rounded).trimEnd('0').trimEnd('.')
        }
        selectedUnit = newUnit
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(colors.restTimerBg, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            UnitToggle(
                selectedUnit = selectedUnit,
                onUnitChange = { onUnitChange(it) },
                unitType = unitType
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = weightText,
                onValueChange = {
                    val value = it.toFloatOrNull()
                    if (it.isEmpty() || (value != null && value >= 0f)) {
                        weightText = it
                        weightInUnit = value ?: 0f
                    }
                },
                label = { Text(if (isCardio) "Distance" else "Weight") },
                trailingIcon = { Text(selectedUnit, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(end = 12.dp)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ElectricBlue,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            )

            OutlinedTextField(
                value = repsText,
                onValueChange = {
                    val value = it.toIntOrNull()
                    if (it.isEmpty() || (value != null && value >= 0)) {
                        repsText = it
                        reps = value ?: 0
                    }
                },
                label = { Text(if (isCardio) "Duration (min)" else "Reps") },
                trailingIcon = { Text(if (isCardio) "min" else "reps", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(end = 12.dp)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ElectricBlue,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
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

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "Cancel",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .clickable { onCancel() }
                    .padding(8.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "Add Set",
                color = ElectricBlue,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .clickable {
                        val weightInKg = if (isCardio) {
                            if (selectedUnit == "mi") weightInUnit * 1.60934f else weightInUnit
                        } else {
                            if (selectedUnit == "lb") weightInUnit * 0.453592f else weightInUnit
                        }
                        onAdd(weightInKg, reps, type)
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
    val backgroundColor = if (selected) ElectricBlue else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

    Text(
        text = label,
        style = MaterialTheme.typography.labelLarge,
        color = textColor,
        modifier = Modifier
            .background(backgroundColor, RoundedCornerShape(6.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 8.dp)
    )
}

@Composable
private fun FinishWorkoutDialog(
    sessionId: Int,
    workoutViewModel: WorkoutViewModel,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val logs by workoutViewModel.getLogsForSession(sessionId).collectAsState(initial = emptyList())

    val workoutStats = produceState(initialValue = Pair(0, 0), logs) {
        if (logs.isEmpty()) {
            value = Pair(0, 0)
            return@produceState
        }

        var totalSets = 0
        var completedSets = 0

        for (log in logs) {
            val sets = workoutViewModel.getSetsForLog(log.exerciseLogId).first()
            totalSets += sets.size
            completedSets += sets.count { it.isCompleted }
        }

        value = Pair(totalSets, completedSets)
    }

    val totalSets = workoutStats.value.first
    val completedSets = workoutStats.value.second
    val completionRate = if (totalSets > 0) (completedSets * 100 / totalSets) else 0
    val hasNoSets = totalSets == 0
    val lowCompletion = completionRate < 50

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (hasNoSets) "No sets recorded" else "Finish Workout?",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        text = {
            Column {
                if (hasNoSets) {
                    Text(
                        text = "You haven't completed any sets yet. Are you sure you want to finish this workout?",
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else if (lowCompletion) {
                    Text(
                        text = "You've only completed $completionRate% of your sets.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = WarningOrange
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Are you sure you want to finish?",
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    Text(
                        text = "Great workout! You completed $completionRate% of your sets.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    StatMini(label = "Exercises", value = logs.size.toString())
                    StatMini(label = "Sets", value = "$completedSets/$totalSets")
                    StatMini(label = "Completed", value = "$completionRate%")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (hasNoSets || lowCompletion) WarningOrange else SetComplete
                )
            ) {
                Text(if (hasNoSets) "Finish Anyway" else "Finish Workout")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Continue Workout")
            }
        }
    )
}

@Composable
private fun StatMini(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
