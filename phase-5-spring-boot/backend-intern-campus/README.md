# Backend Intern Campus — Docker Runbook

## 📌 Overview
This project is a Spring Boot REST API for managing customers, running inside Docker containers with PostgreSQL.

---

## 🛠️ Requirements
- Docker
- Docker Compose

---

## 🚀 How to Run the App

```bash
docker compose up
```
```
The app will be available at: http://localhost:8081
```
# 🛑 How to Stop the App
```bash
docker compose down
```
# 🔁 How to Rebuild (after code changes)
```bash
./gradlew clean build
docker compose up --build
```
# 🐘 How to Check the Database
```bash
docker exec -it postgres_db psql -U postgres -d customer_db

Then run:

sql
SELECT * FROM customers;
```

# 🧪 How to Test the API

```bash
Create a customer

curl -X POST http://localhost:8081/api/v1/customers \
  -H "Content-Type: application/json" \
  -d '{"name":"Test","email":"test@test.com","phone":"1234567890"}'


Get all customers


bash
curl http://localhost:8081/api/v1/customers

```
# 🐞Troubleshooting

App fails to start
Check PostgreSQL logs:

```bash
docker logs postgres_db

```
# 📂 Project Structure
```

.
├── Dockerfile
├── docker-compose.yml
├── .env
├── src/
├── build.gradle
└── README.md
```