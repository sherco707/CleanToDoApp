package com.example.cleantodoapp.di

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.example.cleantodoapp.data.database.ToDoDatabase
import com.example.cleantodoapp.data.repository.RoomTodoRepository
import com.example.cleantodoapp.domain.repository.TodoRepository
import com.example.cleantodoapp.domain.usecase.AddTodoUseCase
import com.example.cleantodoapp.domain.usecase.DeleteTodoUseCase
import com.example.cleantodoapp.domain.usecase.GetTodosUseCase
import com.example.cleantodoapp.domain.usecase.UpdateTodoUseCase
import com.example.cleantodoapp.presentation.TodoViewModelFactory

class AppContainer(context: Context) {
    private val database: ToDoDatabase by lazy { ToDoDatabase.create(context) }

    private val todoRepository: TodoRepository by lazy {
        RoomTodoRepository(database.todoDao())
    }

    val todoViewModelFactory: ViewModelProvider.Factory by lazy {
        TodoViewModelFactory(
            getTodosUseCase = GetTodosUseCase(todoRepository),
            addTodoUseCase = AddTodoUseCase(todoRepository),
            updateTodoUseCase = UpdateTodoUseCase(todoRepository),
            deleteTodoUseCase = DeleteTodoUseCase(todoRepository),
        )
    }
}
