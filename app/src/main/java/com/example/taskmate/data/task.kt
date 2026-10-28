package com.example.taskmate.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val courseName: String,
    val title: String,
    val description: String,
    val deadline: String,
    val priority: String,
    val status: String = "Belum Dikerjakan"
)


