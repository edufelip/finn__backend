# Finn Spring Boot/Kotlin (Migration Scaffold)

This directory contains a Spring Boot + Kotlin scaffold that mirrors the current Node/Express API. It is the starting point for the migration described in MIGRATION.md.

How to run (once Java/Gradle are available):
- Build: `./gradlew build`
- Run: `./gradlew bootRun` (exposes `http://localhost:8080`)

Environment & DB:
- Uses the same env names via Spring placeholders: `DB_HOST`, `DB_PORT`, `DB_NAME`, `DEVDB_NAME`, `DB_USER`, `DB_PASSWORD`.
- Update `src/main/resources/application.yml` as needed.

Status:
- Entities, repositories, DTOs, controllers stubbed.
- Services are interfaces; implementations and security/tests/docs pending.

