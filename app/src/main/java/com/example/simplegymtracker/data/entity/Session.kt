package com.example.simplegymtracker.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "sessions",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            //Columna de donde saco el FK
            parentColumns = ["id"],
            //Donde quiero guardarlo
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Session(
    @PrimaryKey(autoGenerate = true) val sessionId: Int = 0,
    val userId: Int,
    val date: Long // Represented as a timestamp
)
