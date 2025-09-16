# Node.js/Express/TypeScript to Spring Boot/Kotlin Migration

## Overview
Migrate a complete Node.js/Express/TypeScript backend application to a modern Spring Boot application using Kotlin, following clean architecture principles and modern Java ecosystem best practices.

## Core Requirements

### 1. Language & Framework Migration
- Convert all TypeScript code to Kotlin
- Replace Express.js with Spring Boot framework
- Apply Kotlin best practices and idioms
- Use data classes and sealed classes where appropriate
- Implement proper null safety
- Use Kotlin coroutines for asynchronous operations

### 2. Framework & Dependency Migration

#### From Node.js/Express to Spring Boot:
- **Web Framework**: Express.js → Spring Boot WebMVC/WebFlux
- **HTTP Client**: Axios/Fetch → WebClient or OkHttp
- **Validation**: Joi/Yup → Bean Validation (JSR-303) with Hibernate Validator
- **Database ORM**: Prisma/TypeORM/Mongoose → Spring Data JPA or R2DBC
- **Authentication**: Passport.js → Spring Security
- **Configuration**: dotenv → Spring Boot Configuration Properties
- **Logging**: Winston → Logback with SLF4J
- **Testing**: Jest → JUnit 5 + MockK + TestContainers
- **Documentation**: Swagger/OpenAPI → SpringDoc OpenAPI

#### Dependencies to Add:
- Spring Boot Starter Web/WebFlux
- Spring Boot Starter Data JPA/R2DBC
- Spring Boot Starter Security
- Spring Boot Starter Validation
- Spring Boot Starter Actuator
- Kotlin Jackson Module
- Kotlin Coroutines
- SpringDoc OpenAPI
- TestContainers
- MockK

### 3. Database Migration
- Convert database schemas and migrations
- Replace Node.js ORM queries with Spring Data repositories
- Implement proper transaction management
- Convert database connection configuration
- Set up connection pooling (HikariCP)

### 4. Architecture Implementation

#### Project Structure:
```
src/main/kotlin/
├── config/             # Configuration classes
├── controller/         # REST controllers (Presentation layer)
├── service/           # Business logic (Domain layer)
├── repository/        # Data access (Data layer)
├── entity/            # Database entities
├── dto/               # Data transfer objects
├── exception/         # Custom exceptions and handlers
├── security/          # Security configuration
└── util/              # Utility classes

src/main/resources/
├── application.yml    # Configuration
├── db/migration/      # Database migrations (Flyway/Liquibase)
└── static/           # Static resources
```

#### Layer Responsibilities:

**Controller Layer (Presentation):**
- REST endpoints
- Request/response handling
- Input validation
- HTTP status management

**Service Layer (Business Logic):**
- Business rules implementation
- Transaction management
- Domain logic coordination

**Repository Layer (Data Access):**
- Database operations
- Query implementation
- Data mapping

**Entity Layer:**
- JPA/R2DBC entities
- Database table mapping
- Relationships definition

### 5. API Migration Strategy

#### REST API Conversion:
```kotlin
// From Express route:
// app.get('/api/users/:id', async (req, res) => { ... })

// To Spring Boot controller:
@RestController
@RequestMapping("/api/users")
class UserController(private val userService: UserService) {
    
    @GetMapping("/{id}")
    suspend fun getUser(@PathVariable id: Long): ResponseEntity<UserDto> {
        return ResponseEntity.ok(userService.getUser(id))
    }
}
```

#### Middleware to Spring Interceptors/Filters:
- Convert Express middleware to Spring interceptors
- Implement CORS configuration
- Set up request/response logging
- Convert authentication middleware to Spring Security

### 6. Asynchronous Programming Migration
- Replace Node.js async/await with Kotlin coroutines
- Convert Promise-based code to suspend functions
- Implement proper error handling with coroutines
- Use Flow for reactive streams if needed

### 7. Configuration Management
- Replace environment variables with Spring Boot properties
- Implement Configuration Properties classes
- Set up profiles (dev, test, prod)
- Configure externalized configuration

## Technical Specifications

### Database Configuration
```kotlin
// Replace database connection strings
@Configuration
@EnableJpaRepositories
class DatabaseConfig {
    
    @Bean
    @ConfigurationProperties("spring.datasource.hikari")
    fun dataSource(): HikariDataSource {
        return HikariDataSource()
    }
}
```

### Security Migration
- Convert Passport.js authentication to Spring Security
- Implement JWT token handling
- Set up OAuth2 if needed
- Configure CORS and CSRF protection
- Implement role-based access control

### Error Handling
- Replace Express error handlers with @ControllerAdvice
- Implement global exception handling
- Create custom exception classes
- Proper HTTP status code mapping

