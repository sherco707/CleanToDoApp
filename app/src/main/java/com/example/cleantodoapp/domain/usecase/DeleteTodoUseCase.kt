package com.example.cleantodoapp.domain.usecase

import com.example.cleantodoapp.domain.entity.Todo
import com.example.cleantodoapp.domain.repository.TodoRepository

class DeleteTodoUseCase(private val repository: TodoRepository) {
    suspend operator fun invoke(todo: Todo): Result<Unit> = repository.deleteTodo(todo)
}
