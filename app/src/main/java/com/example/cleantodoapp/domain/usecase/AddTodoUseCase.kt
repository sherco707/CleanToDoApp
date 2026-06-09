package com.example.cleantodoapp.domain.usecase

import com.example.cleantodoapp.domain.entity.Todo
import com.example.cleantodoapp.domain.repository.TodoRepository

class AddTodoUseCase(private val repository: TodoRepository) {
    suspend operator fun invoke(title: String): Result<Unit> {
        val normalizedTitle = title.trim()

        if (normalizedTitle.isBlank()) {
            return Result.failure(IllegalArgumentException("Todo title cannot be blank."))
        }

        return repository.addTodo(
            Todo(
                title = normalizedTitle,
                createdAt = System.currentTimeMillis(),
            ),
        )
    }
}
