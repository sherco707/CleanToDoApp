package com.example.cleantodoapp.data.database

import com.example.cleantodoapp.domain.entity.Todo

fun Todo.toEntity(): TodoEntity = TodoEntity(
    id = id,
    title = title,
    createdAt = createdAt,
    isCompleted = isCompleted,
)

fun TodoEntity.toDomain(): Todo = Todo(
    id = id,
    title = title,
    createdAt = createdAt,
    isCompleted = isCompleted,
)
