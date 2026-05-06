package com.example.simplegymtracker.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.simplegymtracker.data.entity.ExerciseLog
import com.example.simplegymtracker.data.entity.Session
import com.example.simplegymtracker.data.entity.Set
import com.example.simplegymtracker.data.repository.WorkoutRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

data class SessionStats(
    val totalExercises: Int = 0,
    val totalSets: Int = 0,
    val totalVolume: Float = 0f,
    val totalReps: Int = 0,
    val completedSets: Int = 0
)

data class WeeklyStats(
    val totalSessions: Int = 0,
    val totalSets: Int = 0,
    val totalVolume: Float = 0f
)

data class MonthlyStats(
    val totalSessions: Int = 0,
    val totalSets: Int = 0,
    val totalVolume: Float = 0f,
    val topExercise: String? = null,
    val topExerciseCount: Int = 0
)

class WorkoutViewModel(private val repository: WorkoutRepository) : ViewModel() {

    val allSessions: StateFlow<List<Session>> = repository.allSessions
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _weekSessions = MutableStateFlow<List<Session>>(emptyList())
    val weekSessions: StateFlow<List<Session>> = _weekSessions.asStateFlow()

    private val _monthSessions = MutableStateFlow<List<Session>>(emptyList())
    val monthSessions: StateFlow<List<Session>> = _monthSessions.asStateFlow()

    private var weekCollectionJob: kotlinx.coroutines.Job? = null
    private var monthCollectionJob: kotlinx.coroutines.Job? = null

    fun loadSessionsForWeek(startOfWeek: Long) {
        weekCollectionJob?.cancel()
        weekCollectionJob = viewModelScope.launch {
            val endOfWeek = startOfWeek + (7 * 24 * 60 * 60 * 1000L) - 1
            repository.getSessionsBetweenDates(startOfWeek, endOfWeek)
                .collect { sessions ->
                    _weekSessions.value = sessions
                }
        }
    }

    fun loadSessionsForMonth(year: Int, month: Int) {
        monthCollectionJob?.cancel()
        monthCollectionJob = viewModelScope.launch {
            val calendar = Calendar.getInstance().apply {
                set(year, month - 1, 1, 0, 0, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val startOfMonth = calendar.timeInMillis
            calendar.add(Calendar.MONTH, 1)
            calendar.add(Calendar.MILLISECOND, -1)
            val endOfMonth = calendar.timeInMillis

            repository.getSessionsBetweenDates(startOfMonth, endOfMonth)
                .collect { sessions ->
                    _monthSessions.value = sessions
                }
        }
    }

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

    fun getSessionStats(sessionId: Int, onResult: (SessionStats) -> Unit) {
        viewModelScope.launch {
            val logs = repository.getLogsForSession(sessionId).first()
            val totalExercises = logs.size
            var totalSets = 0
            var totalVolume = 0f
            var totalReps = 0
            var completedSets = 0

            for (log in logs) {
                val sets = repository.getSetsForLog(log.exerciseLogId).first()
                totalSets += sets.size
                for (set in sets) {
                    totalVolume += set.weight * set.repetitions
                    totalReps += set.repetitions
                    if (set.isCompleted) completedSets++
                }
            }

            onResult(
                SessionStats(
                    totalExercises = totalExercises,
                    totalSets = totalSets,
                    totalVolume = totalVolume,
                    totalReps = totalReps,
                    completedSets = completedSets
                )
            )
        }
    }

    private val _weeklyStats = MutableStateFlow(WeeklyStats())
    val weeklyStats: StateFlow<WeeklyStats> = _weeklyStats.asStateFlow()

    private val _monthlyStats = MutableStateFlow(MonthlyStats())
    val monthlyStats: StateFlow<MonthlyStats> = _monthlyStats.asStateFlow()

    private var weeklyStatsJob: kotlinx.coroutines.Job? = null
    private var monthlyStatsJob: kotlinx.coroutines.Job? = null

    fun loadWeeklyStats() {
        weeklyStatsJob?.cancel()
        weeklyStatsJob = viewModelScope.launch {
            val calendar = Calendar.getInstance()
            val endOfWeek = calendar.timeInMillis
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startOfWeek = calendar.timeInMillis

            repository.getSessionsBetweenDates(startOfWeek, endOfWeek).collect { sessions ->
                var totalSessions = sessions.size
                var totalSets = 0
                var totalVolume = 0f

                for (session in sessions) {
                    val logs = repository.getLogsForSession(session.sessionId).first()
                    for (log in logs) {
                        val sets = repository.getSetsForLog(log.exerciseLogId).first()
                        totalSets += sets.size
                        for (set in sets) {
                            totalVolume += set.weight * set.repetitions
                        }
                    }
                }

                _weeklyStats.value = WeeklyStats(
                    totalSessions = totalSessions,
                    totalSets = totalSets,
                    totalVolume = totalVolume
                )
            }
        }
    }

    fun loadMonthlyStats() {
        monthlyStatsJob?.cancel()
        monthlyStatsJob = viewModelScope.launch {
            val calendar = Calendar.getInstance()
            val endOfMonth = calendar.timeInMillis
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            val startOfMonth = calendar.timeInMillis

            repository.getSessionsBetweenDates(startOfMonth, endOfMonth).collect { sessions ->
                var totalSessions = sessions.size
                var totalSets = 0
                var totalVolume = 0f
                val exerciseCounts = mutableMapOf<String, Int>()

                for (session in sessions) {
                    val logs = repository.getLogsForSession(session.sessionId).first()
                    for (log in logs) {
                        val sets = repository.getSetsForLog(log.exerciseLogId).first()
                        totalSets += sets.size
                        for (set in sets) {
                            totalVolume += set.weight * set.repetitions
                        }
                        val exercise = repository.getExerciseById(log.exerciseId)
                        exercise?.let {
                            exerciseCounts[it.name ?: "Unknown"] = exerciseCounts.getOrDefault(it.name ?: "Unknown", 0) + 1
                        }
                    }
                }

                val topExercise = exerciseCounts.maxByOrNull { it.value }

                _monthlyStats.value = MonthlyStats(
                    totalSessions = totalSessions,
                    totalSets = totalSets,
                    totalVolume = totalVolume,
                    topExercise = topExercise?.key,
                    topExerciseCount = topExercise?.value ?: 0
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        weekCollectionJob?.cancel()
        monthCollectionJob?.cancel()
        weeklyStatsJob?.cancel()
        monthlyStatsJob?.cancel()
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
