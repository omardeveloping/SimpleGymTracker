package com.example.simplegymtracker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.simplegymtracker.data.dao.*
import com.example.simplegymtracker.data.entity.*
import com.example.simplegymtracker.data.entity.Set

@Database(
    entities = [
        User::class,
        Session::class,
        Exercise::class,
        ExerciseLog::class,
        Set::class,
        TimerConfig::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun sessionDao(): SessionDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun exerciseLogDao(): ExerciseLogDao
    abstract fun setDao(): SetDao
    abstract fun timerConfigDao(): TimerConfigDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "gym_tracker_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
