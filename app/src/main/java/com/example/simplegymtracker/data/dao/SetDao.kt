package com.example.simplegymtracker.data.dao

import androidx.room.*
import com.example.simplegymtracker.data.entity.Set
import kotlinx.coroutines.flow.Flow

@Dao
interface SetDao {
    @Query("SELECT * FROM sets WHERE exerciseLogId = :exerciseLogId")
    fun getSetsForLog(exerciseLogId: Int): Flow<List<Set>>

    @Query("SELECT * FROM sets WHERE exerciseLogId IN (:logIds)")
    suspend fun getSetsForLogs(logIds: List<Int>): List<Set>

    @Query("""
        SELECT sets.* FROM sets
        INNER JOIN ExerciseLog ON sets.exerciseLogId = ExerciseLog.exerciseLogId
        WHERE ExerciseLog.exerciseId = :exerciseId
        ORDER BY sets.setId DESC
        LIMIT 1
    """)
    suspend fun getLastSetForExercise(exerciseId: Int): Set?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(set: Set)

    @Update
    suspend fun update(set: Set)

    @Query("UPDATE sets SET isCompleted = :isCompleted WHERE setId = :setId")
    suspend fun updateCompletion(setId: Int, isCompleted: Boolean)

    @Delete
    suspend fun delete(set: Set)
}
