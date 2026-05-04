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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.simplegymtracker.data.AppDatabase
import com.example.simplegymtracker.data.entity.Exercise
import com.example.simplegymtracker.data.entity.User
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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

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

        // Ensure a default user exists (required for foreign key constraint)
        lifecycleScope.launch {
            val users = userRepository.allUsers.first()
            // If no users exist, create a default user
            if (users.isEmpty()) {
                userRepository.insert(User(id = 0, name = "Default", lastName = "User"))
            }
        }

        // Seed predefined exercises into database
        lifecycleScope.launch {
            val existingExercises = exerciseRepository.allExercises.first()
            if (existingExercises.isEmpty()) {
                val predefinedExercises = listOf(
                    Exercise(name = "Bench Press", category = "Chest", createdByUser = false),
                    Exercise(name = "Squat", category = "Legs", createdByUser = false),
                    Exercise(name = "Deadlift", category = "Back", createdByUser = false),
                    Exercise(name = "Overhead Press", category = "Shoulders", createdByUser = false),
                    Exercise(name = "Barbell Row", category = "Back", createdByUser = false),
                    Exercise(name = "Dumbbell Curl", category = "Arms", createdByUser = false),
                    Exercise(name = "Tricep Extension", category = "Arms", createdByUser = false),
                    Exercise(name = "Leg Press", category = "Legs", createdByUser = false),
                    Exercise(name = "Lat Pulldown", category = "Back", createdByUser = false),
                    Exercise(name = "Chest Fly", category = "Chest", createdByUser = false)
                )
                predefinedExercises.forEach { exerciseRepository.insert(it) }
            }
        }

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
                    navController.previousBackStackEntry?.savedStateHandle?.remove<Int>("selected_exercise_id")
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.ActiveWorkout.route) { backStackEntry ->
            val sessionId = backStackEntry.arguments
                ?.getString("sessionId")
                ?.toLongOrNull() ?: 0L

            val selectedExerciseId = backStackEntry.savedStateHandle.get<Int>("selected_exercise_id") ?: -1
            backStackEntry.savedStateHandle["selected_exercise_id"] = -1

            ActiveWorkoutScreen(
                sessionId = sessionId,
                selectedExerciseId = selectedExerciseId,
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
