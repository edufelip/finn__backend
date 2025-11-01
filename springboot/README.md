# Finn Spring Boot/Kotlin (Migration Scaffold)

This directory contains a Spring Boot + Kotlin scaffold that mirrors the current Node/Express API. It is the starting point for the migration described in MIGRATION.md.

How to run (once Java/Gradle are available):
- Build: `./gradlew build`
- Default dev (cloud DB direct IP): `SPRING_PROFILES_ACTIVE=default ./gradlew bootRun` (exposes `http://localhost:8080`)
- Local Postgres: `SPRING_PROFILES_ACTIVE=local-db ./gradlew bootRun`
- Production DB: `SPRING_PROFILES_ACTIVE=prod ./gradlew bootRun`
- In-memory sandbox: `SPRING_PROFILES_ACTIVE=local ./gradlew bootRun`
- Or use the helper script: `./run-local.sh` (auto-sets `SPRING_PROFILES_ACTIVE=default` and loads `.env` if present)

Environment & DB:
- Uses the same env names via Spring placeholders: `DB_HOST`, `DB_PORT`, `DB_NAME`, `DEVDB_NAME`, `DB_USER`, `DB_PASSWORD`.
- Copy `.env.example` to `.env` and fill in the Google Cloud connection details (host `***REMOVED***`, port `5432`, appropriate credentials).
- Additional optional sets: `LOCAL_DB_*` (local instance) and `PROD_DB_*` (production cloud shadow).
- Update `src/main/resources/application.yml` as needed.

Status:
- Entities, repositories, DTOs, controllers stubbed.
- Services are interfaces; implementations and security/tests/docs pending.
