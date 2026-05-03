package com.example.simplegymtracker.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity
data class Exercise(
    @PrimaryKey(autoGenerate = true) val exerciseId: Int = 0,
    val name: String?,
    val category: String?,
    val createdByUser: Boolean
)