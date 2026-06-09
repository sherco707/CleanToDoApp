package com.example.cleantodoapp.domain.usecase

import com.example.cleantodoapp.domain.entity.Todo
import com.example.cleantodoapp.domain.repository.TodoRepository

class UpdateTodoUseCase(private val repository: TodoRepository) {
    suspend operator fun invoke(todo: Todo): Result<Unit> {
        if (todo.title.isBlank()) {
            return Result.failure(IllegalArgumentException("Todo title cannot be blank."))
        }

        return repository.updateTodo(todo.copy(title = todo.title.trim()))
    }
}
