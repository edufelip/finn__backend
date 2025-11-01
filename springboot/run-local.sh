#!/usr/bin/env bash
set -euo pipefail

# Run Spring Boot locally, loading vars from springboot/.env if present
cd "$(dirname "$0")"

if [ -f .env ]; then
  echo "Loading environment from .env"
  set -a
  # shellcheck disable=SC1091
  source .env
  set +a
else
  echo "No .env file found; using current shell environment" >&2
fi

export SPRING_PROFILES_ACTIVE="${SPRING_PROFILES_ACTIVE:-default}"

CMD="./gradlew"
if [ ! -x "$CMD" ]; then
  CMD="gradle"
fi

exec "$CMD" bootRun
