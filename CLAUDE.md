# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Development Commands

### Build and Run
- **Build project**: `./gradlew build`
- **Run application**: `./gradlew bootRun`
- **Run tests**: `./gradlew test`
- **Run single test**: `./gradlew test --tests "com.motycka.edu.game.SomeTest"`
- **Clean build**: `./gradlew clean build`

The application runs on port 8090 by default. Access the UI at `http://localhost:8090/`.

### Testing Framework
- Uses JUnit 5 (JUnit Platform)
- Uses MockK for mocking instead of Mockito
- Uses SpringMockK for Spring Boot testing integration
- Test configuration excludes standard Mockito and JUnit 4

## Architecture Overview

### Layered Architecture
The application follows a standard layered architecture pattern:
- **Controller Layer**: REST API endpoints (`*Controller.kt`)
- **Service Layer**: Business logic (`*Service.kt`)
- **Repository Layer**: Data access (`*Repository.kt`)
- **Model Layer**: Domain entities and DTOs

### Package Structure
```
com.motycka.edu.game/
├── account/           # User management
├── character/         # Character management with class-based inheritance
├── leaderboard/       # Ranking and statistics
├── match/            # Battle system
├── config/           # Spring configuration and security
└── error/            # Exception handling
```

### Character System
The character system uses inheritance with abstract base class:
- **Character** (abstract): Base class with common attributes
- **Warrior**: Stamina and defense-focused character
- **Sorcerer**: Mana and healing-focused character
- Implements strategy pattern through interfaces (Recoverable, Defender, Healer)

### Security
- Uses Basic Authentication
- Spring Security configuration in `SecurityConfiguration.kt`
- User registration endpoint (`POST /api/accounts`) is public
- All other endpoints require authentication
- Password encoding with BCrypt

### Database
- H2 in-memory database (can be switched to PostgreSQL)
- Schema defined in `src/main/resources/schema.sql`
- Initial data in `src/main/resources/data.sql`
- JPA repositories with Spring Data

### REST API Structure
- Account management: `/api/accounts`
- Character CRUD: `/api/characters`
- Match simulation: `/api/matches`
- Leaderboards: `/api/leaderboards`

Each domain follows consistent patterns:
- Request/Response DTOs in `rest/` subpackages
- Model mappers for DTO transformation
- Validation and error handling through global exception handler

### Key Design Patterns
- **Repository Pattern**: Data access abstraction
- **DTO Pattern**: Separate request/response objects from domain models
- **Strategy Pattern**: Character abilities through interfaces
- **Template Method**: Character base class with abstract methods
- **Dependency Injection**: Spring-managed components

### Testing Strategy
- Unit tests for services and repositories
- Integration tests for controllers with `@SpringBootTest`
- Test fixtures in dedicated `*Fixtures.kt` files
- Security context helper for authenticated tests
- Separate test security configuration