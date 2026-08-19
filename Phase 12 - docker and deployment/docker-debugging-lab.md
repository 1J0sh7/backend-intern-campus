markdown
# Docker Debugging Lab — Phase 12

## Scenario 1: Wrong Port

**Action:** Changed app port from `8081:8081` to `8082:8081` in `docker-compose.yml`.

**Symptom:** `curl http://localhost:8081/api/v1/customers` → `Connection refused`

**Root Cause:** App was running on port 8082, not 8081.

**Fix:** Reverted port to `8081:8081`.

**Command:** `docker compose down && docker compose up`

**Screenshot saved** 
![wrong port ...can acess the API.png](Docker%20debugging/wrong%20port%20...can%20acess%20the%20API.png)
#
# **The docker ps -a**
#
![port changed to 8089.png](Docker%20debugging/port%20changed%20to%208089.png)

## Scenario 2: Bad DB Password

**Action:** Changed `DB_PASSWORD` to `wrongpassword` in `.env`.

**Symptom:** App failed to start with:
FATAL: password authentication failed for user "postgres"

text

**Root Cause:** Wrong password in `.env` didn't match PostgreSQL.

**Fix:** Reverted `DB_PASSWORD` to `postgres`.

**Command:** `docker compose down && docker compose up --build`

**Screenshot saved** 
![wrong password log from container.png](Docker%20debugging/wrong%20password%20log%20from%20container.png)


## Scenario 3: Missing Env Var

**Action:** Commented out `DB_URL` in `docker-compose.yml`.

**Symptom:** App failed to start with:
DataSource url is required

text

**Root Cause:** `DB_URL` was missing, so Spring couldn't connect to the database.

**Fix:** Uncommented `DB_URL: ${DB_URL}`.

**Command:** `docker compose down && docker compose up --build`

**Screenshot saved**
![removed one of the .env variables.png](Docker%20debugging/removed%20one%20of%20the%20.env%20variables.png)


## Scenario 4: Failed Healthcheck

**Action:** Changed healthcheck user from `postgres` to `wronguser`.

**Symptom:** PostgreSQL started but healthcheck failed. App hung waiting for healthy DB.

**Root Cause:** Healthcheck command failed because the user didn't exist.

**Fix:** Reverted healthcheck to `pg_isready -U postgres`.

**Command:** `docker compose down && docker compose up --build`

**Screenshot saved** 
![jar build but container failed to start .png](Docker%20debugging/jar%20build%20but%20container%20failed%20to%20start%20.png)
#
## Scenario 5: Wrong DB Host

**Action:** Changed `DB_URL` from `postgres` to `localhost` in `.env`.

**Symptom:** App failed to start with:
Connection to localhost:5432 refused

text

**Root Cause:** Inside Docker, `localhost` means the container itself, not the PostgreSQL container.

**Fix:** Reverted `DB_URL` to `jdbc:postgresql://postgres:5432/customer_db`.

**Command:** `docker compose down && docker compose up --build`

**Screenshot saved** 
![wrong host.png](Docker%20debugging/wrong%20host.png)
#
# The database cant start again
#
![container failing to start.png](Docker%20debugging/container%20failing%20to%20start.png)