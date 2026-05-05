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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simplegymtracker.data.entity.Session
import com.example.simplegymtracker.ui.theme.CardTintSky
import com.example.simplegymtracker.ui.theme.ElectricBlue
import com.example.simplegymtracker.ui.theme.SetPending
import com.example.simplegymtracker.ui.viewmodel.ExerciseViewModel
import com.example.simplegymtracker.ui.viewmodel.ExerciseViewModelFactory
import com.example.simplegymtracker.ui.viewmodel.WorkoutViewModel
import com.example.simplegymtracker.ui.viewmodel.WorkoutViewModelFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    workoutViewModelFactory: WorkoutViewModelFactory,
    exerciseViewModelFactory: ExerciseViewModelFactory,
    onSessionClick: (Long) -> Unit,
    onNavigateBack: () -> Unit
) {
    val workoutViewModel: WorkoutViewModel = viewModel(factory = workoutViewModelFactory)
    val exerciseViewModel: ExerciseViewModel = viewModel(factory = exerciseViewModelFactory)
    val exercises by exerciseViewModel.allExercises.collectAsState()

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Week", "Month")

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Calendar",
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
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            when (selectedTabIndex) {
                0 -> WeekView(
                    workoutViewModel = workoutViewModel,
                    exercises = exercises.map { it.name ?: "Unknown" },
                    onSessionClick = onSessionClick
                )
                1 -> MonthView(
                    workoutViewModel = workoutViewModel,
                    exercises = exercises.map { it.name ?: "Unknown" },
                    onSessionClick = onSessionClick
                )
            }
        }
    }
}

@Composable
private fun WeekView(
    workoutViewModel: WorkoutViewModel,
    exercises: List<String>,
    onSessionClick: (Long) -> Unit
) {
    var currentWeekStart by remember { mutableLongStateOf(getStartOfWeek(System.currentTimeMillis())) }

    val sessions by workoutViewModel.getSessionsForWeek(currentWeekStart).collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { currentWeekStart -= 7 * 24 * 60 * 60 * 1000L }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous Week")
                }

                Text(
                    text = formatWeekRange(currentWeekStart),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                IconButton(onClick = { currentWeekStart += 7 * 24 * 60 * 60 * 1000L }) {
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next Week")
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
        }

        val calendar = Calendar.getInstance().apply { timeInMillis = currentWeekStart }
        val daysOfWeek = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

        items(7) { dayIndex ->
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY + dayIndex)
            val dayStart = calendar.timeInMillis
            val dayEnd = dayStart + 24 * 60 * 60 * 1000L - 1

            val daySessions = sessions.filter { it.date in dayStart..dayEnd }

            DayCard(
                date = calendar.time,
                dayName = daysOfWeek[dayIndex],
                sessions = daySessions,
                onSessionClick = onSessionClick
            )
        }
    }
}

@Composable
private fun MonthView(
    workoutViewModel: WorkoutViewModel,
    exercises: List<String>,
    onSessionClick: (Long) -> Unit
) {
    var currentYear by remember { mutableIntStateOf(Calendar.getInstance().get(Calendar.YEAR)) }
    var currentMonth by remember { mutableIntStateOf(Calendar.getInstance().get(Calendar.MONTH) + 1) }

    val sessions by workoutViewModel.getSessionsForMonth(currentYear, currentMonth).collectAsState()

    val calendar = Calendar.getInstance().apply {
        set(currentYear, currentMonth - 1, 1)
    }
    val firstDayOfMonth = calendar.timeInMillis

    calendar.add(Calendar.MONTH, 1)
    calendar.add(Calendar.MILLISECOND, -1)
    val lastDayOfMonth = calendar.timeInMillis

    val daysInMonth = (currentMonth..currentMonth).map { calendar.getActualMaximum(Calendar.DAY_OF_MONTH) }.first()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = {
                if (currentMonth == 1) {
                    currentMonth = 12
                    currentYear--
                } else {
                    currentMonth--
                }
            }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = "Previous Month")
            }

            Text(
                text = "$currentYear - ${getMonthName(currentMonth)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = {
                if (currentMonth == 12) {
                    currentMonth = 1
                    currentYear++
                } else {
                    currentMonth++
                }
            }) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = "Next Month")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("Su", "Mo", "Tu", "We", "Th", "Fr", "Sa").forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        val firstWeekday = Calendar.getInstance().apply {
            set(currentYear, currentMonth - 1, 1)
        }.get(Calendar.DAY_OF_WEEK) - 1

        val totalCells = ((firstWeekday + daysInMonth + 6) / 7) * 7
        val cells = (0 until totalCells).map { index ->
            val dayNumber = index - firstWeekday + 1
            if (dayNumber in 1..daysInMonth) dayNumber else null
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(cells) { dayNumber ->
                if (dayNumber != null) {
                    val dayCalendar = Calendar.getInstance().apply {
                        set(currentYear, currentMonth - 1, dayNumber, 0, 0, 0)
                        set(Calendar.MILLISECOND, 0)
                    }
                    val dayStart = dayCalendar.timeInMillis
                    val dayEnd = dayStart + 24 * 60 * 60 * 1000L - 1

                    val hasSession = sessions.any { it.date in dayStart..dayEnd }

                    MonthDayCell(
                        day = dayNumber,
                        hasSession = hasSession,
                        onClick = {
                            val session = sessions.find { it.date in dayStart..dayEnd }
                            session?.let { onSessionClick(it.sessionId.toLong()) }
                        }
                    )
                } else {
                    Box(modifier = Modifier.size(40.dp))
                }
            }
        }
    }
}

@Composable
private fun DayCard(
    date: Date,
    dayName: String,
    sessions: List<Session>,
    onSessionClick: (Long) -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
    val hasWorkout = sessions.isNotEmpty()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = hasWorkout) {
                sessions.firstOrNull()?.let { onSessionClick(it.sessionId.toLong()) }
            },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (hasWorkout) CardTintSky else MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(50.dp)
            ) {
                Text(
                    text = dayName,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = dateFormat.format(date).split(" ")[1],
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            if (hasWorkout) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Workout",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${sessions.size} session(s)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = ElectricBlue,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(
                    text = "Rest Day",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun MonthDayCell(
    day: Int,
    hasSession: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(if (hasSession) ElectricBlue else MaterialTheme.colorScheme.surface)
            .clickable(enabled = hasSession, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = if (hasSession) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun getStartOfWeek(timestamp: Long): Long {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = timestamp
        set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return calendar.timeInMillis
}

private fun formatWeekRange(startOfWeek: Long): String {
    val startDate = Date(startOfWeek)
    val endDate = Date(startOfWeek + 6 * 24 * 60 * 60 * 1000L)
    val format = SimpleDateFormat("MMM dd", Locale.getDefault())
    return "${format.format(startDate)} - ${format.format(endDate)}"
}

private fun getMonthName(month: Int): String {
    val calendar = Calendar.getInstance().apply { set(Calendar.MONTH, month - 1) }
    return SimpleDateFormat("MMMM", Locale.getDefault()).format(calendar.time)
}