package com.example.simplegymtracker.data.repository

import com.example.simplegymtracker.data.dao.ExerciseLogDao
import com.example.simplegymtracker.data.dao.SessionDao
import com.example.simplegymtracker.data.dao.SetDao
import com.example.simplegymtracker.data.entity.ExerciseLog
import com.example.simplegymtracker.data.entity.Session
import com.example.simplegymtracker.data.entity.Set
import kotlinx.coroutines.flow.Flow

/**
 * Combined repository to handle the logic of a workout session,
 * including the logs and the sets performed.
 */
class WorkoutRepository(
    private val sessionDao: SessionDao,
    private val logDao: ExerciseLogDao,
    private val setDao: SetDao
) {

    // --- Session Operations ---
    val allSessions: Flow<List<Session>> = sessionDao.getAll()

    suspend fun startSession(session: Session): Long {
        return sessionDao.insert(session)
    }

    suspend fun deleteSession(session: Session) {
        sessionDao.delete(session)
    }

    // --- Log Operations ---
    fun getLogsForSession(sessionId: Int): Flow<List<ExerciseLog>> {
        return logDao.getLogsForSession(sessionId)
    }

    suspend fun addExerciseLog(log: ExerciseLog): Long {
        return logDao.insert(log)
    }

    // --- Set Operations ---
    fun getSetsForLog(logId: Int): Flow<List<Set>> {
        return setDao.getSetsForLog(logId)
    }

    suspend fun addSet(set: Set) {
        setDao.insert(set)
    }

    suspend fun updateSet(set: Set) {
        setDao.update(set)
    }

    suspend fun deleteSet(set: Set) {
        setDao.delete(set)
    }
}
