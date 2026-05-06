package com.example.cleantodoapp.data

import android.content.Context
import com.example.cleantodoapp.data.database.Mapper
import com.example.cleantodoapp.domain.repostory.ToDoRepasitory
import com.example.cleantodoapp.domain.entity.Todo
import com.example.cleantodoapp.data.database.ToDoDatabase
import com.example.cleantodoapp.data.database.TodoEntity

class Imple(context: Context): ToDoRepasitory {
    private val db= ToDoDatabase.getDb(context).todoDao()
    val map = Mapper()

    override fun getTodos():Todo {
        val allTodos = db.getTodos()
        return map.todoentityToTodo(getTodos())
    }

    override  fun addTodo(todo: Todo) {
        db.insert(map.todotoTodoentity(todo))
    }

    override  fun updateTodo(todo: Todo) {

        db.update(map.todotoTodoentity(todo))
    }

    override  fun deleteTodo(todo: Todo) {
        db.delete(map.todotoTodoentity(todo))
    }
}