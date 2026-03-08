# Android Online Store Architecture Analysis

## Overview

This document analyzes the clean architecture implementation in this Android Jetpack Compose application, focusing on the example feature as a reference. The application follows a well-structured clean architecture pattern with clear separation of concerns.

## Clean Architecture Layers

### 1. Data Layer (`data/`)

The data layer handles data operations and implements repositories that interact with external sources.

#### Example: `ExampleRepository.kt`

```kotlin
// Interface definition
interface IExampleRepository {
    suspend fun example(...): List<Example>
}

// Implementation
class ExampleRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
) : IExampleRepository {

    override suspend fun example(...): List<Example> {
        // API call implementation
        val response = apiService.getExample(queryParameters)
        // Data transformation logic
        return response.map { it.toDomain() }
    }
}
```

**Key Patterns:**

- Interfaces for testability and loose coupling
- Dependency injection through `@Inject constructor`
- API service abstraction (Retrofit)
- Data transformation using mapper functions

### 2. Model Layer (`models/`)

Models represent business entities and are immutable.

#### Example: `Example.kt` (optional)

```kotlin
data class Example(
    val id: Int,
    val exampleId: Int?,
    val example: String?,
    // ... other properties
)
```

**Key Patterns:**

- Immutable design with Kotlin `data class`
- Built-in `.copy()` method for functional updates — no manual `copyWith` needed

### 3. Controller Layer (`controller/`)

ViewModels manage state and business logic using reactive programming patterns.

#### Example: `ExampleViewModel.kt` (required)

```kotlin
// ExampleState.kt
sealed class ExampleState {
    object Initial : ExampleState()
    object InProgress : ExampleState()
    data class Error(val message: String) : ExampleState()
    data class Completed(...) : ExampleState()
}

// ExampleViewModel.kt
@HiltViewModel
class ExampleViewModel @Inject constructor(
    private val exampleRepository: IExampleRepository,
) : ViewModel() {

    private val _state = MutableStateFlow<ExampleState>(ExampleState.Initial)
    val state: StateFlow<ExampleState> = _state.asStateFlow()

    fun load(...) {
        viewModelScope.launch {
            _state.value = ExampleState.InProgress

            try {
                val example = exampleRepository.example(...)
                _state.value = ExampleState.Completed(...)
            } catch (e: Exception) {
                _state.value = ExampleState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
```

**Key Patterns:**

- `sealed class` for exhaustive, immutable state management
- `@HiltViewModel` + `@Inject constructor` for dependency injection
- `viewModelScope.launch` to run coroutines safely tied to the ViewModel lifecycle
- `MutableStateFlow` (private) / `StateFlow` (public) for reactive state
- State transitions: Initial → InProgress → Completed / Error

### 4. Widgets Layer (`widgets/`)

The presentation layer handles UI rendering and user interaction using Composable functions.

#### Example: `ExampleScreen.kt`

```kotlin
// The screen-level composable is the entry point for the feature.
// hiltViewModel() is how dependencies (ViewModel) are obtained — equivalent
// to DependenciesScope.of(context) in Flutter.
@Composable
fun ExampleScreen(
    onNavigateUp: () -> Unit,
    viewModel: ExampleViewModel = hiltViewModel(),
) {
    // LaunchedEffect(Unit) runs once when the composable enters the tree.
    // Equivalent to initState() in Flutter.
    LaunchedEffect(Unit) {
        viewModel.load(...)
    }

    // collectAsState() subscribes to the StateFlow.
    // The composable recomposes automatically whenever the state changes.
    val state by viewModel.state.collectAsState()

    when (val currentState = state) {
        is ExampleState.Initial, is ExampleState.InProgress -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
        is ExampleState.Error -> {
            Text(text = currentState.message, color = MaterialTheme.colorScheme.error)
        }
        is ExampleState.Completed -> {
            ExampleContent(
                data = currentState.data,
                uiState = viewModel.uiState.collectAsState().value,
                onAction = viewModel::onAction,
            )
        }
    }
}
```

**Key Patterns:**

