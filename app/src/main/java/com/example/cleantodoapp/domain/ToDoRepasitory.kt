package com.example.cleantodoapp.domain

import kotlinx.coroutines.flow.Flow

interface ToDoRepasitory {
    fun getTodos(): Flow<List<Todo>>
    suspend fun addTodo(todo: Todo)
    suspend fun updateTodo(todo: Todo)
    suspend fun deleteTodo(todo: Todo)
}