### Validation
- Replace request validation middleware with Bean Validation
- Use annotations (@Valid, @NotNull, @Size, etc.)
- Custom validators where needed
- Proper error message handling

### Testing Migration
- Convert Jest tests to JUnit 5
- Replace mocking libraries with MockK
- Set up TestContainers for integration tests
- Implement proper test slices (@WebMvcTest, @DataJpaTest)

### Caching
- Replace Redis/memory caching with Spring Cache
- Configure cache providers (Redis, Caffeine)
- Implement cache strategies

### Monitoring & Observability
- Set up Spring Boot Actuator
- Implement health checks
- Configure metrics and monitoring
- Add distributed tracing if needed

## Build & Deployment

### Build System
- Set up Gradle with Kotlin DSL
- Configure multi-stage Docker builds
- Implement proper JAR packaging
- Set up build profiles

### CI/CD Migration
- Update pipeline configurations
- Modify deployment scripts
- Configure environment-specific properties
- Set up database migration scripts

### Docker Configuration
```dockerfile
FROM openjdk:17-jdk-slim
COPY build/libs/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## Migration Strategy

### Phase 1: Core Framework Setup
1. Initialize Spring Boot project
2. Set up basic project structure
3. Configure database connections
4. Implement basic health endpoints

### Phase 2: API Migration
1. Convert REST endpoints one by one
2. Implement authentication/authorization
3. Add validation and error handling
4. Set up API documentation

### Phase 3: Business Logic Migration
1. Convert service layer logic
2. Implement transaction management
3. Add caching where appropriate
4. Optimize database queries

### Phase 4: Testing & Quality
1. Convert test suites
2. Set up integration tests
3. Add performance testing
4. Implement monitoring

## Deliverables Expected

1. Complete Spring Boot Kotlin application
2. Migrated database schema and data
3. All API endpoints converted and tested
4. Authentication and authorization working
5. Comprehensive test suite
6. Docker configuration and deployment scripts
7. API documentation (OpenAPI/Swagger)
8. Performance benchmarks comparing old vs new
9. Migration guide and documentation
10. Monitoring and health check setup

## Validation Criteria

- [ ] All TypeScript code converted to Kotlin
- [ ] All Express routes converted to Spring Boot controllers
- [ ] Database successfully migrated with all data
- [ ] Authentication/authorization working correctly
- [ ] All tests passing (unit and integration)
- [ ] API documentation up to date
- [ ] Performance meets or exceeds Node.js version
- [ ] Security vulnerabilities addressed
- [ ] Monitoring and logging functional
- [ ] Docker builds and deployments working
- [ ] CI/CD pipeline updated and functional
- [ ] Error handling properly implemented
- [ ] Configuration externalized correctly

## Performance Considerations

### JVM Optimization
- Configure JVM heap sizes
- Set up garbage collection tuning
- Implement connection pooling
- Optimize database queries

### Spring Boot Optimization
- Use WebFlux for high-concurrency needs
- Implement proper caching strategies
- Configure async processing
- Optimize startup time

### Monitoring
- Set up APM tools (New Relic, AppDynamics, etc.)
- Implement custom metrics
- Configure alerting
- Performance profiling setup

## Security Enhancements

### Spring Security Features
- Implement proper CSRF protection
- Configure secure headers
- Set up rate limiting
- Implement audit logging

### Data Protection
- Configure encryption at rest
- Implement secure communication (TLS)
- Set up secrets management
- Configure database security

## Notes for Implementation

- **Maintain API Compatibility**: Ensure all endpoints work exactly as expected by the Finn Android app
- **Preserve Database Schema**: Keep existing PostgreSQL tables, indexes, and data intact
- **Port Consistency**: Update any hardcoded references from port 3333 to 8080
- **Environment Variables**: Maintain support for same .env variable names during transition
- **Testing Strategy**: Run both Node.js and Spring Boot versions in parallel during migration
- **Android App Testing**: Test all API endpoints with the actual Finn Android app
- **Performance Monitoring**: Compare response times and resource usage
- **Rollback Plan**: Keep Node.js version deployable as backup
- **Documentation**: Update any API documentation to reflect new base URL and port
- **Development Workflow**: Ensure `yarn dev` equivalent with `./gradlew bootRun`
- **Database Migrations**: Plan for zero-downtime database migration if schema changes needed

## Post-Migration Tasks

1. Performance tuning and optimization
2. Security audit and penetration testing  
3. Load testing with production-like data
4. Documentation updates
5. Team training on new stack
6. Monitoring dashboard setup
7. Incident response plan updates
8. Backup and disaster recovery testing