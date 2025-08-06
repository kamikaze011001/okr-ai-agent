# OKR Backend - Developer Guide

Welcome to the OKR Backend project! This document will help you understand the project structure, technology stack, and how to implement new features.

## 1. Project Overview

The OKR Backend is a Spring Boot application written in Kotlin that follows a Use Case-Driven Modular Monolith architecture. The system is designed to manage Objectives and Key Results, along with related features like calendar scheduling and chat history.

### Key Features:
- Objective and Key Result management
- Calendar time blocking
- Chat history access (read-only)
- User profile management
- Progress tracking and history

### Architecture Principles:
- Clean Architecture with clear separation of concerns
- Single Responsibility Principle for each use case
- No JPA relations for performance optimization
- Strategic caching at use case level
- Easy migration path to microservices

## 2. Project Structure

```
src/main/kotlin/com/aibles/okragent/
├── OkrAgentApplication.kt          # Main application entry point
├── config/                         # Global configurations
├── exception/                      # Global exception handling
├── security/                       # Security configurations
├── modules/                        # Feature modules
│   ├── okr/                        # OKR Module
│   ├── calendar/                   # Calendar Module
│   ├── chat/                       # Chat Module (Read-Only)
│   ├── user/                       # User Management Module
│   └── shared/                     # Shared utilities
└── modules/{module-name}/
    ├── domain/
    │   └── model/                  # Domain models and enums
    ├── infrastructure/
    │   ├── config/                 # Module-specific configurations
    │   │   └── {Module}ModuleConfig.kt
    │   └── persistence/
    │       ├── entity/             # JPA entities (no relations)
    │       └── repository/         # Spring Data repositories
    ├── api/                        # REST controllers and DTOs
    │   ├── {Module}Controller.kt
    │   └── dto/                    # Data transfer objects
    └── usecase/                    # Business logic implementations
```

For example, the user module structure:
```
user/
├── api/
│   ├── UserController.kt
│   └── dto/
│       └── UserInitializationResponse.kt
├── domain/
│   └── model/                      # Domain models and enums
├── infrastructure/
│   ├── config/
│   │   └── UserModuleConfig.kt
│   └── persistence/
│       ├── entity/
│       │   ├── UserEntity.kt
│       │   └── NotificationPreferenceEntity.kt
│       └── repository/
│           └── UserRepository.kt
└── usecase/
    └── InitializeUserUseCase.kt
```

Each module follows the same structure:
- **API Layer**: Contains REST controllers and data transfer objects (DTOs)
- **Domain Layer**: Contains domain models and enums
- **Infrastructure Layer**: Contains technical implementations like JPA entities, Spring Data repositories, and module configurations
- **Use Case Layer**: Contains business logic implementations (at the module level)

## 3. Tech Stack

### Core Technologies:
- **Kotlin** (1.9.24) - Primary programming language
- **Spring Boot** (3.3.1) - Application framework
- **Java 21** - JVM version

### Key Dependencies:
- **Spring Web** - RESTful API framework
- **Spring Data JPA** - Data access abstraction
- **Spring Security** - Authentication and authorization
- **Spring Validation** - Input validation
- **PostgreSQL** - Primary database
- **Liquibase** - Database migration tool
- **Jackson Kotlin Module** - JSON serialization for Kotlin
- **SpringDoc OpenAPI** - API documentation

### Development Tools:
- **Gradle** - Build automation
- **JUnit 5** - Testing framework
- **Mockito Kotlin** - Mocking framework

## 4. How to Implement a Use Case

Follow these steps to implement a new use case, using the existing `InitializeUserUseCase` as a reference:

### Step 1: Define the Use Case Class

Create a new use case class in the module's `usecase` package:

```kotlin
@Transactional
class YourUseCase(
    private val repository: YourRepository
) {
    
    fun execute(request: YourRequest): YourResponse {
        // Implementation
    }
}
```

