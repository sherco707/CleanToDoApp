package com.example.cleantodoapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cleantodoapp.domain.entity.Todo
import com.example.cleantodoapp.domain.usecase.AddTodoUseCase
import com.example.cleantodoapp.domain.usecase.DeleteTodoUseCase
import com.example.cleantodoapp.domain.usecase.GetTodosUseCase
import com.example.cleantodoapp.domain.usecase.UpdateTodoUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TodoUiState(
    val todos: List<Todo> = emptyList(),
    val isLoading: Boolean = true,
    val userMessage: UiMessage? = null,
)

data class UiMessage(
    val id: Long,
    val text: String,
)

class TodoViewModel(
    getTodosUseCase: GetTodosUseCase,
    private val addTodoUseCase: AddTodoUseCase,
    private val updateTodoUseCase: UpdateTodoUseCase,
    private val deleteTodoUseCase: DeleteTodoUseCase,
) : ViewModel() {

    private var nextMessageId = 0L
    private val pendingUserMessage = MutableStateFlow<UiMessage?>(null)

    val uiState: StateFlow<TodoUiState> = getTodosUseCase()
        .map { result ->
            result.fold(
                onSuccess = { todos -> TodoUiState(todos = todos, isLoading = false) },
                onFailure = { error ->
                    TodoUiState(
                        isLoading = false,
                        userMessage = error.toUiMessage(),
                    )
                },
            )
        }
        .combine(pendingUserMessage) { state, message ->
            state.copy(userMessage = message ?: state.userMessage)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = TodoUiState(),
        )

    fun addTodo(title: String) {
        viewModelScope.launch {
            addTodoUseCase(title).handleResult(
                successMessage = "Vazifa qo'shildi",
            )
        }
    }

    fun deleteTodo(todo: Todo) {
        viewModelScope.launch {
            deleteTodoUseCase(todo).handleResult(
                successMessage = "Vazifa o'chirildi",
            )
        }
    }

    fun updateTodo(todo: Todo) {
        viewModelScope.launch {
            updateTodoUseCase(todo).handleResult(
                successMessage = "Vazifa yangilandi",
            )
        }
    }

    fun userMessageShown() {
        pendingUserMessage.value = null
    }

    private fun Result<Unit>.handleResult(successMessage: String) {
        pendingUserMessage.value = fold(
            onSuccess = { successMessage.toUiMessage() },
            onFailure = { it.toUiMessage() },
        )
    }

    private fun Throwable.toUiMessage(): UiMessage = toUserMessage().toUiMessage()

    private fun String.toUiMessage(): UiMessage = UiMessage(
        id = nextMessageId++,
        text = this,
    )

    private fun Throwable.toUserMessage(): String {
        return when (this) {
            is IllegalArgumentException -> message ?: "Kiritilgan ma'lumot noto'g'ri"
            else -> "Kutilmagan xatolik yuz berdi. Qayta urinib ko'ring."
        }
    }
}

class TodoViewModelFactory(
    private val getTodosUseCase: GetTodosUseCase,
    private val addTodoUseCase: AddTodoUseCase,
    private val updateTodoUseCase: UpdateTodoUseCase,
    private val deleteTodoUseCase: DeleteTodoUseCase,
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TodoViewModel::class.java)) {
            return TodoViewModel(
                getTodosUseCase = getTodosUseCase,
                addTodoUseCase = addTodoUseCase,
                updateTodoUseCase = updateTodoUseCase,
                deleteTodoUseCase = deleteTodoUseCase,
            ) as T
        }

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
