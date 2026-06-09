package com.example.cleantodoapp.domain.usecase

import com.example.cleantodoapp.domain.entity.Todo
import com.example.cleantodoapp.domain.repository.TodoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TodoUseCaseTest {

    private val repository = FakeTodoRepository()

    @Test
    fun addTodo_rejectsBlankTitle() = runBlocking {
        val result = AddTodoUseCase(repository)("   ")

        assertTrue(result.isFailure)
        assertTrue(repository.savedTodos.isEmpty())
    }

    @Test
    fun addTodo_trimsTitleBeforeSaving() = runBlocking {
        val result = AddTodoUseCase(repository)("  Buy milk  ")

        assertTrue(result.isSuccess)
        assertEquals("Buy milk", repository.savedTodos.single().title)
    }

    @Test
    fun updateTodo_rejectsBlankTitle() = runBlocking {
        val result = UpdateTodoUseCase(repository)(Todo(id = 1, title = "   "))

        assertTrue(result.isFailure)
        assertTrue(repository.updatedTodos.isEmpty())
    }

    @Test
    fun updateTodo_trimsTitleBeforeSaving() = runBlocking {
        val result = UpdateTodoUseCase(repository)(Todo(id = 1, title = "  Read docs  "))

        assertTrue(result.isSuccess)
        assertEquals("Read docs", repository.updatedTodos.single().title)
    }

    private class FakeTodoRepository : TodoRepository {
        val savedTodos = mutableListOf<Todo>()
        val updatedTodos = mutableListOf<Todo>()

        override fun observeTodos(): Flow<Result<List<Todo>>> = flowOf(Result.success(emptyList()))

        override suspend fun addTodo(todo: Todo): Result<Unit> {
            savedTodos += todo
            return Result.success(Unit)
        }

        override suspend fun updateTodo(todo: Todo): Result<Unit> {
            updatedTodos += todo
            return Result.success(Unit)
        }

        override suspend fun deleteTodo(todo: Todo): Result<Unit> = Result.success(Unit)
    }
}