- `hiltViewModel()` for ViewModel/dependency access — replaces `DependenciesScope.of(context)`
- `LaunchedEffect` for side effects tied to the composable lifecycle
- `collectAsState()` to observe `StateFlow` and trigger recomposition
- Composables receive only what they need (data + callbacks) — no direct ViewModel reference in children
- Responsive UI can be achieved with `WindowSizeClass` from the Material3 adaptive library

#### Example: `ExampleViewModel.kt` (UI State Management)

In addition to the async `ExampleState` (which mirrors application/network state), the ViewModel also holds a separate `ExampleUiState` for managing purely UI-related state — such as selected items, form field values, or toggle states. This is the Kotlin equivalent of `ExampleDataController with ChangeNotifier` in Flutter.

```kotlin
// ExampleUiState.kt — UI-only state, kept separate from async ExampleState
data class ExampleUiState(
    val from: String? = null,
    val to: String? = null,
    val selectedExamples: List<Example> = emptyList(),
)

// Inside ExampleViewModel.kt
@HiltViewModel
class ExampleViewModel @Inject constructor(
    private val exampleRepository: IExampleRepository,
) : ViewModel() {

    // Async application state (network/loading)
    private val _state = MutableStateFlow<ExampleState>(ExampleState.Initial)
    val state: StateFlow<ExampleState> = _state.asStateFlow()

    // UI-only state (selected items, form values, etc.)
    private val _uiState = MutableStateFlow(ExampleUiState())
    val uiState: StateFlow<ExampleUiState> = _uiState.asStateFlow()

    fun addExample(example: Example) {
        _uiState.update { current ->
            current.copy(selectedExamples = current.selectedExamples + example)
        }
    }

    fun setFrom(value: String?) {
        _uiState.update { it.copy(from = value) }
    }
}
```

**Key Patterns:**

- Separates async application state (`ExampleState` sealed class) from UI-only state (`ExampleUiState` data class)
- `ExampleUiState` is equivalent to `ExampleDataController with ChangeNotifier` in Flutter
- Uses `MutableStateFlow.update { }` for safe, atomic state mutations
- Both states are exposed as `StateFlow` and observed with `collectAsState()` in Composables
- Do not use other state solutions like: LiveData (for new code), RxJava, or third-party state managers for ViewModel state management

## Dependency Injection Patterns

### Global Dependency Injection

The application uses Hilt for global dependency injection, configured through `@Module` classes installed into Hilt components.

#### `AppModule.kt`

```kotlin
@Module
@InstallIn(SingletonComponent::class)  // dependencies live for the entire app lifetime
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindExampleRepository(impl: ExampleRepositoryImpl): IExampleRepository

    // Use @Provides (not @Binds) when you need to manually construct an object:
    // @Provides @Singleton
    // fun provideApiService(): ApiService = Retrofit.Builder()...build().create(ApiService::class.java)
}
```

#### `App.kt`

```kotlin
@HiltAndroidApp  // triggers Hilt code generation — must be on the Application class
class App : Application()
```

### Feature-Level Dependency Injection

Features get their ViewModel injected via `hiltViewModel()` inside Composables. No manual wiring needed.

#### `ExampleScreen.kt` (ViewModel initialization)

```kotlin
@Composable
fun ExampleScreen(
    viewModel: ExampleViewModel = hiltViewModel(),
) {
    // Hilt automatically creates ExampleViewModel with all its dependencies injected.
    // LaunchedEffect is the hook for one-time initialization, equivalent to initState().
    LaunchedEffect(Unit) {
        viewModel.load(...)
    }
}
```

## Interface Usage and DI Concepts

### Interface-Based Design

The application extensively uses interfaces for loose coupling:

```kotlin
// Repository interfaces
interface IExampleRepository { ... }
interface IProductsRepository { ... }
interface IExampleBalanceRepository { ... }
```

### Constructor-Based Dependency Injection

Dependencies are injected through `@Inject constructor`:

