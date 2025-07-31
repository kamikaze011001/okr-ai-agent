# OKR AI Agent - Modular Monolith Architecture (Use Case Based)

**Version:** 1.0  
**Date:** 29/07/2025  
**Status:** Draft

---

## Table of Contents

1. [System Overview](#1-system-overview)
2. [Module Structure](#2-module-structure)
3. [Package Organization](#3-package-organization)
4. [Use Case Architecture](#4-use-case-architecture)
5. [Data Layer Architecture](#5-data-layer-architecture)
6. [Security Architecture](#6-security-architecture)
7. [Cross-cutting Concerns](#7-cross-cutting-concerns)
8. [Technology Stack](#8-technology-stack)
9. [Development Guidelines](#9-development-guidelines)
10. [Migration Strategy](#10-migration-strategy)

---

## 1. System Overview

### 1.1 Architecture Goals

The OKR AI Agent backend is built as a **Use Case-Driven Modular Monolith** to achieve:

- **Clean Architecture**: Each use case has single responsibility
- **Maintainability**: Isolated use cases with clear boundaries
- **High Performance**: No JPA relations, optimized queries
- **Testability**: One use case = one test class
- **Scalability**: Easy migration to microservices when needed

### 1.2 High-Level Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                  AI Agent Backend (Separate)                │
│            (Handles AI Chat & Recommendations)              │
└─────────────────────────────────────────────────────────────┘
                              ↕ (Shared Database)
┌─────────────────────────────────────────────────────────────┐
│                  Spring Boot Application                    │
│  ┌──────────────┐ ┌──────────────┐ ┌──────────────┐        │
│  │  OKR Module  │ │Calendar Module│ │Chat Module   │        │
│  │  (Use Cases) │ │  (Use Cases)  │ │(Read-Only)   │        │
│  └──────────────┘ └──────────────┘ └──────────────┘        │
│  ┌──────────────┐ ┌──────────────┐                         │
│  │ User Module  │ │Shared Module │                         │
│  │ (Use Cases)  │ │              │                         │
│  └──────────────┘ └──────────────┘                         │
└─────────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────────┐
│                PostgreSQL Database (Shared)                 │
└─────────────────────────────────────────────────────────────┘
┌─────────────────────────────────────────────────────────────┐
│              Keycloak (Authentication)                      │
└─────────────────────────────────────────────────────────────┘
```

---

## 2. Module Structure

### 2.1 Module Overview

| Module | Responsibility | Use Cases | Dependencies |
|--------|---------------|-----------|--------------|
| **OKR Module** | Objective and Key Result management | 12 use cases | Shared |
| **Calendar Module** | Time blocking and scheduling | 6 use cases | Shared |
| **Chat Module** | Read-only chat history access | 4 use cases | Shared |
| **User Management Module** | User profiles and preferences | 4 use cases | Shared |
| **Shared Module** | Common utilities and configurations | N/A | None |

### 2.2 Module Use Cases

#### 2.2.1 OKR Module Use Cases
**Objective Management:**
- `CreateObjectiveUseCase` - Create new objective
- `UpdateObjectiveUseCase` - Update existing objective
- `GetObjectivesByUserUseCase` - Get user's objectives
- `GetObjectiveDetailUseCase` - Get objective with key results
- `DeleteObjectiveUseCase` - Delete objective

**Key Result Management:**
- `CreateKeyResultUseCase` - Create key result for objective
- `UpdateKeyResultUseCase` - Update key result details
- `UpdateProgressUseCase` - Update key result progress
- `GetKeyResultsByObjectiveUseCase` - Get key results for objective
- `DeleteKeyResultUseCase` - Delete key result

**Progress Tracking:**
- `GetProgressHistoryUseCase` - Get progress history for key result
- `CalculateCompletionUseCase` - Calculate objective completion percentage

#### 2.2.2 Calendar Module Use Cases
- `CreateTimeBlockUseCase` - Create new time block
- `UpdateTimeBlockUseCase` - Update time block
- `GetTimeBlocksByUserUseCase` - Get user's time blocks by date range
- `GetTimeBlocksByCategoryUseCase` - Get time blocks by category
- `DeleteTimeBlockUseCase` - Delete time block
- `CheckTimeConflictUseCase` - Check for time block conflicts

#### 2.2.3 Chat Module Use Cases (Read-Only)
- `GetUserSessionsUseCase` - Get user's chat sessions
- `GetSessionHistoryUseCase` - Get chat messages for session
- `SearchMessagesUseCase` - Search chat messages
- `GetSessionDetailUseCase` - Get session with metadata

#### 2.2.4 User Management Module Use Cases
- `CreateOrUpdateUserUseCase` - Create/update user profile
- `GetUserProfileUseCase` - Get user profile
- `UpdateNotificationPreferencesUseCase` - Update notification settings
- `GetNotificationPreferencesUseCase` - Get notification preferences

---

## 3. Package Organization

### 3.1 Root Package Structure

```
com.aibles.okragent
├── config/                          # Global configurations
├── security/                        # Security configurations
├── exception/                       # Global exception handling
├── modules/
│   ├── okr/                         # OKR Module
│   ├── calendar/                    # Calendar Module
│   ├── chat/                        # Chat Module (Read-Only)
│   ├── user/                        # User Management Module
│   └── shared/                      # Shared utilities
└── OkrAgentApplication.kt           # Main Spring Boot application
```

### 3.2 Module Package Structure (Example: OKR Module)

```
com.aibles.okragent.modules.okr/
├── api/                             # REST Controllers
│   ├── ObjectiveController.kt
│   ├── KeyResultController.kt
│   └── dto/
│       ├── CreateObjectiveRequest.kt
│       ├── ObjectiveResponse.kt
│       ├── CreateKeyResultRequest.kt
│       ├── KeyResultResponse.kt
│       └── ProgressHistoryResponse.kt
├── domain/                          # Business Logic
│   ├── usecase/                     # Use Cases (Single Responsibility)
│   │   ├── objective/
│   │   │   ├── CreateObjectiveUseCase.kt
│   │   │   ├── UpdateObjectiveUseCase.kt
│   │   │   ├── GetObjectivesByUserUseCase.kt
│   │   │   ├── GetObjectiveDetailUseCase.kt
│   │   │   └── DeleteObjectiveUseCase.kt
│   │   ├── keyresult/
│   │   │   ├── CreateKeyResultUseCase.kt
│   │   │   ├── UpdateKeyResultUseCase.kt
│   │   │   ├── UpdateProgressUseCase.kt
│   │   │   ├── GetKeyResultsByObjectiveUseCase.kt
│   │   │   └── DeleteKeyResultUseCase.kt
│   │   └── progress/
│   │       ├── GetProgressHistoryUseCase.kt
│   │       └── CalculateCompletionUseCase.kt
│   ├── model/                       # Domain Models
│   │   ├── Objective.kt
│   │   ├── KeyResult.kt
│   │   └── ProgressHistory.kt
│   └── repository/                  # Data Access Interfaces
│       ├── ObjectiveRepository.kt
│       ├── KeyResultRepository.kt
│       └── ProgressHistoryRepository.kt
├── infrastructure/                  # Technical Implementation
│   └── persistence/
│       └── entity/                  # JPA Entities (No Relations)
│           ├── ObjectiveEntity.kt
│           ├── KeyResultEntity.kt
│           └── ProgressHistoryEntity.kt
└── config/                          # Module-specific configs
    └── OkrModuleConfig.kt
```

---

## 4. Use Case Architecture

### 4.1 Use Case Pattern

Each use case follows this structure:

```kotlin
@Component
@Transactional
class CreateObjectiveUseCase(
    private val objectiveRepository: ObjectiveRepository,
    private val userContextProvider: UserContextProvider
) {
    
    fun execute(request: CreateObjectiveRequest): ObjectiveResponse {
        // 1. Validation
        validateRequest(request)
        
        // 2. Business Logic
        val entity = ObjectiveEntity(
            userId = userContextProvider.getCurrentUserId(),
            title = request.title,
            description = request.description,
            cycleType = request.cycleType,
            status = ObjectiveStatus.ACTIVE,
            startDate = request.startDate,
            endDate = request.endDate
        )
        
        // 3. Persistence
        val saved = objectiveRepository.save(entity)
        
        // 4. Response
        return saved.toResponse()
    }
    
    private fun validateRequest(request: CreateObjectiveRequest) {
        // Business validation logic
        if (request.endDate.isBefore(request.startDate)) {
            throw ValidationException("endDate", "End date must be after start date")
        }
    }
}
```

### 4.2 Controller Integration

Controllers become thin orchestrators:

```kotlin
@RestController
@RequestMapping("/api/v1/objectives")
@Validated
class ObjectiveController(
    private val createObjectiveUseCase: CreateObjectiveUseCase,
    private val updateObjectiveUseCase: UpdateObjectiveUseCase,
    private val getObjectivesByUserUseCase: GetObjectivesByUserUseCase,
    private val getObjectiveDetailUseCase: GetObjectiveDetailUseCase,
    private val deleteObjectiveUseCase: DeleteObjectiveUseCase
) {
    
    @PostMapping
    fun createObjective(
        @Valid @RequestBody request: CreateObjectiveRequest
    ): ResponseEntity<ObjectiveResponse> {
        val response = createObjectiveUseCase.execute(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }
    
    @GetMapping
    fun getMyObjectives(): ResponseEntity<List<ObjectiveResponse>> {
        val objectives = getObjectivesByUserUseCase.execute()
        return ResponseEntity.ok(objectives)
    }
    
    @GetMapping("/{id}")
    fun getObjectiveDetail(
        @PathVariable id: UUID
    ): ResponseEntity<ObjectiveDetailResponse> {
        val objective = getObjectiveDetailUseCase.execute(id)
        return ResponseEntity.ok(objective)
    }
    
    @PutMapping("/{id}")
    fun updateObjective(
        @PathVariable id: UUID,
        @Valid @RequestBody request: UpdateObjectiveRequest
    ): ResponseEntity<ObjectiveResponse> {
        val response = updateObjectiveUseCase.execute(id, request)
        return ResponseEntity.ok(response)
    }
    
    @DeleteMapping("/{id}")
    fun deleteObjective(@PathVariable id: UUID): ResponseEntity<Unit> {
        deleteObjectiveUseCase.execute(id)
        return ResponseEntity.noContent().build()
    }
}
```

### 4.3 Use Case Examples

#### 4.3.1 Complex Use Case with Multiple Repositories
```kotlin
@Component
@Transactional
class GetObjectiveDetailUseCase(
    private val objectiveRepository: ObjectiveRepository,
    private val keyResultRepository: KeyResultRepository,
    private val progressHistoryRepository: ProgressHistoryRepository,
    private val userContextProvider: UserContextProvider
) {
    
    @Cacheable(value = ["objectiveDetail"], key = "#objectiveId")
    fun execute(objectiveId: UUID): ObjectiveDetailResponse {
        val userId = userContextProvider.getCurrentUserId()
        
        // 1. Get objective and verify ownership
        val objective = objectiveRepository.findById(objectiveId)
            .orElseThrow { ObjectiveNotFoundException(objectiveId) }
            
        if (objective.userId != userId) {
            throw UnauthorizedException("Access denied to objective")
        }
        
        // 2. Get key results
        val keyResults = keyResultRepository.findByObjectiveIdOrderByCreatedAt(objectiveId)
        
        // 3. Get progress history for all key results
        val keyResultIds = keyResults.mapNotNull { it.id }
        val progressHistory = if (keyResultIds.isNotEmpty()) {
            progressHistoryRepository.findByKeyResultIds(keyResultIds)
        } else {
            emptyList()
        }
        
        // 4. Group progress by key result
        val progressByKeyResult = progressHistory.groupBy { it.keyResultId }
        
        // 5. Build response
        return objective.toDetailResponse(keyResults, progressByKeyResult)
    }
}
```

#### 4.3.2 Use Case with Business Logic
```kotlin
@Component
@Transactional
class UpdateProgressUseCase(
    private val keyResultRepository: KeyResultRepository,
    private val progressHistoryRepository: ProgressHistoryRepository,
    private val calculateCompletionUseCase: CalculateCompletionUseCase,
    private val userContextProvider: UserContextProvider
) {
    
    @CacheEvict(value = ["objectiveDetail", "objectives"], allEntries = true)
    fun execute(keyResultId: UUID, request: UpdateProgressRequest): KeyResultResponse {
        val userId = userContextProvider.getCurrentUserId()
        
        // 1. Get key result and verify ownership
        val keyResult = keyResultRepository.findById(keyResultId)
            .orElseThrow { KeyResultNotFoundException(keyResultId) }
            
        // Verify user owns this key result (through objective)
        val objective = objectiveRepository.findById(keyResult.objectiveId)
            .orElseThrow { ObjectiveNotFoundException(keyResult.objectiveId) }
            
        if (objective.userId != userId) {
            throw UnauthorizedException("Access denied to key result")
        }
        
        // 2. Validate progress value
        validateProgressValue(keyResult, request.newValue)
        
        // 3. Record progress history
        val progressHistory = ProgressHistoryEntity(
            keyResultId = keyResultId,
            previousValue = keyResult.currentValue,
            newValue = request.newValue,
            notes = request.notes,
            recordedAt = LocalDateTime.now(),
            updatedBy = userId
        )
        progressHistoryRepository.save(progressHistory)
        
        // 4. Update key result
        val updatedKeyResult = keyResult.copy(
            currentValue = request.newValue,
            updatedAt = LocalDateTime.now()
        )
        val saved = keyResultRepository.save(updatedKeyResult)
        
        // 5. Recalculate objective completion
        calculateCompletionUseCase.execute(keyResult.objectiveId)
        
        return saved.toResponse()
    }
    
    private fun validateProgressValue(keyResult: KeyResultEntity, newValue: BigDecimal) {
        when (keyResult.measurementType) {
            MeasurementType.PERCENTAGE -> {
                if (newValue < BigDecimal.ZERO || newValue > BigDecimal(100)) {
                    throw ValidationException("newValue", "Percentage must be between 0 and 100")
                }
            }
            MeasurementType.METRIC -> {
                if (newValue < BigDecimal.ZERO) {
                    throw ValidationException("newValue", "Metric value cannot be negative")
                }
            }
        }
    }
}
```

#### 4.3.3 Read-Only Use Case
```kotlin
@Component
@Transactional(readOnly = true)
class GetObjectivesByUserUseCase(
    private val objectiveRepository: ObjectiveRepository,
    private val keyResultRepository: KeyResultRepository,
    private val userContextProvider: UserContextProvider
) {
    
    @Cacheable(value = ["objectives"], key = "#root.target.userContextProvider.getCurrentUserId()")
    fun execute(): List<ObjectiveResponse> {
        val userId = userContextProvider.getCurrentUserId()
        
        // 1. Get objectives for user
        val objectives = objectiveRepository.findByUserIdOrderByCreatedAtDesc(userId)
        
        if (objectives.isEmpty()) {
            return emptyList()
        }
        
        // 2. Get key results for all objectives (single query)
        val objectiveIds = objectives.mapNotNull { it.id }
        val keyResults = keyResultRepository.findByObjectiveIds(objectiveIds)
        
        // 3. Group key results by objective
        val keyResultsByObjective = keyResults.groupBy { it.objectiveId }
        
        // 4. Map to response
        return objectives.map { objective ->
            val objectiveKeyResults = keyResultsByObjective[objective.id] ?: emptyList()
            objective.toResponseWithKeyResults(objectiveKeyResults)
        }
    }
}
```

### 4.4 Inter-Module Use Case Communication

```kotlin
// Calendar module use case calling OKR module use case
@Component
@Transactional
class CreateTimeBlockUseCase(
    private val timeBlockRepository: TimeBlockRepository,
    private val checkTimeConflictUseCase: CheckTimeConflictUseCase,
    private val getObjectiveDetailUseCase: GetObjectiveDetailUseCase, // From OKR module
    private val userContextProvider: UserContextProvider
) {
    
    fun execute(request: CreateTimeBlockRequest): TimeBlockResponse {
        val userId = userContextProvider.getCurrentUserId()
        
        // 1. Check for time conflicts
        checkTimeConflictUseCase.execute(request.startTime, request.endTime, userId)
        
        // 2. If related to objective, validate it exists
        request.relatedObjectiveId?.let { objectiveId ->
            getObjectiveDetailUseCase.execute(objectiveId) // Validates ownership
        }
        
        // 3. Create time block
        val entity = TimeBlockEntity(
            userId = userId,
            title = request.title,
            description = request.description,
            category = request.category,
            startTime = request.startTime,
            endTime = request.endTime,
            isRecurring = request.isRecurring,
            recurrencePattern = request.recurrencePattern,
            colorCode = request.colorCode
        )
        
        val saved = timeBlockRepository.save(entity)
        return saved.toResponse()
    }
}
```

---

## 5. Data Layer Architecture

### 5.1 Repository Interfaces

```kotlin
@Repository
interface ObjectiveRepository : JpaRepository<ObjectiveEntity, UUID> {
    
    @Query("SELECT o FROM ObjectiveEntity o WHERE o.userId = :userId ORDER BY o.createdAt DESC")
    fun findByUserIdOrderByCreatedAtDesc(userId: String): List<ObjectiveEntity>
    
    @Query("SELECT o FROM ObjectiveEntity o WHERE o.userId = :userId AND o.status = :status ORDER BY o.updatedAt DESC")
    fun findByUserIdAndStatusOrderByUpdatedAtDesc(userId: String, status: ObjectiveStatus): List<ObjectiveEntity>
    
    @Query("SELECT COUNT(o) FROM ObjectiveEntity o WHERE o.userId = :userId AND o.status = 'ACTIVE'")
    fun countActiveObjectivesByUser(userId: String): Long
}

@Repository
interface KeyResultRepository : JpaRepository<KeyResultEntity, UUID> {
    
    @Query("SELECT kr FROM KeyResultEntity kr WHERE kr.objectiveId = :objectiveId ORDER BY kr.createdAt ASC")
    fun findByObjectiveIdOrderByCreatedAt(objectiveId: UUID): List<KeyResultEntity>
    
    @Query("SELECT kr FROM KeyResultEntity kr WHERE kr.objectiveId IN :objectiveIds ORDER BY kr.objectiveId, kr.createdAt")
    fun findByObjectiveIds(objectiveIds: List<UUID>): List<KeyResultEntity>
    
    @Query("SELECT AVG(CASE WHEN kr.measurementType = 'PERCENTAGE' THEN kr.currentValue ELSE (kr.currentValue / kr.targetValue * 100) END) FROM KeyResultEntity kr WHERE kr.objectiveId = :objectiveId")
    fun calculateAverageCompletionByObjective(objectiveId: UUID): BigDecimal?
}
```

### 5.2 Entity Design (No JPA Relations)

```kotlin
@Entity
@Table(name = "objectives")
data class ObjectiveEntity(
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    val id: UUID? = null,

    @Column(name = "user_id", nullable = false)
    val userId: String,

    @Column(name = "title", nullable = false, length = 200)
    val title: String,

    @Column(name = "description", columnDefinition = "TEXT")
    val description: String?,

    @Enumerated(EnumType.STRING)
    @Column(name = "cycle_type", nullable = false)
    val cycleType: CycleType,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    val status: ObjectiveStatus,

    @Column(name = "start_date", nullable = false)
    val startDate: LocalDate,

    @Column(name = "end_date", nullable = false)
    val endDate: LocalDate,

    @Column(name = "completion_percentage", precision = 5, scale = 2)
    val completionPercentage: BigDecimal = BigDecimal.ZERO
) : BaseEntity() {

    fun toResponse(): ObjectiveResponse = ObjectiveResponse(
        id = id!!,
        title = title,
        description = description,
        cycleType = cycleType,
        status = status,
        startDate = startDate,
        endDate = endDate,
        completionPercentage = completionPercentage,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
    
    fun toResponseWithKeyResults(keyResults: List<KeyResultEntity>): ObjectiveResponse = 
        toResponse().copy(keyResults = keyResults.map { it.toResponse() })
    
    fun toDetailResponse(
        keyResults: List<KeyResultEntity>, 
        progressHistory: Map<UUID, List<ProgressHistoryEntity>>
    ): ObjectiveDetailResponse = ObjectiveDetailResponse(
        id = id!!,
        title = title,
        description = description,
        cycleType = cycleType,
        status = status,
        startDate = startDate,
        endDate = endDate,
        completionPercentage = completionPercentage,
        keyResults = keyResults.map { kr ->
            kr.toDetailResponse(progressHistory[kr.id] ?: emptyList())
        },
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
```

---

## 6. Security Architecture

### 6.1 Security in Use Cases

```kotlin
@Component
@Transactional
class DeleteObjectiveUseCase(
    private val objectiveRepository: ObjectiveRepository,
    private val keyResultRepository: KeyResultRepository,
    private val userContextProvider: UserContextProvider
) {
    
    @PreAuthorize("@userSecurityService.canAccessObjective(#objectiveId)")
    @CacheEvict(value = ["objectives", "objectiveDetail"], allEntries = true)
    fun execute(objectiveId: UUID) {
        val userId = userContextProvider.getCurrentUserId()
        
        // 1. Verify objective exists and user owns it
        val objective = objectiveRepository.findById(objectiveId)
            .orElseThrow { ObjectiveNotFoundException(objectiveId) }
            
        if (objective.userId != userId) {
            throw UnauthorizedException("Cannot delete objective owned by another user")
        }
        
        // 2. Delete related key results first
        keyResultRepository.deleteByObjectiveId(objectiveId)
        
        // 3. Delete objective
        objectiveRepository.delete(objective)
    }
}

@Component
class UserSecurityService(
    private val objectiveRepository: ObjectiveRepository,
    private val userContextProvider: UserContextProvider
) {
    
    fun canAccessObjective(objectiveId: UUID): Boolean {
        val userId = userContextProvider.getCurrentUserId()
        return objectiveRepository.findById(objectiveId)
            .map { it.userId == userId }
            .orElse(false)
    }
}
```

---

## 7. Cross-cutting Concerns

### 7.1 Caching Strategy per Use Case

```kotlin
@Component
@Transactional(readOnly = true)
class GetObjectivesByUserUseCase(
    private val objectiveRepository: ObjectiveRepository,
    private val keyResultRepository: KeyResultRepository,
    private val userContextProvider: UserContextProvider
) {
    
    @Cacheable(
        value = ["userObjectives"], 
        key = "#root.target.userContextProvider.getCurrentUserId()",
        condition = "#result.size() <= 50" // Only cache if reasonable size
    )
    fun execute(): List<ObjectiveResponse> {
        // Implementation
    }
}

@Component
@Transactional
class CreateObjectiveUseCase(
    private val objectiveRepository: ObjectiveRepository,
    private val userContextProvider: UserContextProvider
) {
    
    @CacheEvict(
        value = ["userObjectives"], 
        key = "#root.target.userContextProvider.getCurrentUserId()"
    )
    fun execute(request: CreateObjectiveRequest): ObjectiveResponse {
        // Implementation
    }
}
```

### 7.2 Validation in Use Cases

```kotlin
@Component
@Transactional
class CreateObjectiveUseCase(
    private val objectiveRepository: ObjectiveRepository,
    private val userContextProvider: UserContextProvider
) {
    
    fun execute(request: CreateObjectiveRequest): ObjectiveResponse {
        val userId = userContextProvider.getCurrentUserId()
        
        // 1. Business validation
        validateObjectiveLimit(userId)
        validateDateRange(request.startDate, request.endDate)
        validateCycleType(request.cycleType, request.startDate, request.endDate)
        
        // 2. Create objective
        val entity = ObjectiveEntity(
            userId = userId,
            title = request.title,
            description = request.description,
            cycleType = request.cycleType,
            status = ObjectiveStatus.ACTIVE,
            startDate = request.startDate,
            endDate = request.endDate
        )
        
        val saved = objectiveRepository.save(entity)
        return saved.toResponse()
    }
    
    private fun validateObjectiveLimit(userId: String) {
        val activeCount = objectiveRepository.countActiveObjectivesByUser(userId)
        if (activeCount >= 5) {
            throw ValidationException("objectives", "Cannot have more than 5 active objectives")
        }
    }
    
    private fun validateDateRange(startDate: LocalDate, endDate: LocalDate) {
        if (endDate.isBefore(startDate)) {
            throw ValidationException("endDate", "End date must be after start date")
        }
        
        if (startDate.isBefore(LocalDate.now())) {
            throw ValidationException("startDate", "Start date cannot be in the past")
        }
    }
    
    private fun validateCycleType(cycleType: CycleType, startDate: LocalDate, endDate: LocalDate) {
        val duration = ChronoUnit.DAYS.between(startDate, endDate)
        
        when (cycleType) {
            CycleType.MONTHLY -> {
                if (duration > 45) {
                    throw ValidationException("cycleType", "Monthly objectives should not exceed 45 days")
                }
            }
            CycleType.QUARTERLY -> {
                if (duration > 100) {
                    throw ValidationException("cycleType", "Quarterly objectives should not exceed 100 days")
                }
            }
            CycleType.YEARLY -> {
                if (duration > 400) {
                    throw ValidationException("cycleType", "Yearly objectives should not exceed 400 days")
                }
            }
        }
    }
}
```

---

## 8. Technology Stack

### 8.1 Dependencies

```kotlin
// build.gradle.kts
dependencies {
    // Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-resource-server")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    // Database
    implementation("org.postgresql:postgresql")
    implementation("org.liquibase:liquibase-core")

    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")

    // Utility
    implementation("org.hibernate.validator:hibernate-validator")
    
    // Documentation
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")
    
    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
}
```

---

## 9. Development Guidelines

### 9.1 Use Case Development Rules

1. **Single Responsibility**: Each use case does exactly one thing
2. **No Dependencies Between Use Cases**: Use cases can call other use cases but should minimize coupling
3. **Validation First**: Always validate inputs at the beginning
4. **Transaction Boundary**: Each use case defines its own transaction
5. **Caching Strategy**: Apply caching at use case level, not repository level

### 9.2 Testing Strategy

#### 9.2.1 Use Case Unit Tests

```kotlin
@ExtendWith(MockitoExtension::class)
class CreateObjectiveUseCaseTest {
    
    @Mock
    private lateinit var objectiveRepository: ObjectiveRepository
    
    @Mock
    private lateinit var userContextProvider: UserContextProvider
    
    @InjectMocks
    private lateinit var createObjectiveUseCase: CreateObjectiveUseCase
    
    @Test
    fun `should create objective successfully`() {
        // Given
        val userId = "test-user-id"
        val request = CreateObjectiveRequest(
            title = "Test Objective",
            description = "Test Description",
            cycleType = CycleType.QUARTERLY,
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusMonths(3)
        )
        
        whenever(userContextProvider.getCurrentUserId()).thenReturn(userId)
        whenever(objectiveRepository.countActiveObjectivesByUser(userId)).thenReturn(2L)
        whenever(objectiveRepository.save(any<ObjectiveEntity>())).thenAnswer { 
            (it.arguments[0] as ObjectiveEntity).copy(id = UUID.randomUUID()) 
        }
        
        // When
        val result = createObjectiveUseCase.execute(request)
        
        // Then
        assertThat(result.title).isEqualTo(request.title)
        assertThat(result.cycleType).isEqualTo(request.cycleType)
        verify(objectiveRepository).save(any<ObjectiveEntity>())
    }
    
    @Test
    fun `should throw validation exception when objective limit exceeded`() {
        // Given
        val userId = "test-user-id"
        val request = CreateObjectiveRequest(
            title = "Test Objective",
            description = "Test Description",
            cycleType = CycleType.QUARTERLY,
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusMonths(3)
        )
        
        whenever(userContextProvider.getCurrentUserId()).thenReturn(userId)
        whenever(objectiveRepository.countActiveObjectivesByUser(userId)).thenReturn(5L)
        
        // When & Then
        assertThrows<ValidationException> {
            createObjectiveUseCase.execute(request)
        }
        
        verify(objectiveRepository, never()).save(any<ObjectiveEntity>())
    }
}
```

#### 9.2.2 Integration Tests

```kotlin
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ObjectiveUseCaseIntegrationTest {
    
    @Container
    companion object {
        @JvmStatic
        val postgres = PostgreSQLContainer<Nothing>("postgres:15").apply {
            withDatabaseName("test_okr")
            withUsername("test")
            withPassword("test")
        }
    }
    
    @Autowired
    private lateinit var createObjectiveUseCase: CreateObjectiveUseCase
    
    @Autowired
    private lateinit var getObjectivesByUserUseCase: GetObjectivesByUserUseCase
    
    @Autowired
    private lateinit var userContextProvider: UserContextProvider
    
    @Test
    @WithMockUser(username = "test-user-id")
    fun `should create and retrieve objective`() {
        // Given
        val request = CreateObjectiveRequest(
            title = "Integration Test Objective",
            description = "Test Description",
            cycleType = CycleType.QUARTERLY,
            startDate = LocalDate.now(),
            endDate = LocalDate.now().plusMonths(3)
        )
        
        // When
        val created = createObjectiveUseCase.execute(request)
        val retrieved = getObjectivesByUserUseCase.execute()
        
        // Then
        assertThat(created.title).isEqualTo(request.title)
        assertThat(retrieved).hasSize(1)
        assertThat(retrieved[0].id).isEqualTo(created.id)
    }
}
```

---

## 10. Migration Strategy

### 10.1 Future Microservices Migration

Each module can easily become a separate microservice:

```kotlin
// Phase 1: Extract OKR Service
@RestController
@RequestMapping("/api/v1/objectives")
class ObjectiveController(
    // Use cases remain the same
    private val createObjectiveUseCase: CreateObjectiveUseCase,
    private val getObjectivesByUserUseCase: GetObjectivesByUserUseCase
    // ... other use cases
) {
    // Controllers remain unchanged
}

// Phase 2: Add message-based communication between services
@Component
class CreateObjectiveUseCase(
    private val objectiveRepository: ObjectiveRepository,
    private val userContextProvider: UserContextProvider,
    private val eventPublisher: ApplicationEventPublisher // Add this
) {
    
    fun execute(request: CreateObjectiveRequest): ObjectiveResponse {
        // ... existing logic
        
        val saved = objectiveRepository.save(entity)
        
        // Publish event for other services
        eventPublisher.publishEvent(
            ObjectiveCreatedEvent(saved.id!!, saved.userId)
        )
        
        return saved.toResponse()
    }
}
```

---

## Conclusion

This **Use Case-Driven Modular Monolith** architecture provides:

- **Clean Architecture**: Single responsibility per use case
- **High Testability**: One use case = one focused test class
- **Maintainability**: Clear business logic separation
- **Performance**: No JPA relations, optimized queries, strategic caching
- **Scalability**: Easy migration to microservices
- **Security**: Authorization at use case level  
- **Flexibility**: Easy to add new use cases without affecting existing ones

Each use case encapsulates a single business operation, making the codebase easier to understand, test, and maintain while preparing for future architectural evolution.