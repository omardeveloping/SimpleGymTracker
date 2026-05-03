package com.example.simplegymtracker.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    foreignKeys = [
        ForeignKey(
            entity = Session::class,
            //Column where I get the FK
            parentColumns = ["sessionId"],
            //Where I want to save it
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Exercise::class,
            //Column where I get the FK
            parentColumns = ["exerciseId"],
            //Where I want to save it
            childColumns = ["exerciseId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ExerciseLog(
    @PrimaryKey(autoGenerate = true) val exerciseLogId: Int = 0,
    val sessionId: Int,
    val exerciseId: Int,
    val dateTime: Long // Represented as a timestamp
)
