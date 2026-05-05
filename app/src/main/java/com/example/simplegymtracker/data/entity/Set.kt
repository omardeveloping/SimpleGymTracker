package com.example.simplegymtracker.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "sets",
    foreignKeys = [
        ForeignKey(
            entity = ExerciseLog::class,
            parentColumns = ["exerciseLogId"],
            childColumns = ["exerciseLogId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Set(
    @PrimaryKey(autoGenerate = true) val setId: Int = 0,
    val exerciseLogId: Int,
    val weight: Float,
    val repetitions: Int,
    val setOrder: Int, // To keep track of the sequence (Set 1, Set 2, etc.)
    val type: String, // e.g., "Warm-up", "Working Set", "Drop Set"
    val isCompleted: Boolean = false
)
