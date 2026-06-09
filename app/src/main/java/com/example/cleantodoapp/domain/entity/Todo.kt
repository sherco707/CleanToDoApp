package com.example.cleantodoapp.domain.entity

data class Todo(
    val id: Int = 0,
    val title: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = false,
)
