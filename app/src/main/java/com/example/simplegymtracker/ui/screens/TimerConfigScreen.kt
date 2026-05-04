package com.example.simplegymtracker.ui.screens

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.simplegymtracker.ui.theme.ElectricBlue
import com.example.simplegymtracker.ui.theme.RestTimerAccent
import com.example.simplegymtracker.ui.theme.RestTimerBg
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
    var timerLabel by remember { mutableStateOf("Rest between sets") }

    // Countdown logic
    LaunchedEffect(isTimerRunning) {
        if (isTimerRunning && remainingTime > 0) {
            while (remainingTime > 0 && isTimerRunning) {
                delay(1000L)
                if (isTimerRunning) {
                    remainingTime -= 1
                }
            }
            if (remainingTime <= 0) {
                isTimerRunning = false
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rest Timer", fontSize = 20.sp, fontWeight = FontWeight.SemiBold) },
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
            Spacer(modifier = Modifier.height(16.dp))

            // Timer Display
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = RestTimerBg
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = RestTimerAccent,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isTimerRunning || remainingTime > 0) formatTime(remainingTime) else "00:00",
                            fontSize = 56.sp,
                            fontWeight = FontWeight.Bold,
                            color = RestTimerAccent
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = timerLabel,
                            fontSize = 14.sp,
                            color = androidx.compose.ui.graphics.Color(0xFF5D5B54)
                        )
                        if (!isTimerRunning && remainingTime == 0) {
                            Text(
                                text = "Tap Start to begin",
                                fontSize = 13.sp,
                                color = androidx.compose.ui.graphics.Color(0xFF787671)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Timer Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TimerButton(
                    label = "Start Set Rest",
                    onClick = {
                        timerLabel = "Rest between sets"
                        remainingTime = setRestTime.toIntOrNull() ?: 60
                        isTimerRunning = true
                    },
                    modifier = Modifier.weight(1f)
                )
                TimerButton(
                    label = "Start Exercise Rest",
                    onClick = {
                        timerLabel = "Rest between exercises"
                        remainingTime = exerciseRestTime.toIntOrNull() ?: 120
                        isTimerRunning = true
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            TimerButton(
                label = if (isTimerRunning) "Pause" else "Resume",
                onClick = { isTimerRunning = !isTimerRunning },
                modifier = Modifier.fillMaxWidth(),
                isSecondary = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            TimerButton(
                label = "Reset",
                onClick = {
                    isTimerRunning = false
                    remainingTime = 0
                },
                modifier = Modifier.fillMaxWidth(),
                isSecondary = true
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Configuration
            Text(
                text = "Timer Configuration",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = setRestTime,
                onValueChange = { setRestTime = it },
                label = { Text("Rest between sets (seconds)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = exerciseRestTime,
                onValueChange = { exerciseRestTime = it },
                label = { Text("Rest between exercises (seconds)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }
    }
}

@Composable
fun TimerButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isSecondary: Boolean = false
) {
    val backgroundColor = if (isSecondary) androidx.compose.ui.graphics.Color.White else ElectricBlue
    val textColor = if (isSecondary) ElectricBlue else androidx.compose.ui.graphics.Color.White
    val border = if (isSecondary) BorderStroke(1.dp, ElectricBlue) else null

    Card(
        modifier = modifier
            .height(48.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = border
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = label,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = textColor
            )
        }
    }
}

private fun formatTime(seconds: Int): String {
    val mins = seconds / 60
    val secs = seconds % 60
    return String.format("%02d:%02d", mins, secs)
}
