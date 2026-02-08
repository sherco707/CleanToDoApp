package com.example.cleantodoapp.domain

data class Todo(
    val id:Int = 0,
    val title: String,
    val isDone: Boolean = false
)
