package com.example.simplegymtracker.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity
data class TimerConfig(
    @PrimaryKey(autoGenerate = true) val timerConfigId: Int = 0,
    val setRestTime: Int,
    val exerciseRestTime: Int,
)
