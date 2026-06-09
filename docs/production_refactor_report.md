# Production Refactor Report

This report documents each architectural change made in the refactor, including the original approach, the improved implementation, and the reason for the change.

## 1. Dependency injection and app-level composition

### Original code

```kotlin
class TodoViewModel(application: Application) : AndroidViewModel(application) {
    private val imple = Imple(application)
    private val getUscase = GetTodosUsecase(imple)
    private val addUseCase = AddTodoUseCase(imple)
    private val deleteUseCase = DeleteTodoUseCase(imple)
    private val updateUseCase = UpdateTodoUsecase(imple)
}
```

### Improved code

```kotlin
class CleanTodoApplication : Application() {
    val appContainer: AppContainer by lazy { AppContainer(this) }
}

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
```

### Reason

The ViewModel no longer constructs data-layer objects or depends on `Application`. Dependencies are composed once at the application boundary and injected through a `ViewModelProvider.Factory`, improving SOLID compliance, testability, and lifecycle safety.

## 2. Repository abstraction and naming

### Original code

```kotlin
interface ToDoRepasitory {
    fun getTodos(): List<Todo>
    fun addTodo(todo: Todo)
    fun updateTodo(todo: Todo)
    fun deleteTodo(todo:Todo)
}
```

### Improved code

```kotlin
interface TodoRepository {
    fun observeTodos(): Flow<Result<List<Todo>>>

    suspend fun addTodo(todo: Todo): Result<Unit>

    suspend fun updateTodo(todo: Todo): Result<Unit>

    suspend fun deleteTodo(todo: Todo): Result<Unit>
}
```

### Reason

The repository contract now exposes reactive reads with `Flow`, writes are suspendable, and every operation returns `Result` for explicit error handling. The misspelled package and interface names were replaced with production-readable names.

## 3. Room DAO threading and reactive data

### Original code

```kotlin
@Query("SELECT * FROM mytodos ORDER BY createdAt DESC, id DESC")
fun getTodos(): List<TodoEntity>

@Insert(onConflict = OnConflictStrategy.REPLACE)
fun addTitle(todo: TodoEntity)

@Update
fun update(todo: TodoEntity)

@Delete
fun delete(todo: TodoEntity)
```

### Improved code

```kotlin
@Query("SELECT * FROM mytodos ORDER BY createdAt DESC, id DESC")
fun observeTodos(): Flow<List<TodoEntity>>

@Insert(onConflict = OnConflictStrategy.REPLACE)
suspend fun insert(todo: TodoEntity)

@Update
suspend fun update(todo: TodoEntity)

@Delete
suspend fun delete(todo: TodoEntity)
```

### Reason

Synchronous Room APIs were replaced with `Flow` and `suspend` functions. This prevents main-thread blocking, automatically updates the UI when the table changes, and scales better as the database grows.

## 4. Database creation

### Original code

```kotlin
fun getDb(context: Context): ToDoDatabase {
    return Room.databaseBuilder(context, ToDoDatabase::class.java, "task.db")
        .addMigrations(MIGRATION_1_2)
        .allowMainThreadQueries()
        .build()
}
```

### Improved code

```kotlin
fun create(context: Context): ToDoDatabase {
    return Room.databaseBuilder(
        context.applicationContext,
        ToDoDatabase::class.java,
        DATABASE_NAME,
    )
        .addMigrations(MIGRATION_1_2)
        .build()
}
```

### Reason

`allowMainThreadQueries()` was removed to protect UI performance, the application context is used to avoid leaking activities, and the database name is centralized as a constant.

## 5. Entity mapping

### Original code

```kotlin
class Mapper {
    fun todotoTodoentity(todo: Todo): TodoEntity = TodoEntity(...)
    fun todoentityToTodo(todoEntity: TodoEntity): Todo = Todo(...)
}
```

### Improved code

```kotlin
fun Todo.toEntity(): TodoEntity = TodoEntity(...)

fun TodoEntity.toDomain(): Todo = Todo(...)
```

### Reason

Stateless extension functions remove unnecessary object allocation, make mappings discoverable at call sites, and use Android/Kotlin naming conventions.

## 6. Data-layer implementation

### Original code

```kotlin
class Imple(context: Context): ToDoRepasitory {
    private val db = ToDoDatabase.getDb(context).todoDao()
    val map = Mapper()

    override fun getTodos(): List<Todo> {
        val entityList = db.getTodos()
        return entityList.map { map.todoentityToTodo(it) }
    }
}
```

### Improved code

```kotlin
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
}
```

### Reason

The implementation now depends on a DAO abstraction instead of creating the database itself, uses an injectable dispatcher for tests, maps errors into `Result`, and keeps database work off the main thread.

## 7. Use-case validation and single responsibility

### Original code

```kotlin
class AddTodoUseCase(private val toDoRepasitory: ToDoRepasitory) {
    operator fun invoke(todo: Todo) = toDoRepasitory.addTodo(todo)
}
```

### Improved code

```kotlin
class AddTodoUseCase(private val repository: TodoRepository) {
    suspend operator fun invoke(title: String): Result<Unit> {
        val normalizedTitle = title.trim()

        if (normalizedTitle.isBlank()) {
            return Result.failure(IllegalArgumentException("Todo title cannot be blank."))
        }

        return repository.addTodo(
            Todo(
                title = normalizedTitle,
                createdAt = System.currentTimeMillis(),
            ),
        )
    }
}
```

### Reason

Validation and domain object creation moved into the domain layer. Presentation code no longer duplicates validation rules, and the use case communicates validation errors explicitly.

## 8. ViewModel state management

### Original code

```kotlin
fun addTodo(title: String) {
    val todo = Todo(
        title = title,
        createdAt = System.currentTimeMillis(),
    )
    addUseCase(todo)
}

fun getTodos(): List<Todo> {
    return getUscase()
}
```

### Improved code

```kotlin
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
```

### Reason

The ViewModel now owns a single immutable UI state stream instead of exposing blocking getters. This makes the UI lifecycle-aware, predictable, and easy to test.

## 9. Activity rendering and lifecycle collection

### Original code

```kotlin
private fun refreshTodos() {
    recAdapter.submitList(viewModel.getTodos())
}
```

### Improved code

```kotlin
private fun observeUiState() {
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.uiState.collect(::render)
        }
    }
}

private fun render(uiState: TodoUiState) {
    binding.addBtn.isEnabled = !uiState.isLoading
    binding.recyclerView.isVisible = !uiState.isLoading
    recAdapter.submitList(uiState.todos)
}
```

### Reason

Manual refresh calls were replaced with lifecycle-aware state collection. The UI updates automatically when Room emits changes and stops collecting when the activity is not visible.

## 10. Test coverage

### Original code

```kotlin
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
}
```

### Improved code

```kotlin
@Test
fun addTodo_trimsTitleBeforeSaving() = runBlocking {
    val result = AddTodoUseCase(repository)("  Buy milk  ")

    assertTrue(result.isSuccess)
    assertEquals("Buy milk", repository.savedTodos.single().title)
}
```

### Reason

The new tests verify real domain behavior: blank-title validation and title normalization. The fake repository demonstrates that the domain layer can be tested without Android, Room, or UI dependencies.
