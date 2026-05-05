package com.example.simplegymtracker.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity
data class Exercise(
    @PrimaryKey(autoGenerate = true) val exerciseId: Int = 0,
    val name: String?,
    val category: String?, // "Strength", "Cardio", "Flexibility", "Other"
    val equipment: String?, // "Barbell", "Dumbbell", "Machine", "Cable", "Bodyweight", "Treadmill", "Bike"
    val muscleGroup: String?, // "Chest", "Back", "Legs", "Shoulders", "Arms", "Core"
    val createdByUser: Boolean
)