package com.example.cleantodoapp.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "mytodos")
data class TodoEntity (
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
)