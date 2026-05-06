package com.example.cleantodoapp.domain.usecase

import com.example.cleantodoapp.domain.entity.Todo
import com.example.cleantodoapp.domain.repostory.ToDoRepasitory

class UpdateTodoUsecase(private var repostory : ToDoRepasitory) {
    operator fun invoke(todo: Todo)=repostory.updateTodo(todo)

}