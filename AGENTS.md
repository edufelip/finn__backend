# Repository Guidelines

## Project Structure & Module Organization
- Spring Boot + Kotlin application lives under `springboot/`.
- Application sources: `springboot/src/main/kotlin/com/finn/`
  - `config/`, `security/`, `controller/`, `service/`, `repository/`, `entity/`, `dto/`, `mapper/`, `exception/`, `storage/`.
- Shared resources: `springboot/src/main/resources/`
  - `application.yml` holds profile-specific configuration and env placeholders.
  - `db/migration/` contains Flyway SQL migrations.
- Tests: `springboot/src/test/kotlin/com/finn/` (JUnit 5 + MockK + Spring Boot test slices).
- File uploads default to `public/` (relative to repo root). Create the directory locally if missing.
- Legacy Node artifacts were removed; use these Spring Boot modules for any new work.

## Build, Test, and Development Commands
- Install prerequisites: Java 17+, Docker (for Testcontainers), and either Gradle 8+ or the bundled wrapper.
- Preferred local run: `./springboot/run-local.sh` (loads `springboot/.env` if present, defaults `SPRING_PROFILES_ACTIVE=default`, starts `bootRun` on http://localhost:8080 using the cloud dev DB).
- Manual options (from `springboot/`):
  - `SPRING_PROFILES_ACTIVE=default ./gradlew bootRun` → cloud dev database (`finn_dev`)
  - `SPRING_PROFILES_ACTIVE=local-db ./gradlew bootRun` → locally running Postgres (configure `LOCAL_DB_*`)
  - `SPRING_PROFILES_ACTIVE=prod ./gradlew bootRun` → cloud production database (`finn_prod`)
  - `SPRING_PROFILES_ACTIVE=local ./gradlew bootRun` → in-memory H2 sandbox
- Build artifacts: `cd springboot && ./gradlew build` (outputs JAR under `build/libs/`).
- Tests: `./springboot/run-tests.sh` (loads env + runs `./gradlew test` with Testcontainers). You can also call `./gradlew test` directly when env vars are exported.
- Linting/formatting: apply Kotlin conventions (ktlint is not yet wired; follow IntelliJ defaults).

## Coding Style & Naming Conventions
- Language: Kotlin. Indent with 4 spaces; keep one class/declaration per file.
- Package naming: lowercase, feature-based (`com.finn.controller`, `com.finn.service.impl`, etc.).
- Classes/interfaces: suffix according to role (`*Controller`, `*Service`, `*Repository`, `*Entity`, `*Dto`).
- Spring annotations follow constructor-based injection (`@Service class FooService(private val repo: BarRepository)`).
- Prefer data classes for DTOs and entities where appropriate; use sealed types for domain hierarchies.
- Leverage Kotlin null-safety and `Result`/exception handling instead of nullable gymnastics.

## Testing Guidelines
- Primary stack: JUnit 5 + MockK + Spring Boot Test + Testcontainers (PostgreSQL).
- Tests live under `springboot/src/test/kotlin/com/finn/` and mirror the main package structure.
- Scripts (`run-tests.sh`) automatically load `springboot/.env`; ensure `DEVDB_NAME` and DB credentials are set for Testcontainers overrides.
- Integration tests spin up ephemeral Postgres instances; keep assertions deterministic and clean up any storage artifacts.
- For new features, cover controllers (MockMvc), services, and repositories as needed; include regression tests for bug fixes.

## Commit & Pull Request Guidelines
- Commits: concise, imperative present (e.g., "add community image upload flow"). Keep related changes together.
- PR requirements:
  - Explain the change, include rationale, and document testing (curl/Postman examples for API updates).
  - Link issues as applicable and attach screenshots/log snippets when they aid reviewers.
  - Update tests, migrations, and docs when behavior or schema shifts.
  - Ensure CI (build + tests) passes and code compiles with no Kotlin/Gradle warnings.

## Security & Configuration Tips
- Copy `springboot/.env.example` to `springboot/.env` for local development; never commit secrets.
- Required env vars (Spring placeholders): `DB_HOST`, `DB_PORT`, `DB_NAME`, `DEVDB_NAME`, `DB_USER`, `DB_PASSWORD`.
- Firebase Admin credentials: either set `FIREBASE_SERVICE_ACCOUNT` (raw/base64 JSON) or point `GOOGLE_APPLICATION_CREDENTIALS` to a local file path.
- Security toggles (for local-only experiments) live in `.env`: `SECURITY_REQUIRE_APPHEADER`, `SECURITY_REQUIRE_APPCHECK`, `SECURITY_DISABLEAUTH`. Keep them enabled in shared environments.
- File uploads are stored via `LocalFileStorageService` under `public/`; respect size/type constraints enforced in the service layer.