```kotlin
// Repository implementation receives API service
class ExampleRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
) : IExampleRepository

// ViewModel receives repository interface
@HiltViewModel
class ExampleViewModel @Inject constructor(
    private val exampleRepository: IExampleRepository,
    // Hilt resolves IExampleRepository to ExampleRepositoryImpl via AppModule @Binds
)

// Composables receive the ViewModel via hiltViewModel()
val viewModel: ExampleViewModel = hiltViewModel()
```

## Scope Management with Hilt

### Hilt Components and Scopes

Hilt replaces the manual `InheritedWidget` / `DependenciesScope` pattern with a component hierarchy managed at compile time.

```kotlin
// @SingletonComponent — lives for the entire app lifetime
// Use for: repositories, API clients, databases, shared preferences
@InstallIn(SingletonComponent::class)
abstract class AppModule { ... }

// @ViewModelComponent — lives for the ViewModel's lifetime
// Use for: use cases or helpers scoped to a single ViewModel
@InstallIn(ViewModelComponent::class)
abstract class ViewModelModule { ... }

// @ActivityRetainedComponent — lives across configuration changes for an Activity
// Rarely needed; prefer SingletonComponent or ViewModelComponent
```

### Sharing State Between Composables

To share ViewModel state between a parent screen and child composables, pass data and callbacks as parameters (the preferred Compose pattern):

```kotlin
@Composable
fun ExampleScreen(viewModel: ExampleViewModel = hiltViewModel()) {
    val uiState by viewModel.uiState.collectAsState()

    // Pass only what the child needs — not the whole ViewModel
    ExampleContent(
        selectedExamples = uiState.selectedExamples,
        onAddExample = viewModel::addExample,
    )
}

@Composable
fun ExampleContent(
    selectedExamples: List<Example>,
    onAddExample: (Example) -> Unit,
) {
    // Uses data directly — no ViewModel reference needed here
}
```

If a deeply nested composable needs ViewModel access and passing parameters becomes impractical, call `hiltViewModel()` directly inside that composable — Hilt will return the same scoped instance.

## Feature Structure Analysis: Example

### Directory Structure

```
example/
├── controller/
│   ├── ExampleViewModel.kt
│   └── ExampleState.kt
├── data/
│   ├── IExampleRepository.kt
│   └── ExampleRepositoryImpl.kt
├── models/
│   ├── Example.kt
│   ├── ExampleOther1.kt
│   ├── ExampleOther2.kt
│   ├── ExampleOther3.kt
│   └── ...
└── widgets/
    ├── components/
    │   └── ExampleCard.kt
    └── ExampleScreen.kt
```

### Layer Integration

1. **UI Layer**: `ExampleScreen.kt` calls `hiltViewModel()`, runs `LaunchedEffect` for initialization
2. **Presentation Layer**: `ExampleUiState` inside `ExampleViewModel` manages UI-only state
3. **Business Logic Layer**: `ExampleViewModel` handles async operations and application state
4. **Data Layer**: `ExampleRepositoryImpl` handles data operations
5. **Model Layer**: `Example.kt` represents domain entities

## Best Practices Observed

### 1. Separation of Concerns

- Each layer has a clear responsibility
- Models handle data representation
- ViewModels manage business logic and state
- Repositories handle data operations
- Composables handle presentation only

### 2. Testability

- Interface-based design enables mocking with frameworks like MockK
- `@Inject constructor` enables easy testing without Hilt in unit tests
- Immutable `data class` models reduce side effects
- `StateFlow` is testable with `Turbine` or `runTest`

### 3. Maintainability

- Consistent naming conventions (`I` prefix for interfaces, `Impl` suffix for implementations)
- Clear directory structure mirroring Flutter's feature-first layout
- Proper separation of business logic (`ExampleState`) from UI state (`ExampleUiState`)

### 4. Performance

- `StateFlow` only emits when the value actually changes (no redundant recompositions)
- `viewModelScope` automatically cancels all coroutines when the ViewModel is cleared
- `key` parameter in `LazyColumn`/`LazyVerticalGrid` enables efficient item diffing

## Additional Development Notes

### Generated Files

When working with this architecture, be aware of generated Kotlin/Java files that should be ignored in version control and never manually edited:

