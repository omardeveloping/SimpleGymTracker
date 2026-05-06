package com.example.simplegymtracker.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simplegymtracker.ui.components.GymTrackerAppBar
import com.example.simplegymtracker.ui.theme.ElectricBlue
import com.example.simplegymtracker.ui.viewmodel.ExerciseViewModelFactory
import com.example.simplegymtracker.ui.viewmodel.WorkoutViewModel
import com.example.simplegymtracker.ui.viewmodel.WorkoutViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressChartScreen(
    workoutViewModelFactory: WorkoutViewModelFactory,
    exerciseViewModelFactory: ExerciseViewModelFactory,
    onNavigateBack: () -> Unit
) {
    val workoutViewModel: WorkoutViewModel = viewModel(factory = workoutViewModelFactory)
    val sessions by workoutViewModel.allSessions.collectAsState()

    val totalWorkouts = sessions.size

    val chartData = remember(sessions) {
        if (sessions.isEmpty()) return@remember emptyList<Pair<Float, Float>>()

        val sortedSessions = sessions.sortedBy { it.date }
        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val sessionsByDay = sortedSessions.groupBy {
            dateFormat.format(Date(it.date))
        }.toSortedMap()

        val points = mutableListOf<Pair<Float, Float>>()
        var cumulativeCount = 0
        sessionsByDay.values.forEachIndexed { index, daySessions ->
            cumulativeCount += daySessions.size
            points.add(index.toFloat() to cumulativeCount.toFloat())
        }

        if (points.size < 2 && sortedSessions.size >= 2) {
            points.clear()
            sortedSessions.forEachIndexed { index, session ->
                points.add(index.toFloat() to (index + 1).toFloat())
            }
        }

        points
    }

    val firstWorkoutDate = if (sessions.isNotEmpty()) {
        val oldest = sessions.minByOrNull { it.date }
        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(oldest?.date ?: 0))
    } else "—"

    val lastWorkoutDate = if (sessions.isNotEmpty()) {
        val newest = sessions.maxByOrNull { it.date }
        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(newest?.date ?: 0))
    } else "—"

    Scaffold(
        topBar = {
            GymTrackerAppBar(
                title = "Progress Chart",
                onNavigateBack = onNavigateBack
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                if (chartData.size < 2) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ShowChart,
                                contentDescription = null,
                                tint = ElectricBlue,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Complete 2+ workouts",
                                style = MaterialTheme.typography.titleLarge,
                                color = ElectricBlue
                            )
                            Text(
                                text = "to see your progress chart",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    AnimatedLineChart(
                        data = chartData,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Statistics",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                StatRow(label = "Total Sessions", value = totalWorkouts.toString())
                StatRow(label = "First Workout", value = firstWorkoutDate)
                StatRow(label = "Last Workout", value = lastWorkoutDate)
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun AnimatedLineChart(
    data: List<Pair<Float, Float>>,
    modifier: Modifier = Modifier
) {
    val lineColor = ElectricBlue
    val pointColor = ElectricBlue
    val gridColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)

    val animationProgress = remember { Animatable(0f) }
    LaunchedEffect(data) {
        animationProgress.snapTo(0f)
        animationProgress.animateTo(1f, animationSpec = tween(1200))
    }

    Canvas(modifier = modifier) {
        if (data.isEmpty()) return@Canvas

        val padding = 40f
        val chartWidth = size.width - padding * 2
        val chartHeight = size.height - padding * 2

        val maxX = data.maxOf { it.first }
        val maxY = data.maxOf { it.second }
        val minY = 0f

        val gridLines = 5
        for (i in 0..gridLines) {
            val y = padding + chartHeight * (i / gridLines.toFloat())
            drawLine(
                color = gridColor,
                start = Offset(padding, y),
                end = Offset(padding + chartWidth, y),
                strokeWidth = 1f
            )
        }

        val points = data.map { (x, y) ->
            val px = padding + (x / maxX) * chartWidth
            val py = if (maxY == minY) {
                padding + chartHeight / 2
            } else {
                padding + chartHeight - ((y - minY) / (maxY - minY)) * chartHeight
            }
            Offset(px, py)
        }

        if (points.size > 1) {
            val path = Path()
            path.moveTo(points[0].x, points[0].y)

            val totalSegments = points.size - 1
            val currentSegment = (animationProgress.value * totalSegments).toInt()
            val segmentProgress = (animationProgress.value * totalSegments) - currentSegment

            for (i in 0 until currentSegment.coerceAtMost(totalSegments)) {
                path.lineTo(points[i + 1].x, points[i + 1].y)
            }

            if (currentSegment < totalSegments && segmentProgress > 0f) {
                val start = points[currentSegment]
                val end = points[currentSegment + 1]
                val partialX = start.x + (end.x - start.x) * segmentProgress
                val partialY = start.y + (end.y - start.y) * segmentProgress
                path.lineTo(partialX, partialY)
            }

            drawPath(
                path = path,
                color = lineColor,
                style = Stroke(width = 3f)
            )
        }

        val visiblePoints = (animationProgress.value * points.size).toInt().coerceAtLeast(1)
        points.take(visiblePoints).forEach { point ->
            drawCircle(
                color = pointColor,
                radius = 6f,
                center = point
            )
            drawCircle(
                color = Color.White,
                radius = 3f,
                center = point
            )
        }
    }
}

@Composable
fun StatRow(label: String, value: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}
