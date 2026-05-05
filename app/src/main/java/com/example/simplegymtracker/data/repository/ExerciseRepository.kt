package com.example.simplegymtracker.data.repository

import com.example.simplegymtracker.data.dao.ExerciseDao
import com.example.simplegymtracker.data.entity.Exercise
import kotlinx.coroutines.flow.Flow

class ExerciseRepository(private val exerciseDao: ExerciseDao) {

    val allExercises: Flow<List<Exercise>> = exerciseDao.getAll()

    fun getExercisesByCategory(category: String): Flow<List<Exercise>> {
        return exerciseDao.getExercisesByCategory(category)
    }

    fun getExercisesByEquipment(equipment: String): Flow<List<Exercise>> {
        return exerciseDao.getExercisesByEquipment(equipment)
    }

    suspend fun insert(exercise: Exercise) {
        exerciseDao.insert(exercise)
    }

    suspend fun delete(exercise: Exercise) {
        exerciseDao.delete(exercise)
    }
}
