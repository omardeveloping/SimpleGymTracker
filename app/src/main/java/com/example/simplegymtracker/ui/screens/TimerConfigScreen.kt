package com.example.simplegymtracker.ui.screens

import android.media.AudioManager
import android.media.ToneGenerator
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simplegymtracker.ui.components.GymTrackerAppBar
import com.example.simplegymtracker.ui.theme.ElectricBlue
import com.example.simplegymtracker.ui.theme.ElectricBlueDeep
import com.example.simplegymtracker.ui.theme.SurfaceSoft
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerConfigScreen(
    onNavigateBack: () -> Unit
) {
    var setRestTime by remember { mutableStateOf("60") }
    var exerciseRestTime by remember { mutableStateOf("120") }
    var isTimerRunning by remember { mutableStateOf(false) }
    var remainingTime by remember { mutableIntStateOf(0) }
    var totalTime by remember { mutableIntStateOf(0) }
    var currentProgress by remember { mutableFloatStateOf(1f) }

    val animatedProgress by animateFloatAsState(
        targetValue = currentProgress,
        animationSpec = tween(durationMillis = 300, easing = LinearEasing),
        label = "timerProgress"
    )

    val toneGenerator = remember { ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100) }

    LaunchedEffect(isTimerRunning, remainingTime, totalTime) {
        if (isTimerRunning && remainingTime > 0) {
            while (remainingTime > 0 && isTimerRunning) {
                delay(1000L)
                if (isTimerRunning) {
                    remainingTime -= 1
                    currentProgress = if (totalTime > 0) remainingTime.toFloat() / totalTime else 0f
                    if (remainingTime <= 5 && remainingTime > 0) {
                        toneGenerator.startTone(ToneGenerator.TONE_PROP_BEEP, 100)
                    }
                }
            }
            if (remainingTime <= 0) {
                isTimerRunning = false
                toneGenerator.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 500)
            }
        }
    }

    fun startTimer(seconds: Int) {
        totalTime = seconds
        remainingTime = seconds
        currentProgress = 1f
        isTimerRunning = true
    }

    fun resetTimer() {
        isTimerRunning = false
        remainingTime = 0
        totalTime = 0
        currentProgress = 1f
    }

    Scaffold(
        topBar = {
            GymTrackerAppBar(
                title = "Rest Timer",
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

            TimerCircle(
                time = if (remainingTime > 0) formatTime(remainingTime) else "00:00",
                progress = animatedProgress,
                isRunning = isTimerRunning,
                hasTimeSet = remainingTime > 0 || totalTime > 0,
                remainingTime = remainingTime,
                onStartPause = {
                    if (remainingTime == 0 && totalTime == 0) return@TimerCircle
                    isTimerRunning = !isTimerRunning
                },
                onReset = { resetTimer() }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TimerPresetButton(
                    label = "Set Rest",
                    time = "${setRestTime.toIntOrNull() ?: 60}s",
                    onClick = { startTimer(setRestTime.toIntOrNull() ?: 60) },
                    modifier = Modifier.weight(1f)
                )
                TimerPresetButton(
                    label = "Exercise Rest",
                    time = "${exerciseRestTime.toIntOrNull() ?: 120}s",
                    onClick = { startTimer(exerciseRestTime.toIntOrNull() ?: 120) },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Custom Duration",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceSoft),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = setRestTime,
                        onValueChange = { if (it.isEmpty() || it.all { c -> c.isDigit() }) setRestTime = it },
                        label = { Text("Rest between sets (seconds)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                    )

                    OutlinedTextField(
                        value = exerciseRestTime,
                        onValueChange = { if (it.isEmpty() || it.all { c -> c.isDigit() }) exerciseRestTime = it },
                        label = { Text("Rest between exercises (seconds)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun TimerCircle(
    time: String,
    progress: Float,
    isRunning: Boolean,
    hasTimeSet: Boolean,
    remainingTime: Int,
    onStartPause: () -> Unit,
    onReset: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
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
                    RoundedCornerShape(24.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Canvas(
                modifier = Modifier.size(220.dp)
            ) {
                val strokeWidth = 8f
                val radius = (size.minDimension - strokeWidth) / 2
                val center = Offset(size.width / 2, size.height / 2)

                drawArc(
                    color = Color.White.copy(alpha = 0.2f),
                    startAngle = -90f,
                    sweepAngle = 360f,
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                    topLeft = Offset(center.x - radius, center.y - radius),
                    size = Size(radius * 2, radius * 2)
                )

                if (hasTimeSet && progress < 1f) {
                    drawArc(
                        color = Color.White,
                        startAngle = -90f,
                        sweepAngle = 360f * progress,
                        useCenter = false,
                        style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                        topLeft = Offset(center.x - radius, center.y - radius),
                        size = Size(radius * 2, radius * 2)
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = time,
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    letterSpacing = 4.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (!hasTimeSet) {
                    Text(
                        text = "Select a rest type below",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                } else if (isRunning) {
                    Text(
                        text = "Tap to pause",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                } else if (remainingTime > 0) {
                    Text(
                        text = "Tap to resume",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.5f)
                    )
                }
            }

            if (hasTimeSet) {
                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .clickable(onClick = onStartPause),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isRunning) "Pause" else "Start",
                        tint = Color.White.copy(alpha = 0.08f),
                        modifier = Modifier.size(80.dp)
                    )
                }

                if (!isRunning && remainingTime > 0) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(32.dp)
                            .clip(RoundedCornerShape(50))
                            .background(Color.White.copy(alpha = 0.2f))
                            .clickable(onClick = onReset)
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Reset",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TimerPresetButton(
    label: String,
    time: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(80.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceSoft),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = time,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = ElectricBlue
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatTime(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return String.format("%02d:%02d", mins, secs)
}
