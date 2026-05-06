package com.example.cleantodoapp.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cleantodoapp.data.Imple
import com.example.cleantodoapp.data.database.ToDoDatabase
import com.example.cleantodoapp.domain.usecase.AddTodoUseCase
import com.example.cleantodoapp.domain.entity.Todo
import com.example.cleantodoapp.domain.usecase.DeleteTodoUseCase
import com.example.cleantodoapp.domain.usecase.GetTodosUsecase
import com.example.cleantodoapp.domain.usecase.UpdateTodoUsecase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TodoViewModel(application: Application) : AndroidViewModel(application) {

    private val imple = Imple(application)
    private val getUscase = GetTodosUsecase(imple)
    private val addUseCase = AddTodoUseCase(imple)
    private val deleteUseCase = DeleteTodoUseCase(imple)
    private val updateUseCase = UpdateTodoUsecase(imple)


    fun addTodo(title: String) {
        val todo = Todo(title = title)
        addUseCase(todo)
    }

    fun deleteTodo(todo: Todo) {
        deleteUseCase(todo)
    }

    fun getTodos(): List<Todo>{
        return getUscase()
    }

    fun editTodo(todo: Todo) {
        updateUseCase(todo)
    }
}