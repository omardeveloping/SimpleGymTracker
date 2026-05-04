package com.example.simplegymtracker.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object ExerciseSelector : Screen("exercise_selector")
    data object ActiveWorkout : Screen("active_workout/{sessionId}") {
        fun createRoute(sessionId: Long) = "active_workout/$sessionId"
    }
    data object SessionHistory : Screen("session_history")
    data object ProgressChart : Screen("progress_chart")
    data object TimerConfig : Screen("timer_config")
}
