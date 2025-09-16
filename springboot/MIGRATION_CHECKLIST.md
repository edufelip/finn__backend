# Migration Checklist (from MIGRATION.md)

- [x] Initialize Spring Boot/Kotlin project scaffold
- [x] Project structure (config, controller, service, repository, entity, dto)
- [x] Database entities mirroring current PostgreSQL schema
- [x] Repositories for primary entities
- [x] Controllers mapping Express routes to Spring endpoints
- [x] Configuration with env var parity (`DB_*`, `DEVDB_NAME`) and profiles
- [x] Actuator basic endpoints
 - [x] Service implementations and business logic
 - [x] Validation annotations and error handling (@ControllerAdvice)
 - [x] Authentication via Firebase ID token (Spring Security filter)
 - [x] OpenAPI docs via SpringDoc
 - [x] Test suite (JUnit5 + MockK + Testcontainers)
 - [ ] Dockerfile and Gradle wrapper
 - [ ] CI pipeline changes
- [ ] Performance tuning/monitoring

Notes:
- Base port changed to `8080`.
- File uploads will require a storage strategy (multer parity) — to be implemented.
- Ensure API compatibility with the Finn Android app.
