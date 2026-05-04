package com.example.simplegymtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.simplegymtracker.data.AppDatabase
import com.example.simplegymtracker.data.repository.ExerciseRepository
import com.example.simplegymtracker.data.repository.UserRepository
import com.example.simplegymtracker.data.repository.WorkoutRepository
import com.example.simplegymtracker.ui.navigation.Screen
import com.example.simplegymtracker.ui.screens.ActiveWorkoutScreen
import com.example.simplegymtracker.ui.screens.ExerciseSelectorScreen
import com.example.simplegymtracker.ui.screens.HomeScreen
import com.example.simplegymtracker.ui.screens.ProgressChartScreen
import com.example.simplegymtracker.ui.screens.SessionHistoryScreen
import com.example.simplegymtracker.ui.screens.TimerConfigScreen
import com.example.simplegymtracker.ui.theme.SimpleGymTrackerTheme
import com.example.simplegymtracker.ui.viewmodel.ExerciseViewModel
import com.example.simplegymtracker.ui.viewmodel.ExerciseViewModelFactory
import com.example.simplegymtracker.ui.viewmodel.UserViewModel
import com.example.simplegymtracker.ui.viewmodel.UserViewModelFactory
import com.example.simplegymtracker.ui.viewmodel.WorkoutViewModel
import com.example.simplegymtracker.ui.viewmodel.WorkoutViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppDatabase.getDatabase(applicationContext)
        val userRepository = UserRepository(db.userDao())
        val exerciseRepository = ExerciseRepository(db.exerciseDao())
        val workoutRepository = WorkoutRepository(
            db.sessionDao(),
            db.exerciseLogDao(),
            db.setDao()
        )

        val userViewModelFactory = UserViewModelFactory(userRepository)
        val exerciseViewModelFactory = ExerciseViewModelFactory(exerciseRepository)
        val workoutViewModelFactory = WorkoutViewModelFactory(workoutRepository)

        setContent {
            SimpleGymTrackerTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AppNavHost(
                        navController = navController,
                        userViewModelFactory = userViewModelFactory,
                        exerciseViewModelFactory = exerciseViewModelFactory,
                        workoutViewModelFactory = workoutViewModelFactory,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    userViewModelFactory: UserViewModelFactory,
    exerciseViewModelFactory: ExerciseViewModelFactory,
    workoutViewModelFactory: WorkoutViewModelFactory,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        modifier = modifier
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                userViewModelFactory = userViewModelFactory,
                workoutViewModelFactory = workoutViewModelFactory,
                onStartWorkout = { sessionId ->
                    navController.navigate(Screen.ActiveWorkout.createRoute(sessionId))
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.SessionHistory.route)
                },
                onNavigateToProgress = {
                    navController.navigate(Screen.ProgressChart.route)
                },
                onNavigateToTimer = {
                    navController.navigate(Screen.TimerConfig.route)
                }
            )
        }

        composable(Screen.ExerciseSelector.route) {
            ExerciseSelectorScreen(
                exerciseViewModelFactory = exerciseViewModelFactory,
                onExerciseSelected = { exerciseId ->
                    navController.previousBackStackEntry
                        ?.savedStateHandle
                        ?.set("selected_exercise_id", exerciseId)
                    navController.popBackStack()
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.ActiveWorkout.route) { backStackEntry ->
            val sessionId = backStackEntry.arguments
                ?.getString("sessionId")
                ?.toLongOrNull() ?: 0L

            ActiveWorkoutScreen(
                sessionId = sessionId,
                workoutViewModelFactory = workoutViewModelFactory,
                exerciseViewModelFactory = exerciseViewModelFactory,
                onAddExercise = {
                    navController.navigate(Screen.ExerciseSelector.route)
                },
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToTimer = {
                    navController.navigate(Screen.TimerConfig.route)
                }
            )
        }

        composable(Screen.SessionHistory.route) {
            SessionHistoryScreen(
                workoutViewModelFactory = workoutViewModelFactory,
                exerciseViewModelFactory = exerciseViewModelFactory,
                onCopySession = { sessionId ->
                    // TODO: Implement copy session logic
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.ProgressChart.route) {
            ProgressChartScreen(
                workoutViewModelFactory = workoutViewModelFactory,
                exerciseViewModelFactory = exerciseViewModelFactory,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.TimerConfig.route) {
            TimerConfigScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
