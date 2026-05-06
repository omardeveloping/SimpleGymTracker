package com.example.simplegymtracker.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simplegymtracker.data.entity.Session
import com.example.simplegymtracker.ui.components.GymTrackerAppBar
import com.example.simplegymtracker.ui.theme.CardTintLavender
import com.example.simplegymtracker.ui.theme.CardTintMint
import com.example.simplegymtracker.ui.theme.CardTintPeach
import com.example.simplegymtracker.ui.theme.CardTintSky
import com.example.simplegymtracker.ui.theme.ElectricBlue
import com.example.simplegymtracker.ui.theme.ElectricBlueDeep
import com.example.simplegymtracker.ui.theme.StreakGold
import com.example.simplegymtracker.ui.theme.SurfaceSoft
import com.example.simplegymtracker.ui.theme.IconGreen
import com.example.simplegymtracker.ui.theme.IconPurple
import com.example.simplegymtracker.ui.theme.IconOrange
import com.example.simplegymtracker.ui.theme.IconBlue
import com.example.simplegymtracker.ui.viewmodel.UserViewModel
import com.example.simplegymtracker.ui.viewmodel.UserViewModelFactory
import com.example.simplegymtracker.ui.viewmodel.WorkoutViewModel
import com.example.simplegymtracker.ui.viewmodel.WorkoutViewModelFactory
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userViewModelFactory: UserViewModelFactory,
    workoutViewModelFactory: WorkoutViewModelFactory,
    onStartWorkout: (Long) -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToProgress: () -> Unit,
    onNavigateToTimer: () -> Unit,
    onNavigateToCalendar: () -> Unit
) {
    val userViewModel: UserViewModel = viewModel(factory = userViewModelFactory)
    val workoutViewModel: WorkoutViewModel = viewModel(factory = workoutViewModelFactory)

    val sessions by workoutViewModel.allSessions.collectAsState()
    val recentSessions = sessions.take(3)
    var isStartingSession by remember { mutableStateOf(false) }

    val visibleCardIndices = remember { mutableStateListOf<Int>() }
    LaunchedEffect(recentSessions.size) {
        visibleCardIndices.clear()
        recentSessions.indices.forEach { index ->
            kotlinx.coroutines.delay(60L * index)
            visibleCardIndices.add(index)
        }
    }

    val streak = calculateStreak(sessions)
    val greeting = getTimeBasedGreeting()

    Scaffold(
        topBar = {
            GymTrackerAppBar(title = "Simple Gym Tracker")
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    HeroCard(
                        greeting = greeting,
                        streak = streak,
                        onStartWorkout = {
                            if (isStartingSession) return@HeroCard
                            isStartingSession = true
                            workoutViewModel.startNewSessionOrResume(userId = 1) { sessionId ->
                                onStartWorkout(sessionId)
                                isStartingSession = false
                            }
                        }
                    )
                }

                item {
                    QuickActionsGrid(
                        onNavigateToHistory = onNavigateToHistory,
                        onNavigateToProgress = onNavigateToProgress,
                        onNavigateToTimer = onNavigateToTimer,
                        onNavigateToCalendar = onNavigateToCalendar
                    )
                }

                item {
                    Text(
                        text = "Recent Sessions",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                if (recentSessions.isEmpty()) {
                    item {
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { it / 2 }
                        ) {
                            EmptySessionsState()
                        }
                    }
                } else {
                    itemsIndexed(recentSessions) { index, session ->
                        AnimatedVisibility(
                            visible = index in visibleCardIndices,
                            enter = fadeIn(tween(300)) + slideInVertically(tween(300)) { 40 }
                        ) {
                            SessionCard(
                                session = session,
                                onClick = { onStartWorkout(session.sessionId.toLong()) }
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            StartWorkoutButton(
                onStartWorkout = {
                    if (isStartingSession) return@StartWorkoutButton
                    isStartingSession = true
                    workoutViewModel.startNewSessionOrResume(userId = 1) { sessionId ->
                        onStartWorkout(sessionId)
                        isStartingSession = false
                    }
                }
            )
        }
    }
}

private fun getTimeBasedGreeting(): String {
    val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    return when {
        hour < 12 -> "Good morning"
        hour < 17 -> "Good afternoon"
        hour < 21 -> "Good evening"
        else -> "Late night grind"
    }
}

private fun calculateStreak(sessions: List<Session>): Int {
    if (sessions.isEmpty()) return 0

    val today = Calendar.getInstance()
    today.set(Calendar.HOUR_OF_DAY, 0)
    today.set(Calendar.MINUTE, 0)
    today.set(Calendar.SECOND, 0)
    today.set(Calendar.MILLISECOND, 0)
    val todayStart = today.timeInMillis

    val yesterdayStart = todayStart - TimeUnit.DAYS.toMillis(1)

    val uniqueDays = sessions
        .map { it.date }
        .map { date ->
            val cal = Calendar.getInstance()
            cal.timeInMillis = date
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            cal.timeInMillis
        }
        .distinct()
        .sortedDescending()

    if (uniqueDays.isEmpty()) return 0

    val mostRecent = uniqueDays.first()

    if (mostRecent < yesterdayStart) return 0

    var streak = 1
    for (i in 1 until uniqueDays.size) {
        val expectedPrevious = mostRecent - TimeUnit.DAYS.toMillis(i.toLong())
        if (uniqueDays[i] == expectedPrevious) {
            streak++
        } else {
            break
        }
    }

    return streak
}

@Composable
private fun HeroCard(
    greeting: String,
    streak: Int,
    onStartWorkout: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        colors = listOf(ElectricBlue, ElectricBlueDeep),
                        start = Offset(0f, 0f),
                        end = Offset(1f, 1f)
                    ),
                    RoundedCornerShape(20.dp)
                )
                .padding(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "$greeting!",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White.copy(alpha = 0.9f),
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Ready to crush it?",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.weight(1f))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (streak > 0) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Canvas(
                                modifier = Modifier.size(16.dp)
                            ) {
                                drawCircle(
                                    color = StreakGold,
                                    radius = size.minDimension / 2
                                )
                            }
                            Text(
                                text = "$streak day streak",
                                style = MaterialTheme.typography.labelLarge,
                                color = Color.White.copy(alpha = 0.9f),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.height(1.dp))
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .clickable(onClick = onStartWorkout)
                            .padding(horizontal = 20.dp, vertical = 12.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FitnessCenter,
                                contentDescription = null,
                                tint = ElectricBlue,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Start Workout",
                                color = ElectricBlue,
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickActionsGrid(
    onNavigateToHistory: () -> Unit,
    onNavigateToProgress: () -> Unit,
    onNavigateToTimer: () -> Unit,
    onNavigateToCalendar: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionCard(
                icon = Icons.Default.History,
                label = "History",
                tint = CardTintMint,
                iconColor = IconGreen,
                onClick = onNavigateToHistory,
                modifier = Modifier.weight(1f)
            )
            QuickActionCard(
                icon = Icons.AutoMirrored.Filled.ShowChart,
                label = "Progress",
                tint = CardTintLavender,
                iconColor = IconPurple,
                onClick = onNavigateToProgress,
                modifier = Modifier.weight(1f)
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionCard(
                icon = Icons.Default.Timer,
                label = "Timer",
                tint = CardTintPeach,
                iconColor = IconOrange,
                onClick = onNavigateToTimer,
                modifier = Modifier.weight(1f)
            )
            QuickActionCard(
                icon = Icons.Default.CalendarMonth,
                label = "Calendar",
                tint = CardTintSky,
                iconColor = IconBlue,
                onClick = onNavigateToCalendar,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun QuickActionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: Color,
    iconColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceSoft
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(tint),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = iconColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun EmptySessionsState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.FitnessCenter,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "No workouts yet",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Start your first session!",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun SessionCard(
    session: Session,
    onClick: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) }
    val timeFormat = remember { SimpleDateFormat("HH:mm", Locale.getDefault()) }
    val dateString = dateFormat.format(Date(session.date))
    val timeString = timeFormat.format(Date(session.date))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceSoft
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardTintSky),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = "Session",
                    tint = ElectricBlue,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Workout Session",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = dateString,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Box(
                        modifier = Modifier
                            .size(4.dp)
                            .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(2.dp))
                    )
                    Text(
                        text = timeString,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ShowChart,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}

@Composable
private fun StartWorkoutButton(onStartWorkout: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 16.dp)
            .padding(bottom = 16.dp, top = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(ElectricBlue, ElectricBlueDeep)
                    )
                )
                .clickable(onClick = onStartWorkout)
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
                Text(
                    text = "Start Workout",
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    com.example.simplegymtracker.ui.theme.SimpleGymTrackerTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Text("Home Screen Preview - Requires ViewModels")
        }
    }
}
