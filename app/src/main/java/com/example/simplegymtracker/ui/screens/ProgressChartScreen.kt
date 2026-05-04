package com.example.simplegymtracker.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simplegymtracker.ui.theme.ElectricBlue
import com.example.simplegymtracker.ui.viewmodel.ExerciseViewModelFactory
import com.example.simplegymtracker.ui.viewmodel.WorkoutViewModel
import com.example.simplegymtracker.ui.viewmodel.WorkoutViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressChartScreen(
    workoutViewModelFactory: WorkoutViewModelFactory,
    exerciseViewModelFactory: ExerciseViewModelFactory,
    onNavigateBack: () -> Unit
) {
    val workoutViewModel: WorkoutViewModel = viewModel(factory = workoutViewModelFactory)
    val sessions by workoutViewModel.allSessions.collectAsState()

    // Prepare chart data
    val chartData = remember(sessions) {
        if (sessions.size < 2) return@remember emptyList<Pair<Float, Float>>()

        val dateFormat = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        val sessionsByDay = sessions.groupBy {
            dateFormat.format(Date(it.date))
        }.toSortedMap()

        val points = mutableListOf<Pair<Float, Float>>()
        var cumulativeCount = 0
        sessionsByDay.values.forEachIndexed { index, daySessions ->
            cumulativeCount += daySessions.size
            points.add(index.toFloat() to cumulativeCount.toFloat())
        }
        points
    }

    val totalWorkouts = sessions.size

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Progress Chart", fontSize = 20.sp, fontWeight = FontWeight.SemiBold) },
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
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            // Chart Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(320.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
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
                                imageVector = Icons.Default.ShowChart,
                                contentDescription = null,
                                tint = ElectricBlue,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Complete 2+ workouts",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = ElectricBlue
                            )
                            Text(
                                text = "to see your progress chart",
                                fontSize = 14.sp,
                                color = Color(0xFF5D5B54)
                            )
                        }
                    }
                } else {
                    // Custom Canvas Chart
                    SimpleLineChart(
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
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Stats summary
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                StatRow(label = "Total Workouts", value = totalWorkouts.toString())
                StatRow(label = "Total Sessions", value = totalWorkouts.toString())
                StatRow(
                    label = "First Workout",
                    value = if (sessions.isNotEmpty()) {
                        SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                            .format(Date(sessions.last().date))
                    } else "—"
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun SimpleLineChart(
    data: List<Pair<Float, Float>>,
    modifier: Modifier = Modifier
) {
    val lineColor = ElectricBlue
    val pointColor = ElectricBlue
    val gridColor = Color(0xFFE5E3DF)
    val textColor = Color(0xFF5D5B54)

    Canvas(modifier = modifier) {
        if (data.isEmpty()) return@Canvas

        val padding = 40f
        val chartWidth = size.width - padding * 2
        val chartHeight = size.height - padding * 2

        val maxX = data.maxOf { it.first }
        val maxY = data.maxOf { it.second }
        val minY = 0f

        // Draw grid lines (horizontal)
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

        // Draw Y-axis labels
        for (i in 0..gridLines) {
            val value = (maxY * (1 - i / gridLines.toFloat())).roundToInt()
            val y = padding + chartHeight * (i / gridLines.toFloat())
            // Note: drawText requires TextMeasurer in newer Compose versions
            // We'll skip text labels in Canvas for simplicity and use overlay
        }

        // Calculate points
        val points = data.map { (x, y) ->
            val px = padding + (x / maxX) * chartWidth
            val py = padding + chartHeight - ((y - minY) / (maxY - minY)) * chartHeight
            Offset(px, py)
        }

        // Draw line
        if (points.size > 1) {
            for (i in 0 until points.size - 1) {
                drawLine(
                    color = lineColor,
                    start = points[i],
                    end = points[i + 1],
                    strokeWidth = 3f
                )
            }
        }

        // Draw points
        points.forEach { point ->
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
            containerColor = Color.White
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
                fontSize = 14.sp,
                color = Color(0xFF5D5B54)
            )
            Text(
                text = value,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
