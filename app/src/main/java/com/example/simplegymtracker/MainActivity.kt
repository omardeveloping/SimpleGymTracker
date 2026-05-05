package com.example.simplegymtracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
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
import com.example.simplegymtracker.ui.screens.CalendarScreen
import com.example.simplegymtracker.ui.screens.ExerciseSelectorScreen
import com.example.simplegymtracker.ui.screens.HomeScreen
import com.example.simplegymtracker.ui.screens.ProgressChartScreen
import com.example.simplegymtracker.ui.screens.SessionHistoryScreen
import com.example.simplegymtracker.ui.screens.TimerConfigScreen
import com.example.simplegymtracker.ui.theme.SimpleGymTrackerTheme
import com.example.simplegymtracker.ui.viewmodel.ExerciseViewModelFactory
import com.example.simplegymtracker.ui.viewmodel.UserViewModelFactory
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

        lifecycleScope.launch {
            val users = userRepository.allUsers.first()
            if (users.isEmpty()) {
                userRepository.insert(User(id = 0, name = "Default", lastName = "User", preferredUnit = "kg"))
            }
        }

        lifecycleScope.launch {
            val existingExercises = exerciseRepository.allExercises.first()
            if (existingExercises.isEmpty()) {
                val predefinedExercises = listOf(
                    Exercise(name = "Bench Press", category = "Strength", equipment = "Barbell", muscleGroup = "Chest", createdByUser = false),
                    Exercise(name = "Squat", category = "Strength", equipment = "Barbell", muscleGroup = "Legs", createdByUser = false),
                    Exercise(name = "Deadlift", category = "Strength", equipment = "Barbell", muscleGroup = "Back", createdByUser = false),
                    Exercise(name = "Overhead Press", category = "Strength", equipment = "Barbell", muscleGroup = "Shoulders", createdByUser = false),
                    Exercise(name = "Barbell Row", category = "Strength", equipment = "Barbell", muscleGroup = "Back", createdByUser = false),
                    Exercise(name = "Dumbbell Curl", category = "Strength", equipment = "Dumbbell", muscleGroup = "Arms", createdByUser = false),
                    Exercise(name = "Lateral Raise", category = "Strength", equipment = "Dumbbell", muscleGroup = "Shoulders", createdByUser = false),
                    Exercise(name = "Dumbbell Fly", category = "Strength", equipment = "Dumbbell", muscleGroup = "Chest", createdByUser = false),
                    Exercise(name = "Leg Press", category = "Strength", equipment = "Machine", muscleGroup = "Legs", createdByUser = false),
                    Exercise(name = "Lat Pulldown", category = "Strength", equipment = "Machine", muscleGroup = "Back", createdByUser = false),
                    Exercise(name = "Chest Press Machine", category = "Strength", equipment = "Machine", muscleGroup = "Chest", createdByUser = false),
                    Exercise(name = "Treadmill Running", category = "Cardio", equipment = "Treadmill", muscleGroup = "Legs", createdByUser = false),
                    Exercise(name = "Stationary Bike", category = "Cardio", equipment = "Bike", muscleGroup = "Legs", createdByUser = false),
                    Exercise(name = "Rowing Machine", category = "Cardio", equipment = "Rowing", muscleGroup = "Back", createdByUser = false),
                    Exercise(name = "Stairmaster", category = "Cardio", equipment = "Stairmaster", muscleGroup = "Legs", createdByUser = false),
                    Exercise(name = "Push-up", category = "Strength", equipment = "Bodyweight", muscleGroup = "Chest", createdByUser = false),
                    Exercise(name = "Pull-up", category = "Strength", equipment = "Bodyweight", muscleGroup = "Back", createdByUser = false),
                    Exercise(name = "Plank", category = "Strength", equipment = "Bodyweight", muscleGroup = "Core", createdByUser = false)
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
    val enterTransition = slideInHorizontally(
        initialOffsetX = { it },
        animationSpec = tween(300)
    ) + fadeIn(animationSpec = tween(300))

    val exitTransition = slideOutHorizontally(
        targetOffsetX = { -it / 3 },
        animationSpec = tween(300)
    ) + fadeOut(animationSpec = tween(300))

    val popEnterTransition = slideInHorizontally(
        initialOffsetX = { -it / 3 },
        animationSpec = tween(300)
    ) + fadeIn(animationSpec = tween(300))

    val popExitTransition = slideOutHorizontally(
        targetOffsetX = { it },
        animationSpec = tween(300)
    ) + fadeOut(animationSpec = tween(300))

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
                },
                onNavigateToCalendar = {
                    navController.navigate(Screen.Calendar.route)
                }
            )
        }

        composable(
            route = Screen.ExerciseSelector.route,
            enterTransition = { enterTransition },
            exitTransition = { exitTransition },
            popEnterTransition = { popEnterTransition },
            popExitTransition = { popExitTransition }
        ) {
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

        composable(
            route = Screen.ActiveWorkout.route,
            enterTransition = { enterTransition },
            exitTransition = { exitTransition },
            popEnterTransition = { popEnterTransition },
            popExitTransition = { popExitTransition }
        ) { backStackEntry ->
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

        composable(
            route = Screen.SessionHistory.route,
            enterTransition = { enterTransition },
            exitTransition = { exitTransition },
            popEnterTransition = { popEnterTransition },
            popExitTransition = { popExitTransition }
        ) {
            SessionHistoryScreen(
                workoutViewModelFactory = workoutViewModelFactory,
                exerciseViewModelFactory = exerciseViewModelFactory,
                onCopySession = { sessionId ->
                    workoutViewModelFactory.let { factory ->
                        // Copy session logic is handled in the ViewModel
                    }
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.ProgressChart.route,
            enterTransition = { enterTransition },
            exitTransition = { exitTransition },
            popEnterTransition = { popEnterTransition },
            popExitTransition = { popExitTransition }
        ) {
            ProgressChartScreen(
                workoutViewModelFactory = workoutViewModelFactory,
                exerciseViewModelFactory = exerciseViewModelFactory,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.TimerConfig.route,
            enterTransition = { enterTransition },
            exitTransition = { exitTransition },
            popEnterTransition = { popEnterTransition },
            popExitTransition = { popExitTransition }
        ) {
            TimerConfigScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.Calendar.route,
            enterTransition = { enterTransition },
            exitTransition = { exitTransition },
            popEnterTransition = { popEnterTransition },
            popExitTransition = { popExitTransition }
        ) {
            CalendarScreen(
                workoutViewModelFactory = workoutViewModelFactory,
                exerciseViewModelFactory = exerciseViewModelFactory,
                onSessionClick = { sessionId ->
                    navController.navigate(Screen.ActiveWorkout.createRoute(sessionId))
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}