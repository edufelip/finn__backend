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

-Environment
- DB vars: `DB_HOST`, `DB_PORT`, `DB_NAME`, `DEVDB_NAME`, `DB_USER`, `DB_PASSWORD`
- Copy `springboot/.env.example` to `springboot/.env` and populate it with the remote development database connection (host value supplied via your secret store or `.env`), port `5432`. The committed `.env.example` is a skeleton only.
- Update your local SSH config with the host you deploy to:
  ```
  Host finn-backend
    HostName <vm-ip-address>
    User <your_vm_user>
    IdentityFile ~/.ssh/<your_key>
  ```
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
./run-local.sh   # defaults to SPRING_PROFILES_ACTIVE=default (cloud dev via direct IP)

# Option 2: run directly (export env in your shell first)
SPRING_PROFILES_ACTIVE=default ./gradlew bootRun   # cloud dev DB (finn_dev using remote Postgres)
SPRING_PROFILES_ACTIVE=local-db ./gradlew bootRun  # local Postgres (LOCAL_DB_* env vars)
SPRING_PROFILES_ACTIVE=prod ./gradlew bootRun      # cloud production DB (finn_prod)
SPRING_PROFILES_ACTIVE=local ./gradlew bootRun     # in-memory H2 sandbox
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

## Infrastructure & Operations
- **Runtime host**: Linux VM (Ubuntu 22.04) provisioned on your infrastructure provider (GCP, AWS, Azure, etc.). Configure a local SSH alias (e.g., `Host finn-backend`) pointing to the active public IP.
- **Process manager**: PM2 runs via systemd unit `pm2-finnbackend.service` (enabled). Commands:
  - Check status: `sudo systemctl status pm2-finnbackend`
  - View process list: `sudo -u finnbackend pm2 status`
  - Reload after deploy: `sudo -u finnbackend pm2 restart finn-backend --update-env` then `sudo -u finnbackend pm2 save`
- **App bootstrap**: `/opt/finn-backend/run.sh` pulls secrets, loads env, then launches the jar at `/opt/finn-backend/repo/springboot/build/libs/finn-backend-kotlin-0.1.0.jar`.
- **Secrets**: Runtime variables are sourced from a cloud secret manager into `/opt/finn-backend/.env.generated` by `/opt/finn-backend/scripts/load_env_from_secrets.sh`. After rotating a secret (e.g., with `gcloud secrets versions add`, `aws secretsmanager put-secret-value`, etc.), rerun the loader and restart PM2 so the app receives the new values. The fallback `.env` stores only non-sensitive overrides.
- **Logs**: PM2 streams under `/var/log/finn-backend/pm2-out.log` and `/var/log/finn-backend/pm2-error.log`. Use `sudo tail -f` or `sudo -u finnbackend pm2 logs finn-backend --lines 100`.
- **Database (Docker)**: Postgres 15 runs in a Docker container named `finn-postgres` on the same VM. Management commands:
  - Status: `sudo docker ps` (look for `postgres:15` image / `finn-postgres` name)
  - Logs: `sudo docker logs --tail 100 finn-postgres`
  - Exec shell: `sudo docker exec -it finn-postgres psql -U <db_user> -d <db_name>`
  Port 5432 is published externally (IPv4/IPv6). UFW currently allows ports 22, 80, 443, and 5432—restrict sources before exposing to the public Internet.
- **PM2 & Docker interplay**: PM2’s systemd unit starts before Docker; ensure the database container uses a restart policy such as `--restart unless-stopped` so it becomes available before Spring Boot initialises. If you run PM2 inside Docker instead, mirror the steps in a compose/service definition.
- **Monitoring & alerts**: Uptime Kuma (Docker) listens at `127.0.0.1:3001`; forward with `ssh -L 3001:127.0.0.1:3001 googlecloud` to manage monitors (Telegram notifications configured).
- **Health checks**: `/healthz` is protected by App Check. For manual probes, include the `X-Firebase-AppCheck` header or temporarily toggle `SECURITY_REQUIRE_APPCHECK=false` in the environment.
- **Deploy refresher**:
  1. `ssh googlecloud`
  2. `sudo -u finnbackend bash -lc 'cd /opt/finn-backend/repo/springboot && ./gradlew bootJar'`
  3. `sudo -u finnbackend pm2 restart finn-backend --update-env`
  4. `sudo -u finnbackend pm2 save`

## Maintainers
This project is mantained by:
* [Eduardo Felipe](http://github.com/edufelip)

## Contributing

1. Fork it
2. Create your feature branch (git checkout -b my-new-feature)
3. Commit your changes (git commit -m 'Add some feature')
4. Push your branch (git push origin my-new-feature)
5. Create a new Pull Request
