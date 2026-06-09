package com.example.cleantodoapp.domain.usecase


import com.example.cleantodoapp.domain.entity.Todo
import com.example.cleantodoapp.domain.repostory.ToDoRepasitory

class GetTodosUsecase(private val repasitory: ToDoRepasitory) {
    operator fun invoke(): List<Todo> {
        return repasitory.getTodos()
    }
}