package com.example.simplegymtracker.data.dao

import androidx.room.*
import com.example.simplegymtracker.data.entity.Session
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Query("SELECT * FROM sessions ORDER BY date DESC")
    fun getAll(): Flow<List<Session>>

    @Query("SELECT * FROM sessions WHERE userId = :userId")
    fun getByUserId(userId: Int): Flow<List<Session>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(session: Session): Long

    @Delete
    suspend fun delete(session: Session)
}
