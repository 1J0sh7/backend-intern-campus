# Customer API — Production Readiness Runbook

Phase 19 deliverable: build, deploy, monitor, and troubleshoot reference for the customer-facing loan API.

---

## Quick Start

```bash
# 1. Build
./gradlew clean build

# 2. Run
docker compose up --build -d

# 3. Verify
curl http://localhost:8081/actuator/health
# Expected: {"status":"UP"}
```

---

## Environment Variables

Set these in your `.env` file before starting the stack:

| Variable | Purpose |
|---|---|
| `DB_URL` | PostgreSQL connection string |
| `DB_USERNAME` | Database user |
| `DB_PASSWORD` | Database password |
| `SENDGRID_API_KEY` | SendGrid API key for transactional email |
| `SENDGRID_FROM_EMAIL` | Verified sender address |

---

## Logs

```bash
# Tail everything
docker compose logs -f app

# Errors only
docker compose logs -f app | grep -i "error\|exception"

# Email / retry activity
docker compose logs -f app | grep -i "email\|sendgrid\|retry"
```

---

## Monitoring

| Tool | URL | Notes |
|---|---|---|
| Actuator Health | http://localhost:8081/actuator/health | Liveness/readiness |
| Actuator Metrics | http://localhost:8081/actuator/metrics | Raw metrics |
| Prometheus | http://localhost:9090 | Scraped metrics |
| Grafana | http://localhost:3000 | `admin` / `admin` |

---

## Database Migrations

Flyway runs automatically on startup. To inspect migration history manually:

```bash
docker exec -it postgres_db psql -U ${DB_USERNAME} -d customer_db \
  -c "SELECT version, description, success FROM flyway_schema_history ORDER BY installed_rank;"
```

---

## Rollback

```bash
# Find the last known-good commit
git log --oneline

# Roll back
git revert <HASH>
# — or —
git checkout <STABLE_TAG>

# Rebuild
docker compose down
docker compose up --build -d
```

---

## Troubleshooting

| Problem | Check |
|---|---|
| App won't start | `docker compose logs app` |
| DB connection failed | `docker compose logs postgres` and `.env` values |
| Emails not sending | `/actuator/health` for SendGrid status, then retry logs above |
| Port already in use | `sudo lsof -i :8081` (Linux) / `netstat -ano \| findstr 8081` (Windows) |

---

## Phase 19 Status

| Task | Status |
|---|---|
| Flyway migrations | ✅ Done |
| Graceful shutdown | ✅ Done |
| SendGrid resilience (timeouts + retries) | ✅ Done |
| Custom health check | ✅ Done |