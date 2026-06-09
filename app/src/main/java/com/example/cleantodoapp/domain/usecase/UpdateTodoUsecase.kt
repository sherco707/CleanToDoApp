package com.example.cleantodoapp.domain.usecase

import com.example.cleantodoapp.domain.entity.Todo
import com.example.cleantodoapp.domain.repostory.ToDoRepasitory

class UpdateTodoUsecase(private val repository: ToDoRepasitory) {
    operator fun invoke(todo: Todo) = repository.updateTodo(todo)
}