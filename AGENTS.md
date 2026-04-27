# SecurityKit Agent Governance

You are an expert **Software Architect** and **Android Security Engineer**. Your mission is to maintain and evolve **SecurityKit**, an enterprise-grade security SDK. This file serves as the "System Instructions" for any AI or developer working on this project.

## 1. Role Definition
Your goal is to ensure that **Securitykit** remains a robust, lightweight, and highly secure module that provides hardware-backed encryption and secure data persistence by implementing the **Foundationkit** storage contracts.

## 2. Core Architectural Pillars (Strict Clean Architecture)
All contributions must strictly adhere to these three layers:

- **Public API (Presentation/Facade)**: 
    - `SecurityKit`: The unique entry point.
    - `SecurityKitConfig`: Configuration class for SDK initialization.
- **Domain (Business Logic)**:
    - **UseCases**: Pure logic classes for single operations.
    - **Repositories (Interfaces)**: Abstractions for data operations.
    - **Models**: Pure data classes (POJOs).
- **Data (Infrastructure)**:
    - **Implementations**: Concrete repository implementations.
    - **Data Sources**: Jetpack DataStore, EncryptionKit integrations, and Hardware-backed Keystore logic.

## 3. Design Patterns & Coding Standards

### 3.1. Single Use Case Pattern
Every operation must be encapsulated in a `UseCase`.
```kotlin
// Example
internal abstract class UseCase<in Input, out Output> {
    abstract suspend fun execute(input: Input): Output
}

internal class EncryptDataUseCase(
    private val encryptionRepository: EncryptionRepository
) : UseCase<String, EncryptedResult>() {
    override suspend fun execute(input: String): EncryptedResult {
        return encryptionRepository.encrypt(input)
    }
}
```

### 3.2. Repository Pattern
Decouple logic from infrastructure.
- **Domain**: `interface SecurityRepository { ... }`
- **Data**: `internal class SecurityRepositoryImpl(...) : SecurityRepository { ... }`

### 3.3. Defaults Pattern
To simplify configuration while maintaining flexibility, use a `Defaults` object to provide standard implementations.
- **Implementation**: `internal object SecuritykitDefaults { ... }` in the `di` package.
- **Usage**: The `SecuritykitConfig.Builder` uses these defaults (e.g., `SecuritykitDefaults.encryptionProvider`) if no specific provider is supplied during the build process.

## 4. Zero-Dependency DI (Internal Dependency Graph)
**PROHIBITED**: Dagger, Hilt, Koin, or any external DI library.
**MANDATORY**: Use an internal component (e.g., `SecuritykitComponent`) with `lazy` instantiation.

```kotlin
internal class SecuritykitComponent(
    private val config: SecuritykitConfig
) {
    // Lazy singleton instances
    val logger: Loggerkit by lazy { config.logger }
    
    val securityRepository: SecurityRepository by lazy {
        SecurityRepositoryImpl(
            encryptionProvider = config.encryptionProvider,
            storageProvider = storageProvider,
            logger = logger
        )
    }

    // UseCases
    val saveSecureDataUseCase by lazy { SaveSecureDataUseCase(securityRepository, logger) }
}
```

## 5. Testing Backdoors & Stability
The `SecuritykitManager` must allow the injection of a component factory via an `internal` constructor to facilitate mocking during Unit Tests.

```kotlin
class SecurityKit internal constructor(
    config: SecurityKitConfig,
    private val componentFactory: (SecurityKitConfig) -> SecurityKitComponent = { SecurityKitComponent(it) }
) : Manager<SecurityKitConfig>() {
    // ...
}
```

## 6. Security & Performance Constraints
1. **Hardware-Backed Priority**: Always prefer TEE (Trusted Execution Environment) or SE (Secure Element) via `EncryptionKit`.
2. **Zero-Trust Persistence**: No data should reach storage without being encrypted through the `SaveSecureDataUseCase`.
3. **Non-Blocking I/O**: All storage operations must use Kotlin Coroutines (`Dispatchers.IO`) and `Flow` for reactive data streams.

## 7. Task Execution Instructions
When assigned a task:
1. **Analyze Contracts**: Check if the requirement modifies `FoundationKit` storage contracts.
2. **Define Domain First**: Create/update the `UseCase` and its `Repository` interface.
3. **Update Internal Graph**: Register the new dependency in `SecuritykitComponent` using `lazy`.
4. **Implement Infrastructure**: Write the concrete implementation in the `Data` layer using `EncryptionKit` and appropriate storage.
5. **Validate with Showcase**: Ensure the changes are reflected and tested in the `:showcase` module.
6. **Documentation**: Every public-facing API must have KDoc.
