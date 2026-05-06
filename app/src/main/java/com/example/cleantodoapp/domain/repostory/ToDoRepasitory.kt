package com.example.cleantodoapp.domain.repostory

import com.example.cleantodoapp.domain.entity.Todo
import kotlinx.coroutines.flow.Flow

interface ToDoRepasitory {
    fun getTodos() :Todo
     fun addTodo(todo: Todo)
     fun updateTodo(todo: Todo)
     fun deleteTodo(todo:Todo)
}