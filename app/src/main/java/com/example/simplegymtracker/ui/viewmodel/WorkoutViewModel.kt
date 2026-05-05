package com.example.simplegymtracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.simplegymtracker.data.entity.ExerciseLog
import com.example.simplegymtracker.data.entity.Session
import com.example.simplegymtracker.data.entity.Set
import com.example.simplegymtracker.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WorkoutViewModel(private val repository: WorkoutRepository) : ViewModel() {

    // Expose all sessions to the UI
    val allSessions: StateFlow<List<Session>> = repository.allSessions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // --- Session Actions ---
    fun startNewSession(userId: Int, onSessionCreated: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val newSession = Session(
                userId = userId,
                date = System.currentTimeMillis()
            )
            val sessionId = repository.startSession(newSession)
            onSessionCreated(sessionId)
        }
    }

    /**
     * Starts a new session only if the most recent session is not empty
     * (has exercise logs). If the most recent session has no logs and was
     * created within the last 4 hours, resume it instead to avoid duplicates.
     */
    fun startNewSessionOrResume(userId: Int, onSessionReady: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val sessions = repository.allSessions.first()
            val mostRecent = sessions.maxByOrNull { it.date }

            val fourHoursInMs = 4 * 60 * 60 * 1000L
            val isRecent = mostRecent != null &&
                    (System.currentTimeMillis() - mostRecent.date) < fourHoursInMs

            if (mostRecent != null && isRecent) {
                val logs = repository.getLogsForSession(mostRecent.sessionId).first()
                if (logs.isEmpty()) {
                    // Resume the existing empty session
                    onSessionReady(mostRecent.sessionId.toLong())
                    return@launch
                }
            }

            // Create a brand new session
            val newSession = Session(
                userId = userId,
                date = System.currentTimeMillis()
            )
            val sessionId = repository.startSession(newSession)
            onSessionReady(sessionId)
        }
    }

    fun deleteSession(session: Session) {
        viewModelScope.launch {
            repository.deleteSession(session)
        }
    }

    // --- Exercise Log Actions ---
    fun getLogsForSession(sessionId: Int) = repository.getLogsForSession(sessionId)

    fun deleteLog(log: ExerciseLog) {
        viewModelScope.launch {
            repository.deleteExerciseLog(log)
        }
    }

    fun addExerciseToSession(sessionId: Int, exerciseId: Int) {
        viewModelScope.launch {
            val log = ExerciseLog(
                sessionId = sessionId,
                exerciseId = exerciseId,
                dateTime = System.currentTimeMillis()
            )
            repository.addExerciseLog(log)
        }
    }

    // --- Set Actions ---
    fun getSetsForLog(logId: Int) = repository.getSetsForLog(logId)

    fun addSetToLog(exerciseLogId: Int, weight: Float, reps: Int, order: Int, type: String) {
        viewModelScope.launch {
            val newSet = Set(
                exerciseLogId = exerciseLogId,
                weight = weight,
                repetitions = reps,
                setOrder = order,
                type = type
            )
            repository.addSet(newSet)
        }
    }

    fun updateSet(set: Set) {
        viewModelScope.launch {
            repository.updateSet(set)
        }
    }

    fun deleteSet(set: Set) {
        viewModelScope.launch {
            repository.deleteSet(set)
        }
    }
}

class WorkoutViewModelFactory(private val repository: WorkoutRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WorkoutViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WorkoutViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
