package com.example.cleantodoapp.data.repository

import com.example.cleantodoapp.data.database.ToDoDao
import com.example.cleantodoapp.data.database.toDomain
import com.example.cleantodoapp.data.database.toEntity
import com.example.cleantodoapp.domain.entity.Todo
import com.example.cleantodoapp.domain.repository.TodoRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class RoomTodoRepository(
    private val todoDao: ToDoDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : TodoRepository {

    override fun observeTodos(): Flow<Result<List<Todo>>> {
        return todoDao.observeTodos()
            .map { entities -> Result.success(entities.map { it.toDomain() }) }
            .catch { throwable -> emit(Result.failure(throwable)) }
            .flowOn(ioDispatcher)
    }

    override suspend fun addTodo(todo: Todo): Result<Unit> = executeDatabaseOperation {
        todoDao.insert(todo.toEntity())
    }

    override suspend fun updateTodo(todo: Todo): Result<Unit> = executeDatabaseOperation {
        todoDao.update(todo.toEntity())
    }

    override suspend fun deleteTodo(todo: Todo): Result<Unit> = executeDatabaseOperation {
        todoDao.delete(todo.toEntity())
    }

    private suspend fun executeDatabaseOperation(block: suspend () -> Unit): Result<Unit> {
        return withContext(ioDispatcher) {
            try {
                block()
                Result.success(Unit)
            } catch (throwable: Throwable) {
                Result.failure(throwable)
            }
        }
    }
}
