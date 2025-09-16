# Repository Guidelines

## Project Structure & Module Organization
- `src/`: application code. Entry points: `src/server.ts`, `src/index.ts`.
  - `config/` (e.g., `multer.ts`), `infra/` (DB), `routes/`, `service/`, `data/`, `models/`, `interfaces/`, `test/` (Jest specs and assets).
- `database/create.sql`: bootstrap schema for local/dev databases.
- `public/`: runtime upload target (create locally if missing).

## Build, Test, and Development Commands
- `yarn` — install dependencies.
- `yarn dev` — run API locally via `ts-node-dev` on `src/server.ts` (default http://localhost:3333).
- `yarn test` — run Jest (TypeScript preset). Uses `jestSetup.js` to set `TEST_MODE=true` and connect to `DEVDB_NAME`.
- `yarn build` — transpile to `dist/` with Babel. Run with `node dist/server.js`.

## Coding Style & Naming Conventions
- Language: TypeScript. Indentation: 2 spaces. Imports prefer path aliases (`@models/*`, `@service/*`, `@data/*`, `@interfaces/*`).
- ESLint: Standard config (`.eslintrc.json`); several rules relaxed (e.g., `camelcase`, `no-unused-vars`).
- Filenames: Models use `PascalCase` (e.g., `UserModel.ts`), services/routes use `camelCase`, interfaces are prefixed with `I` (e.g., `IUserMethods.ts`).
- Lint locally with: `npx eslint . --ext .ts`.

## Testing Guidelines
- Framework: Jest (`ts-jest`) + `supertest`. Tests live in `src/test` and follow `*.spec.ts`.
- DB: Tests toggle `TEST_MODE=true` and will target `process.env.DEVDB_NAME`. Ensure this database exists and credentials are set in `.env`.
- Schema: Tests often create/drop tables; for fresh setups, apply `database/create.sql` to your dev DBs.
- Coverage outputs to `coverage/`. Keep new/changed code covered with meaningful assertions.

## Commit & Pull Request Guidelines
- Commits: concise, imperative present (e.g., "add user lookup", "fix trending query"). Group related changes; avoid noisy mix-ins.
- PRs must include:
  - Clear description, rationale, and testing notes (cURL/Postman examples for new/changed endpoints).
  - Linked issue (if any) and screenshots for API responses where useful.
  - Updated tests and docs when routes, schema, or behavior change.
  - Passing CI (lint/tests) and no TypeScript errors.

## Security & Configuration Tips
- Copy `.env.example` to `.env` and fill `DB_*` and `DEVDB_NAME`. Never commit secrets.
- File uploads: `multer` stores to `./public/`, enforces `.png` and ~300KB limit.
- Use a separate dev/test database to avoid data collisions.

