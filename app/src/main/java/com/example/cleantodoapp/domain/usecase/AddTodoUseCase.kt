package com.example.cleantodoapp.domain.usecase

import com.example.cleantodoapp.domain.repostory.ToDoRepasitory
import com.example.cleantodoapp.domain.entity.Todo

class AddTodoUseCase(private val toDoRepasitory: ToDoRepasitory) {
    operator fun invoke(todo: Todo)=toDoRepasitory.addTodo(todo)
}