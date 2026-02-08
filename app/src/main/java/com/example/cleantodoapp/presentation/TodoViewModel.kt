package com.example.cleantodoapp.presentation

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.example.cleantodoapp.data.ToDoDataRepasitory
import com.example.cleantodoapp.data.ToDoDatabase
import com.example.cleantodoapp.domain.AddTodoUseCase
import com.example.cleantodoapp.domain.Todo
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

open class TodoViewModel(application: Application): AndroidViewModel(application) {

    private val db = Room.databaseBuilder(
        application,
        ToDoDatabase::class.java,
        "my_todo_db"
    ).build()

    private val toDoRepasitory = ToDoDataRepasitory(db.todoDao())
    private val addUseCase = AddTodoUseCase(toDoRepasitory)

    open val todos = toDoRepasitory.getTodos().stateIn(
        viewModelScope, SharingStarted.Lazily,emptyList()
    )

    fun addTodo(title:String){
        viewModelScope.launch {
            addUseCase.execute(title)
        }
    }

    fun toggleTodoDone(todo: Todo){
        viewModelScope.launch {
            toDoRepasitory.updateTodo(todo.copy(isDone = !todo.isDone))
        }
    }

    fun editTodo(todo: Todo, newTitle:String){
        viewModelScope.launch {
            if(newTitle.isNotBlank()){
                toDoRepasitory.updateTodo(todo.copy(title = newTitle))
            }
        }
    }
    fun delateTodo(todo: Todo){
        viewModelScope.launch {
            toDoRepasitory.deleteTodo(todo)
        }
    }
}