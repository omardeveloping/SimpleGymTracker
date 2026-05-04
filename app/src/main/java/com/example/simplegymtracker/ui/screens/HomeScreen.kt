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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.simplegymtracker.data.entity.Session
import com.example.simplegymtracker.ui.theme.CardTintSky
import com.example.simplegymtracker.ui.theme.ElectricBlue
import com.example.simplegymtracker.ui.theme.SurfaceSoft
import com.example.simplegymtracker.ui.viewmodel.UserViewModel
import com.example.simplegymtracker.ui.viewmodel.UserViewModelFactory
import com.example.simplegymtracker.ui.viewmodel.WorkoutViewModel
import com.example.simplegymtracker.ui.viewmodel.WorkoutViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    userViewModelFactory: UserViewModelFactory,
    workoutViewModelFactory: WorkoutViewModelFactory,
    onStartWorkout: (Long) -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToProgress: () -> Unit,
    onNavigateToTimer: () -> Unit
) {
    val userViewModel: UserViewModel = viewModel(factory = userViewModelFactory)
    val workoutViewModel: WorkoutViewModel = viewModel(factory = workoutViewModelFactory)

    val sessions by workoutViewModel.allSessions.collectAsState()
    val recentSessions = sessions.take(3)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Simple Gym Tracker",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // Start a new session with userId = 1 (default user)
                    workoutViewModel.startNewSession(userId = 1)
                    // Get the latest session and navigate
                    val latestSession = sessions.firstOrNull()
                    if (latestSession != null) {
                        onStartWorkout(latestSession.sessionId.toLong())
                    }
                },
                containerColor = ElectricBlue,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Start Workout",
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
                // Welcome Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = CardTintSky
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Text(
                            text = "Ready to crush it?",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap the + button to start a new workout",
                            fontSize = 14.sp,
                            color = Slate
                        )
                    }
                }
            }

            item {
                // Quick Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    QuickStatCard(
                        icon = Icons.Default.History,
                        label = "Workouts",
                        value = sessions.size.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    QuickStatCard(
                        icon = Icons.Default.ShowChart,
                        label = "Progress",
                        value = "View",
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToProgress
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Recent Sessions",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if (recentSessions.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No workouts yet. Start your first!",
                            color = Slate,
                            fontSize = 14.sp
                        )
                    }
                }
            } else {
                items(recentSessions) { session ->
                    SessionCard(
                        session = session,
                        onClick = { onStartWorkout(session.sessionId.toLong()) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                // Timer Shortcut
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToTimer() },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = SurfaceSoft
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Timer",
                            tint = ElectricBlue,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Rest Timer",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Text(
                                text = "Configure your rest periods",
                                fontSize = 12.sp,
                                color = Slate
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = Slate
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun QuickStatCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = androidx.compose.ui.graphics.Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = ElectricBlue,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = label,
                fontSize = 12.sp,
                color = Slate
            )
        }
    }
}

@Composable
fun SessionCard(
    session: Session,
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault())
    val dateString = dateFormat.format(Date(session.date))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = androidx.compose.ui.graphics.Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(CardTintSky),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "Session",
                    tint = ElectricBlue,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Workout Session",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = dateString,
                    fontSize = 13.sp,
                    color = Slate
                )
            }
        }
    }
}

@Composable
private fun Spacer(modifier: Modifier) = androidx.compose.foundation.layout.Spacer(modifier)

private val Slate = androidx.compose.ui.graphics.Color(0xFF5D5B54)

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    com.example.simplegymtracker.ui.theme.SimpleGymTrackerTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Text("Home Screen Preview - Requires ViewModels")
        }
    }
}
