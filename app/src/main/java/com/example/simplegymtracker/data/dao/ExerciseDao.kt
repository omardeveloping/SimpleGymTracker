package com.example.simplegymtracker.data.dao

import androidx.room.*
import com.example.simplegymtracker.data.entity.Exercise
import kotlinx.coroutines.flow.Flow

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM Exercise")
    fun getAll(): Flow<List<Exercise>>

    @Query("SELECT * FROM Exercise WHERE category = :category")
    fun getExercisesByCategory(category: String): Flow<List<Exercise>>

    @Query("SELECT * FROM Exercise WHERE equipment = :equipment")
    fun getExercisesByEquipment(equipment: String): Flow<List<Exercise>>

    @Query("SELECT * FROM Exercise WHERE exerciseId = :exerciseId")
    suspend fun getExerciseById(exerciseId: Int): Exercise?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(exercise: Exercise)

    @Delete
    suspend fun delete(exercise: Exercise)
}
