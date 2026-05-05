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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.horizontalScroll
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simplegymtracker.data.entity.Exercise
import com.example.simplegymtracker.data.entity.ExerciseLog
import com.example.simplegymtracker.data.entity.Set
import com.example.simplegymtracker.ui.components.NumericInputField
import com.example.simplegymtracker.ui.components.NumericKeypad
import com.example.simplegymtracker.ui.components.KeypadMode
import com.example.simplegymtracker.ui.components.SwipeableSetRow
import com.example.simplegymtracker.ui.components.UnitToggle
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
    onNavigateToTimer: () -> Unit,
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Active Workout",
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
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
                    tint = MaterialTheme.colorScheme.onPrimary
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
                    EmptyWorkoutState()
                }
            }

            itemsIndexed(logs) { index, log ->
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
                        style = MaterialTheme.typography.titleLarge
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
    onComplete: () -> Unit,
    onDelete: () -> Unit
) {
    val view = LocalView.current
    var isComplete by remember { mutableStateOf(set.isCompleted) }
    val statusColor = if (isComplete) SetComplete else SetPending

    val scale by animateFloatAsState(
        targetValue = if (isComplete) 1.2f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessHigh),
        label = "checkScale"
    )

    SwipeableSetRow(
        onSwipeComplete = {
            isComplete = true
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            onComplete()
        },
        onSwipeDelete = {
            view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
            onDelete()
        }
    ) { _, triggerAnim ->
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    RoundedCornerShape(8.dp)
                )
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$setNumber",
                style = MaterialTheme.typography.labelLarge,
                color = ElectricBlue,
                modifier = Modifier.width(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "${set.weight}kg",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.width(60.dp)
            )
            Text(
                text = "${set.repetitions} reps",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = set.type,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.width(80.dp)
            )
            IconButton(
                onClick = {
                    if (!isComplete) {
                        isComplete = true
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                        triggerAnim()
                        onComplete()
                    }
                },
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = "Complete",
                    tint = statusColor,
                    modifier = Modifier
                        .size(20.dp)
                        .scale(scale)
                )
            }
        }
    }
}

@Composable
fun AddSetForm(
    initialWeight: Float = 0f,
    initialReps: Int = 8,
    preferredUnit: String = "kg",
    onAdd: (Float, Int, String) -> Unit,
    onCancel: () -> Unit
) {
    var weight by remember { mutableFloatStateOf(if (initialWeight > 0) initialWeight else 20f) }
    var reps by remember { mutableIntStateOf(if (initialReps > 0) initialReps else 8) }
    var type by remember { mutableStateOf("Working Set") }
    var selectedUnit by remember { mutableStateOf(preferredUnit) }

    var showKeypad by remember { mutableStateOf(false) }
    var activeField by remember { mutableStateOf<KeypadMode?>(null) }
    var keypadInput by remember { mutableStateOf("") }

    fun openKeypad(mode: KeypadMode) {
        activeField = mode
        keypadInput = when (mode) {
            KeypadMode.WEIGHT -> weight.toString().trimEnd('0').trimEnd('.')
            KeypadMode.REPS -> reps.toString()
        }
        showKeypad = true
    }

    fun handleDigit(digit: String) {
        if (activeField == KeypadMode.WEIGHT) {
            if (keypadInput.isEmpty() && digit == "0") return
            if (keypadInput.contains('.') && keypadInput.substringAfter('.').length >= 1) return
            keypadInput += digit
        } else {
            if (keypadInput.isEmpty() && digit == "0") return
            if (keypadInput.length >= 3) return
            keypadInput += digit
        }
    }

    fun handleDecimal() {
        if (activeField == KeypadMode.WEIGHT && !keypadInput.contains('.')) {
            if (keypadInput.isEmpty()) keypadInput = "0"
            keypadInput += "."
        }
    }

    fun handleBackspace() {
        if (keypadInput.isNotEmpty()) {
            keypadInput = keypadInput.dropLast(1)
        }
    }

    fun handleDone() {
        if (keypadInput.isNotEmpty() && keypadInput != ".") {
            when (activeField) {
                KeypadMode.WEIGHT -> {
                    val newValue = keypadInput.toFloatOrNull()?.coerceIn(0f, 300f)
                    if (newValue != null) weight = newValue
                }
                KeypadMode.REPS -> {
                    val newValue = keypadInput.toIntOrNull()?.coerceIn(1, 100)
                    if (newValue != null) reps = newValue
                }
                null -> {}
            }
        }
        showKeypad = false
        activeField = null
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(RestTimerBg, RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            UnitToggle(
                selectedUnit = selectedUnit,
                onUnitChange = { selectedUnit = it }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            NumericInputField(
                label = "Weight",
                value = weight.toString().trimEnd('0').trimEnd('.'),
                unit = "kg",
                onClick = { openKeypad(KeypadMode.WEIGHT) },
                modifier = Modifier.weight(1f)
            )

            NumericInputField(
                label = "Reps",
                value = reps.toString(),
                unit = "reps",
                onClick = { openKeypad(KeypadMode.REPS) },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

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

        Spacer(modifier = Modifier.height(16.dp))

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
                        val finalWeight = if (selectedUnit == "lb") weight * 0.453592f else weight
                        onAdd(finalWeight, reps, type)
                    }
                    .padding(8.dp)
            )
        }
    }

    NumericKeypad(
        visible = showKeypad,
        currentValue = keypadInput.ifEmpty { "0" },
        mode = activeField ?: KeypadMode.WEIGHT,
        onDigitClick = { handleDigit(it) },
        onDecimalClick = { handleDecimal() },
        onBackspace = { handleBackspace() },
        onDone = { handleDone() }
    )
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