Note: Use cases are not annotated with `@Service` or `@Component`. They are configured in module config classes.

### Step 2: Create Request/Response DTOs

In the module's `api/dto` package, create data classes for request and response:

```kotlin
data class YourRequest(
    @field:NotBlank
    val name: String,
    
    @field:Positive
    val value: Int
)

data class YourResponse(
    val id: String,
    val name: String?,
    val value: Int
)
```

### Step 3: Register the Use Case in Module Configuration

Add a bean definition in the module's config class (e.g., in `infrastructure/config/UserModuleConfig.kt`):

```kotlin
@Configuration
class YourModuleConfig {

    @Bean
    fun yourUseCase(repository: YourRepository): YourUseCase {
        return YourUseCase(repository)
    }
}
```

### Step 4: Implement Business Logic

Follow this pattern in your use case:

```kotlin
@Transactional
class YourUseCase(
    private val repository: YourRepository
) {
    
    fun execute(request: YourRequest): YourResponse {
        // 1. Validate inputs (if needed)
        validateRequest(request)
        
        // 2. Business logic
        val entity = YourEntity(
            name = request.name,
            value = request.value
        )
        
        // 3. Persistence
        val saved = repository.save(entity)
        
        // 4. Return response
        return YourResponse(
            id = saved.id!!,
            name = saved.name,
            value = saved.value
        )
    }
    
    private fun validateRequest(request: YourRequest) {
        // Add business validation rules
        if (request.value > 100) {
            throw ValidationException("value", "Value cannot exceed 100")
        }
    }
}
```

### Step 5: Add Repository Methods

If needed, add methods to your repository interface:

```kotlin
@Repository
interface YourRepository : JpaRepository<YourEntity, String> {
    // JpaRepository already provides findById, save, etc.
    // Add any custom query methods here if needed
    // fun findBySomeCriteria(criteria: String): List<YourEntity>
}
```

### Step 6: Create/Update Controller

Add endpoint to the appropriate controller:

```kotlin
@RestController
@RequestMapping("/api/v1/your-endpoint")
class YourController(
    private val yourUseCase: YourUseCase
) {
    
    @PostMapping
    fun createSomething(
        @Valid @RequestBody request: YourRequest
    ): StandardResponse<YourResponse> {
        val response = yourUseCase.execute(request)
        return StandardResponse(data = response)
    }
}
```

### Step 7: Write Unit Tests

Create unit tests for your use case in `src/test/kotlin/com/aibles/okragent/modules/{module-name}/usecase/`:

```kotlin
@ExtendWith(MockitoExtension::class)
class YourUseCaseTest {
    
    @Mock
    private lateinit var repository: YourRepository
    
    @InjectMocks
    private lateinit var yourUseCase: YourUseCase
    
    @Test
    fun `should create something successfully`() {
        // Given
        val request = YourRequest("Test", 50)
        
        whenever(repository.save(any<YourEntity>())).thenAnswer { 
            (it.arguments[0] as YourEntity).copy(id = "test-id") 
        }
        
        // When
        val result = yourUseCase.execute(request)
        
        // Then
        assertThat(result.name).isEqualTo(request.name)
        verify(repository).save(any<YourEntity>())
    }
}
```

### Best Practices:

1. **Single Responsibility**: Each use case should do exactly one thing
2. **Validation First**: Always validate inputs at the beginning of the use case
3. **Transaction Boundaries**: Use `@Transactional` at the use case level
4. **Error Handling**: Use custom exceptions for business logic errors or leverage existing ones
5. **Security**: Always verify user ownership of resources when applicable
6. **Response Wrapping**: Use `StandardResponse` wrapper for API responses
7. **Configuration**: Register use cases in module configuration classes rather than component scanning
8. **Compilation**: Ensure code compiles successfully before writing tests
9. **Testing**: Write unit tests for all use cases with both positive and negative scenarios

This structure ensures that our codebase remains maintainable, testable, and follows clean architecture principles.
