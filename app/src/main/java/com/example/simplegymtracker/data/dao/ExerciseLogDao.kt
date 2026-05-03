package com.example.simplegymtracker.data.dao

import androidx.room.*
import com.example.simplegymtracker.data.entity.ExerciseLog
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseLogDao {
    @Query("SELECT * FROM ExerciseLog WHERE sessionId = :sessionId")
    fun getLogsForSession(sessionId: Int): Flow<List<ExerciseLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(exerciseLog: ExerciseLog): Long

    @Delete
    suspend fun delete(exerciseLog: ExerciseLog)
}
