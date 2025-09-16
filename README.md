<h1 align="start">Finn Server</h1>

<p align="start">
  <a href="https://medium.com/@eduardofelipi"><img alt="Medium" src="https://img.shields.io/static/v1?label=Medium&message=@edu_santos&color=gray&logo=medium"/></a>
  <a href="https://www.youtube.com/channel/UCYcwwX7nDU_U0FP-TsXMwVg"><img alt="Profile" src="https://img.shields.io/static/v1?label=Youtube&message=edu_santos&color=red&logo=youtube"/></a> 
  <a href="https://github.com/edufelip"><img alt="Profile" src="https://img.shields.io/static/v1?label=Github&message=edufelip&color=white&logo=github"/></a> 
  <a href="https://www.linkedin.com/in/eduardo-felipe-dev/"><img alt="Linkedin" src="https://img.shields.io/static/v1?label=Linkedin&message=edu_santos&color=blue&logo=linkedin"/></a> 
  <a href="http://localhost:8080/swagger-ui/index.html"><img alt="Swagger UI" src="https://img.shields.io/badge/docs-Swagger%20UI-brightgreen?logo=swagger"/></a>
  <a href="http://localhost:8080/v3/api-docs"><img alt="OpenAPI" src="https://img.shields.io/badge/OpenAPI-JSON-blue?logo=openapiinitiative"/></a>
</p>

<p align="start">  
  This is a RESTful API for the Android App Finn
</p>

## Prerequisites

- Java 17+
- Gradle 8+ (or use `./gradlew` if present)

## Clone

Clone the repository from GitHub.

```
$ git clone https://github.com/edufelip/finn__backend.git
```

## Environment Variables

For the API to work properly the following values are required (read by Spring Boot from the environment):

