#!/usr/bin/env bash
set -euo pipefail

# start.sh for Railpack detection in monorepo
# Usage: set SERVICE=frontend | portfolio-manager or rely on PORT defaults

SERVICE=${SERVICE:-}
PORT=${PORT:-}

echo "start.sh: SERVICE=${SERVICE:-<none>} PORT=${PORT:-<none>}"

if [ -n "$SERVICE" ]; then
  if [ "$SERVICE" = "frontend" ]; then
    echo "Starting frontend..."
    cd frontend
    npm ci
    npm run build
    npx serve -s dist -l ${PORT:-5173}
  elif [ "$SERVICE" = "portfolio-manager" ] || [ "$SERVICE" = "backend" ]; then
    echo "Starting portfolio-manager (backend)..."
    cd portfolio-manager
    mvn -DskipTests package
    java -Dserver.port=${PORT:-8080} -jar target/portfolio-manager.jar
  else
    echo "Unknown SERVICE: $SERVICE"
    exit 1
  fi
else
  # Infer service from PORT, otherwise default to frontend
  if [ "${PORT:-}" = "5173" ]; then
    echo "No SERVICE set but PORT=5173 — starting frontend"
    cd frontend
    npm ci
    npm run build
    npx serve -s dist -l ${PORT}
  elif [ "${PORT:-}" = "8080" ]; then
    echo "No SERVICE set but PORT=8080 — starting backend"
    cd portfolio-manager
    mvn -DskipTests package
    java -Dserver.port=${PORT} -jar target/portfolio-manager.jar
  else
    echo "No SERVICE or PORT detected — defaulting to frontend"
    cd frontend
    npm ci
    npm run build
    npx serve -s dist -l ${PORT:-5173}
  fi
fi
