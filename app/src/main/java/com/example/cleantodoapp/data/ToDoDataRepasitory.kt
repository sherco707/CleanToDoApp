package com.example.cleantodoapp.data

import com.example.cleantodoapp.domain.ToDoRepasitory
import com.example.cleantodoapp.domain.Todo
import kotlinx.coroutines.flow.Flow
import com.example.cleantodoapp.data.ToDoDao
import kotlinx.coroutines.flow.map

class ToDoDataRepasitory(private val dao: ToDoDao): ToDoRepasitory {
    override fun getTodos(): Flow<List<Todo>> {
        return dao.getAllTodos().map { list ->
            list.map {entity ->
                Todo(
                    id = entity.id,
                    title = entity.title,
                    isDone = entity.isDone
                )
            }
        }
    }

    override suspend fun addTodo(todo: Todo) {
        dao.insert(TodoEntity(title = todo.title,
            isDone = todo.isDone))
    }

    override suspend fun updateTodo(todo: Todo) {
        dao.update(TodoEntity(id = todo.id, title = todo.title,
            isDone = todo.isDone))
    }

    override suspend fun deleteTodo(todo: Todo) {
        dao.delete(TodoEntity(id = todo.id, title = todo.title,
            isDone = todo.isDone))
    }

}