| Variable Name                     | Description                    |
|-----------------------------------|--------------------------------|
| DB_NAME                   | Name of the PostgreSQL database the api is going to use |
| DEVDB_NAME                  | Name of the PostgreSQL database the api is going to use to run the tests |
| DB_HOST                | Database of the address (Use localhost if you're running the db in your machine) |
| DB_PORT              | The port your db is using. Since this api requires a postgreSQL db the value is supposed to be 5432 |
| DB_USER                | User of your postgreSQL database |
| DB_PASSWORD                | User's password |

## Spring Boot API (Kotlin)
Location: `springboot/`

Environment
- DB vars: `DB_HOST`, `DB_PORT`, `DB_NAME`, `DEVDB_NAME`, `DB_USER`, `DB_PASSWORD`
- Firebase Admin: set either `FIREBASE_SERVICE_ACCOUNT` (raw or base64 JSON) or `GOOGLE_APPLICATION_CREDENTIALS=/path/to/serviceAccount.json`
- Security flags (application.yml):
  - `security.requireAppHeader` (default true)
  - `security.requireAppCheck` (default true)
  - `security.disableAuth` (false; set true for local testing)
  - Example file: see `springboot/.env.example` (copy to `springboot/.env` for local use; do not commit secrets)

Run
```
cd springboot
# Option 1: load env from springboot/.env automatically
./run-local.sh

# Option 2: run directly (export env in your shell first)
gradle bootRun   # or ./gradlew bootRun
```
API runs at http://localhost:8080. Swagger UI: http://localhost:8080/swagger-ui/index.html

Tests
```
cd springboot
# Option 1: load env from springboot/.env automatically
./run-tests.sh   # uses Testcontainers + Postgres

# Option 2: run directly (export env in your shell first)
gradle test
```

Security Model
- Every request must come from the mobile app: `X-App-Package: com.edufelip.finn` (or OkHttp UA)
- Firebase App Check: header `X-Firebase-AppCheck: <token>`
- Firebase Auth: `Authorization: Bearer <Firebase ID token>`
- For local testing without mobile/Firebase, set profile `test` or `security.disableAuth=true`.

Uploads
- Multipart endpoints accept .png up to ~300KB:
  - POST `/communities` with parts `community` (JSON) + `community` (file)
  - PUT `/communities/{id}/image` with part `community` (file)
  - POST `/posts` with parts `post` (JSON) + optional `post` (file)
- Files saved to `public/` with random names.

API Docs (Swagger)
- Interactive OpenAPI docs for exploring endpoints and payloads at `/swagger-ui/index.html`. In production, access is still subject to security filters.

## Architecture Overview
- Controllers: HTTP endpoints only; validate inputs (`@Valid`) and delegate to services.
- Services: business logic and transactions (`@Transactional`), expose DTOs.
- Repositories: Spring Data JPA repositories for persistence.
- Entities: JPA entities mapped to PostgreSQL tables.
- DTOs & Mappers: DTOs in `springboot/src/main/kotlin/com/finn/dto/` and centralized mappings in `springboot/src/main/kotlin/com/finn/mapper/` (Entity↔DTO).
- Error Handling: `@ControllerAdvice` maps domain errors and validation to HTTP codes.
- Security: Filters enforce mobile-only access (`X-App-Package`), Firebase App Check, and Firebase Auth; simple rate-limit filter.
  - Guards pattern: RequestGuardsFilter composes pluggable guards (AppHeaderGuard, FirebaseAppCheckGuard, FirebaseAuthGuard) for decoupled request validation.
- Transactions & Paging: read-only transactions on queries; DB-level pagination for posts feed/listing.
- File Storage: `FileStorageService` saves uploads under `app.upload.dir` (defaults to `public/`).
  - Strategy pattern: `StorageService` interface with `LocalFileStorageService` implementation.
  - Community image update orchestrated in service (`updateImageWithStorage`) for transactional consistency.

## Architecture Diagram
Mermaid view for quick orientation (rendered in supported viewers):

```mermaid
flowchart TD
    A[Android App] -->|X-App-Package, AppCheck, Firebase ID Token| B[Security Filters]
    subgraph B[Security]
      B1[AppRequestFilter]\n(X-App-Package)
      B2[FirebaseAppCheckFilter]\n(X-Firebase-AppCheck)
      B3[FirebaseAuthFilter]\n(Bearer ID Token)
      B4[RateLimitFilter]
    end
    B --> C[Controllers]
    C --> D[Services]
    D --> E[Repositories]
    E --> F[(PostgreSQL)]
    D --> G[DTOs]
    G <--> H[Mappers]
    C --> I[Global Exception Handler]
```
```
Client (Android)
   |
   v
[Security Filters]
  - AppRequestFilter (X-App-Package)
  - FirebaseAppCheckFilter (X-Firebase-AppCheck)
  - FirebaseAuthFilter (Bearer ID Token)
  - RateLimitFilter
   |
   v
[Controllers] --> [Services] --> [Repositories] --> [Entities <-> DB]
        ^             |
        |             v
      [DTOs] <---- [Mappers]
        |
        v
 [Global Exception Handler]
```

## Code Map (Key Paths)
- Controllers: `springboot/src/main/kotlin/com/finn/controller/`
- Services:
  - Interfaces: `springboot/src/main/kotlin/com/finn/service/Services.kt`
  - Implementations: `springboot/src/main/kotlin/com/finn/service/impl/`
- Repositories: `springboot/src/main/kotlin/com/finn/repository/Repositories.kt`
- Entities: `springboot/src/main/kotlin/com/finn/entity/`
- DTOs: `springboot/src/main/kotlin/com/finn/dto/`
- Mappers: `springboot/src/main/kotlin/com/finn/mapper/Mappers.kt`
- Exception Handling: `springboot/src/main/kotlin/com/finn/exception/`
- Security:
  - `springboot/src/main/kotlin/com/finn/security/AppRequestFilter.kt`
  - `springboot/src/main/kotlin/com/finn/security/FirebaseAppCheckFilter.kt`
  - `springboot/src/main/kotlin/com/finn/security/FirebaseAuthFilter.kt`
  - `springboot/src/main/kotlin/com/finn/security/RateLimitFilter.kt`
  - `springboot/src/main/kotlin/com/finn/security/SecurityConfig.kt`
- File Storage: `springboot/src/main/kotlin/com/finn/service/files/FileStorageService.kt`
- Configuration: `springboot/src/main/resources/application.yml`
- Database Migrations (Flyway): `springboot/src/main/resources/db/migration/`
- Scripts: `springboot/run-local.sh`, `springboot/run-tests.sh`
- Tests:
  - Service integration: `springboot/src/test/kotlin/com/finn/IntegrationTests.kt`
  - API parity (MockMvc): `springboot/src/test/kotlin/com/finn/ApiParityTests.kt`

## Maintainers
This project is mantained by:
* [Eduardo Felipe](http://github.com/edufelip)

## Contributing

1. Fork it
2. Create your feature branch (git checkout -b my-new-feature)
3. Commit your changes (git commit -m 'Add some feature')
4. Push your branch (git push origin my-new-feature)
5. Create a new Pull Request
