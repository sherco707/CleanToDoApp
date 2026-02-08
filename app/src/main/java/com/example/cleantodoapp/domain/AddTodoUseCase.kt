package com.example.cleantodoapp.domain

class AddTodoUseCase(private val toDoRepasitory: ToDoRepasitory) {
    suspend fun execute(title:String) {
        toDoRepasitory.addTodo(Todo(title = title, isDone = false))
    }

}