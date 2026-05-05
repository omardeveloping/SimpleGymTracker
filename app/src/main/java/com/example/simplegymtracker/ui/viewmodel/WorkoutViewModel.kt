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

    val allSessions: StateFlow<List<Session>> = repository.allSessions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

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
                    onSessionReady(mostRecent.sessionId.toLong())
                    return@launch
                }
            }

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

    fun completeSet(setId: Int, isCompleted: Boolean) {
        viewModelScope.launch {
            repository.updateSetCompletion(setId, isCompleted)
        }
    }

    fun getLastSetForExercise(exerciseId: Int, onResult: (Set?) -> Unit) {
        viewModelScope.launch {
            val lastSet = repository.getLastSetForExercise(exerciseId)
            onResult(lastSet)
        }
    }

    fun deleteSet(set: Set) {
        viewModelScope.launch {
            repository.deleteSet(set)
        }
    }

    fun copySession(sessionId: Int, userId: Int = 1, onCopied: (Long) -> Unit) {
        viewModelScope.launch {
            val newSessionId = repository.copySession(sessionId, userId)
            onCopied(newSessionId)
        }
    }

    fun getSessionsForWeek(startOfWeek: Long): StateFlow<List<Session>> {
        val endOfWeek = startOfWeek + (7 * 24 * 60 * 60 * 1000L) - 1
        return repository.getSessionsBetweenDates(startOfWeek, endOfWeek)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
    }

    fun getSessionsForMonth(year: Int, month: Int): StateFlow<List<Session>> {
        val calendar = java.util.Calendar.getInstance().apply {
            set(year, month - 1, 1, 0, 0, 0)
            set(java.util.Calendar.MILLISECOND, 0)
        }
        val startOfMonth = calendar.timeInMillis
        calendar.add(java.util.Calendar.MONTH, 1)
        calendar.add(java.util.Calendar.MILLISECOND, -1)
        val endOfMonth = calendar.timeInMillis

        return repository.getSessionsBetweenDates(startOfMonth, endOfMonth)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5000),
                initialValue = emptyList()
            )
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
