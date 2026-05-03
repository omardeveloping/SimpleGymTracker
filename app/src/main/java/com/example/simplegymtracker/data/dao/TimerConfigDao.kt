package com.example.simplegymtracker.data.dao

import androidx.room.*
import com.example.simplegymtracker.data.entity.TimerConfig
import kotlinx.coroutines.flow.Flow

@Dao
interface TimerConfigDao {
    @Query("SELECT * FROM TimerConfig LIMIT 1")
    fun getConfig(): Flow<TimerConfig?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(config: TimerConfig)
}