- Files inside `build/generated/` (generated by KSP from Hilt annotations)
- Files matching `Hilt_*.java` or `*_Factory.java` (Hilt component and factory classes)
- Files matching `*_MembersInjector.java` (Hilt field injection helpers)
- Room database generated files (if using Room): `*_Impl.kt`

These files are automatically regenerated on every Gradle build and are listed in `.gitignore` by default.

### Build / Code Generation Command

After creating a new feature or making changes that require code generation (such as adding new `@HiltViewModel`, `@Module`, `@Inject`, or Room `@Entity` annotations), trigger code generation by building the project:

```bash
./gradlew build
```

Or in Android Studio: **Build → Make Project** (Cmd+F9 / Ctrl+F9).

KSP runs automatically as part of the Gradle build — unlike Flutter's `build_runner`, there is no separate command to run.

### Model Copy Pattern

Kotlin `data class` provides `.copy()` built-in. For nullable fields, set them directly to `null` — no `ValueGetter` wrapper is needed:

```kotlin
data class Example(
    val id: Int,
    val parameter1: Double?,
    val parameter2: Double?,
    val invoicesQty: Double?,
    val returnsTotal: Double?,
    val paymentsTotal: Double?,
    val grandTotal: Double?,
    // ... other parameters
)

// Updating specific fields — nullable fields can be set to null directly
val updated = example.copy(
    parameter1 = 42.0,
    parameter2 = null,          // set to null directly — no ValueGetter needed
    grandTotal = newTotal,
)
```

### Accessing ViewModel State in Child Composables

To access `ExampleUiState` (initialized inside `ExampleScreen`) from a child composable, pass the values as parameters. For example, if `ExampleContentComposable` needs the `selectedExamples` list:

```kotlin
@Composable
fun ExampleScreen(
    viewModel: ExampleViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    ExampleContentComposable(
        selectedExamples = uiState.selectedExamples,
        from = uiState.from,
        to = uiState.to,
        onAddExample = viewModel::addExample,
        onSetFrom = viewModel::setFrom,
    )
}

@Composable
fun ExampleContentComposable(
    selectedExamples: List<Example>,
    from: String?,
    to: String?,
    onAddExample: (Example) -> Unit,
    onSetFrom: (String?) -> Unit,
) {
    // Build UI based on the passed state
    LazyColumn {
        items(selectedExamples) { example ->
            ExampleCard(example = example)
        }
    }
}
```

### Observing Multiple State Flows

When a composable needs both `state` (async) and `uiState` (UI), collect both:

```kotlin
@Composable
fun ExampleScreen(viewModel: ExampleViewModel = hiltViewModel()) {
    val state by viewModel.state.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    // Use collectAsStateWithLifecycle() instead of collectAsState() when targeting
    // Android lifecycle-awareness (requires lifecycle-runtime-compose dependency):
    // val state by viewModel.state.collectAsStateWithLifecycle()
}
```

### Coroutine Scope and Lifecycle

`viewModelScope` is the standard scope for ViewModel coroutines. It is automatically cancelled when the ViewModel is cleared (e.g., when the user navigates away permanently):

```kotlin
fun load() {
    // viewModelScope.launch = handle(() async { }) in Flutter
    // Coroutines launched here are cancelled automatically when the ViewModel is cleared
    viewModelScope.launch {
        _state.value = ExampleState.InProgress
        try {
            val result = exampleRepository.example(...)
            _state.value = ExampleState.Completed(result)
        } catch (e: Exception) {
            _state.value = ExampleState.Error(e.message ?: "Unknown error")
        }
    }
}
```

## Conclusion

This Android application demonstrates a well-implemented clean architecture with:

- Clear separation of concerns across data, domain, and presentation layers
- Comprehensive dependency injection using Hilt with both singleton and ViewModel-scoped dependencies
- Effective use of `StateFlow` + `collectAsState()` for reactive state propagation
- Consistent patterns across all features
- Proper lifecycle management through `viewModelScope` and Compose's `LaunchedEffect`

The example feature serves as an excellent reference for how the architecture principles are applied consistently throughout the application, maintaining scalability and maintainability while following Jetpack Compose best practices.
