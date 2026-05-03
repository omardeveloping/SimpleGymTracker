package com.example.simplegymtracker.data.dao

import androidx.room.*
import com.example.simplegymtracker.data.entity.Set
import kotlinx.coroutines.flow.Flow

@Dao
interface SetDao {
    @Query("SELECT * FROM sets WHERE exerciseLogId = :exerciseLogId")
    fun getSetsForLog(exerciseLogId: Int): Flow<List<Set>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(set: Set)

    @Update
    suspend fun update(set: Set)

    @Delete
    suspend fun delete(set: Set)
}
