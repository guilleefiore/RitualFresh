#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
LOG_DIR="$ROOT_DIR/.opencode/logs"
PID_DIR="$ROOT_DIR/.opencode/pids"

mkdir -p "$LOG_DIR" "$PID_DIR"

port_in_use() {
  lsof -ti tcp:"$1" >/dev/null 2>&1
}

echo "Starting RitualFresh development stack..."

docker compose -f "$ROOT_DIR/docker-compose.yml" up -d postgres backend
echo "Backend: starting with Docker Compose. Logs: docker compose logs -f backend"

if port_in_use 5173; then
  echo "Frontend: port 5173 is already in use; leaving existing process running."
else
  (
    cd "$ROOT_DIR/frontend"
    nohup npm run dev -- --host 0.0.0.0 > "$LOG_DIR/frontend.log" 2>&1 &
    echo $! > "$PID_DIR/frontend.pid"
  )
  echo "Frontend: starting with Vite. Logs: .opencode/logs/frontend.log"
fi

echo ""
echo "Services requested:"
docker compose -f "$ROOT_DIR/docker-compose.yml" ps postgres backend
echo ""
echo "Backend:  http://localhost:8080"
echo "Frontend: http://localhost:5173"
echo ""
echo "To inspect logs:"
echo "  docker compose logs -f backend"
echo "  tail -f .opencode/logs/frontend.log"
