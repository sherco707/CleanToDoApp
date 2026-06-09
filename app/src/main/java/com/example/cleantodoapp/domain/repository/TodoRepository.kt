package com.example.cleantodoapp.domain.repository

import com.example.cleantodoapp.domain.entity.Todo
import kotlinx.coroutines.flow.Flow

interface TodoRepository {
    fun observeTodos(): Flow<Result<List<Todo>>>

    suspend fun addTodo(todo: Todo): Result<Unit>

    suspend fun updateTodo(todo: Todo): Result<Unit>

    suspend fun deleteTodo(todo: Todo): Result<Unit>
}
