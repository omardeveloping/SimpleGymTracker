package com.example.simplegymtracker.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timeline
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
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simplegymtracker.ui.components.GymTrackerAppBar
import com.example.simplegymtracker.ui.theme.CardTintLavender
import com.example.simplegymtracker.ui.theme.CardTintMint
import com.example.simplegymtracker.ui.theme.CardTintPeach
import com.example.simplegymtracker.ui.theme.CardTintSky
import com.example.simplegymtracker.ui.theme.ElectricBlue
import com.example.simplegymtracker.ui.theme.SurfaceSoft
import com.example.simplegymtracker.ui.viewmodel.MonthlyStats
import com.example.simplegymtracker.ui.viewmodel.SessionStats
import com.example.simplegymtracker.ui.viewmodel.WeeklyStats
import com.example.simplegymtracker.ui.viewmodel.WorkoutViewModel
import com.example.simplegymtracker.ui.viewmodel.WorkoutViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionSummaryScreen(
    sessionId: Long,
    workoutViewModelFactory: WorkoutViewModelFactory,
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit
) {
    val workoutViewModel: WorkoutViewModel = viewModel(factory = workoutViewModelFactory)

    var sessionStats by remember { mutableStateOf<SessionStats?>(null) }
    val weeklyStats by workoutViewModel.getWeeklyStats().collectAsState()
    val monthlyStats by workoutViewModel.getMonthlyStats().collectAsState()
    val sessions by workoutViewModel.allSessions.collectAsState()

    var sessionDuration by remember { mutableStateOf("") }

    LaunchedEffect(sessionId) {
        workoutViewModel.getSessionStats(sessionId.toInt()) { stats ->
            sessionStats = stats
        }
    }

    LaunchedEffect(sessionId) {
        val sessions = workoutViewModel.allSessions.value
        val session = sessions.find { it.sessionId.toLong() == sessionId }
        session?.let {
            val now = System.currentTimeMillis()
            val diff = now - it.date
            val hours = diff / (1000 * 60 * 60)
            val minutes = (diff % (1000 * 60 * 60)) / (1000 * 60)
            sessionDuration = if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
        }
    }

    Scaffold(
        topBar = {
            GymTrackerAppBar(
                title = "Session Summary",
                onNavigateBack = onNavigateBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Great Workout!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Session completed in $sessionDuration",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Today's Session",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            sessionStats?.let { stats ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummaryStatCard(
                        icon = Icons.Default.FitnessCenter,
                        label = "Exercises",
                        value = stats.totalExercises.toString(),
                        tint = CardTintMint,
                        modifier = Modifier.weight(1f)
                    )
                    SummaryStatCard(
                        icon = Icons.Default.Repeat,
                        label = "Sets",
                        value = stats.totalSets.toString(),
                        tint = CardTintSky,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SummaryStatCard(
                        icon = Icons.Default.Speed,
                        label = "Volume",
                        value = "${(stats.totalVolume / 1000).toInt()}k kg",
                        tint = CardTintLavender,
                        modifier = Modifier.weight(1f)
                    )
                    SummaryStatCard(
                        icon = Icons.Default.LocalFireDepartment,
                        label = "Reps",
                        value = stats.totalReps.toString(),
                        tint = CardTintPeach,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceSoft),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Completion Rate",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${if (stats.totalSets > 0) (stats.completedSets * 100 / stats.totalSets) else 0}%",
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = ElectricBlue
                            )
                        }
                        Box(
                            modifier = Modifier.size(60.dp)
                        ) {
                            CompletionRing(
                                progress = if (stats.totalSets > 0) stats.completedSets.toFloat() / stats.totalSets else 0f
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "This Week",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            WeeklySummaryCard(weeklyStats)

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "This Month",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            MonthlySummaryCard(monthlyStats)

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Volume Trend",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceSoft),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (sessions.size < 2) {
                        Text(
                            text = "Need 2+ sessions to show trend",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        Text(
                            text = "${sessions.size} sessions logged",
                            style = MaterialTheme.typography.bodyLarge,
                            color = ElectricBlue,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(ElectricBlue)
                    .padding(vertical = 16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Back to Home",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable(onClick = onNavigateHome)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun SummaryStatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceSoft),
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
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(tint),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = ElectricBlue,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun CompletionRing(progress: Float) {
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(progress) {
        animatedProgress.animateTo(progress, animationSpec = tween(1000))
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val strokeWidth = 6f
        val radius = (size.minDimension - strokeWidth) / 2
        val center = Offset(size.width / 2, size.height / 2)

        drawCircle(
            color = Color(0xFFE5E3DF),
            radius = radius,
            style = Stroke(width = strokeWidth)
        )

        drawArc(
            color = ElectricBlue,
            startAngle = -90f,
            sweepAngle = 360f * animatedProgress.value,
            useCenter = false,
            style = Stroke(width = strokeWidth)
        )
    }
}

@Composable
private fun WeeklySummaryCard(stats: WeeklyStats) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceSoft),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WeeklyStatItem(label = "Sessions", value = stats.totalSessions.toString())
                WeeklyStatItem(label = "Sets", value = stats.totalSets.toString())
                WeeklyStatItem(
                    label = "Volume",
                    value = if (stats.totalVolume > 1000) "${(stats.totalVolume / 1000).toInt()}k kg" else "${stats.totalVolume.toInt()} kg"
                )
            }
        }
    }
}

@Composable
private fun WeeklyStatItem(label: String, value: String) {
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

@Composable
private fun MonthlySummaryCard(stats: MonthlyStats) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceSoft),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                WeeklyStatItem(label = "Sessions", value = stats.totalSessions.toString())
                WeeklyStatItem(label = "Sets", value = stats.totalSets.toString())
                WeeklyStatItem(
                    label = "Volume",
                    value = if (stats.totalVolume > 1000) "${(stats.totalVolume / 1000).toInt()}k kg" else "${stats.totalVolume.toInt()} kg"
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (stats.topExercise != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFB800),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Most frequent: ${stats.topExercise} (${stats.topExerciseCount}x)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
