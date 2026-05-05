package com.example.simplegymtracker.data.repository

import com.example.simplegymtracker.data.dao.ExerciseLogDao
import com.example.simplegymtracker.data.dao.SessionDao
import com.example.simplegymtracker.data.dao.SetDao
import com.example.simplegymtracker.data.entity.ExerciseLog
import com.example.simplegymtracker.data.entity.Session
import com.example.simplegymtracker.data.entity.Set
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class WorkoutRepository(
    private val sessionDao: SessionDao,
    private val logDao: ExerciseLogDao,
    private val setDao: SetDao
) {

    val allSessions: Flow<List<Session>> = sessionDao.getAll()

    suspend fun startSession(session: Session): Long {
        return sessionDao.insert(session)
    }

    suspend fun deleteSession(session: Session) {
        sessionDao.delete(session)
    }

    fun getSessionsBetweenDates(startDate: Long, endDate: Long): Flow<List<Session>> {
        return sessionDao.getSessionsBetweenDates(startDate, endDate)
    }

    fun getLogsForSession(sessionId: Int): Flow<List<ExerciseLog>> {
        return logDao.getLogsForSession(sessionId)
    }

    suspend fun addExerciseLog(log: ExerciseLog): Long {
        return logDao.insert(log)
    }

    suspend fun deleteExerciseLog(log: ExerciseLog) {
        logDao.delete(log)
    }

    fun getSetsForLog(logId: Int): Flow<List<Set>> {
        return setDao.getSetsForLog(logId)
    }

    suspend fun getLastSetForExercise(exerciseId: Int): Set? {
        return setDao.getLastSetForExercise(exerciseId)
    }

    suspend fun addSet(set: Set) {
        setDao.insert(set)
    }

    suspend fun updateSet(set: Set) {
        setDao.update(set)
    }

    suspend fun updateSetCompletion(setId: Int, isCompleted: Boolean) {
        setDao.updateCompletion(setId, isCompleted)
    }

    suspend fun deleteSet(set: Set) {
        setDao.delete(set)
    }

    suspend fun copySession(sessionId: Int, newUserId: Int): Long {
        val sourceLogs = logDao.getLogsForSession(sessionId).first()
        val newSession = Session(userId = newUserId, date = System.currentTimeMillis())
        val newSessionId = sessionDao.insert(newSession)

        for (sourceLog in sourceLogs) {
            val newLog = ExerciseLog(
                sessionId = newSessionId.toInt(),
                exerciseId = sourceLog.exerciseId,
                dateTime = System.currentTimeMillis()
            )
            val newLogId = logDao.insert(newLog).toInt()

            val sourceSets = setDao.getSetsForLog(sourceLog.exerciseLogId).first()
            for (sourceSet in sourceSets) {
                val newSet = Set(
                    exerciseLogId = newLogId,
                    weight = sourceSet.weight,
                    repetitions = sourceSet.repetitions,
                    setOrder = sourceSet.setOrder,
                    type = sourceSet.type,
                    isCompleted = false
                )
                setDao.insert(newSet)
            }
        }

        return newSessionId
    }
}
