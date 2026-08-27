Phase 16 - Observability and Monitoring (OB-001)
## Prometheus + Grafana Setup Lab Report

**Date:** 2026-08-27  
**Objective:** Integrate Prometheus and Grafana into the existing Docker Compose stack to monitor the `customer_api` Spring Boot application.

---

## 1. Overview

This lab added a complete observability stack to the project:

| Component | Purpose |
|-----------|---------|
| **Micrometer** | Java library that exposes application metrics (JVM, HTTP, CPU) |
| **Prometheus** | Scrapes and stores time-series metrics from the application |
| **Grafana** | Visualizes metrics from Prometheus in dashboards and graphs |

---

## 2. What Was Done

### 2.1 Updated `docker-compose.yml`

Added two new services to the existing Docker Compose setup:

```yaml
# ==================== PROMETHEUS ====================
prometheus:
  image: prom/prometheus:latest
  container_name: prometheus
  ports:
    - "9090:9090"
  volumes:
    - ./prometheus.yml:/etc/prometheus/prometheus.yml
  networks:
    - app-network

# ==================== GRAFANA ====================
grafana:
  image: grafana/grafana:latest
  container_name: grafana
  ports:
    - "3000:3000"
  environment:
    - GF_SECURITY_ADMIN_USER=admin
    - GF_SECURITY_ADMIN_PASSWORD=admin
  volumes:
    - grafana-storage:/var/lib/grafana
  networks:
    - app-network
```
### Also added a shared network (app-network) and a persistent volume for Grafana data.

# 2.2 Created prometheus.yml
### This file tells Prometheus where to scrape metrics from:
```
yaml
global:
  scrape_interval: 5s

scrape_configs:
  - job_name: 'spring-boot-app'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['app:8081']
Key detail: The target is app:8081 (Docker service name, not localhost) so Prometheus can reach the app inside the Docker network.

2.3 Added Gradle Dependency
In build.gradle:

groovy
implementation 'io.micrometer:micrometer-registry-prometheus'
2.4 Updated application-dev.yml
Enabled the Prometheus actuator endpoint:

yaml
management:
  endpoints:
    web:
      exposure:
        include: health, metrics, info, prometheus   # added 'prometheus'
  endpoint:
    health:
      show-details: always
    prometheus:
      enabled: true                                 # explicitly enabled
2.5 Updated SecurityConfig.java
Added /actuator/** to the public permit list so Prometheus can access metrics without authentication:

java
.requestMatchers("/actuator/**").permitAll()   // <-- Added this line
2.6 Fixed Environment Variables
Added SENDGRID_FROM_EMAIL to .env and docker-compose.yml to prevent the app from crashing on startup.
```

# 2.7 Verified Deployment
```bash
docker compose down
docker compose up --build
```
# 3. Verification Checklist
```
Check	Command / URL	Result
All containers running	docker ps	✅ customer_api, postgres_db, prometheus, grafana all Up
App health endpoint	curl http://localhost:8081/actuator/health	✅ {"status":"UP"}
Prometheus metrics endpoint	curl http://localhost:8081/actuator/prometheus | head -20	✅ Returns metric data
Prometheus targets	http://localhost:9090/targets	✅ spring-boot-app = UP (1/1)
Grafana login	http://localhost:3000 (admin/admin)	✅ Success
```
# 4. How to Use Prometheus
## 4.1 Access
```
URL: http://localhost:9090

Use the Graph tab to query metrics.
```
# 4.2 Useful Queries
```
Query	What it shows
http_server_requests_seconds_count	Total request count by endpoint
rate(http_server_requests_seconds_count[1m])	Requests per second over last 1 minute
http_server_requests_seconds_max	Max request duration
jvm_memory_used_bytes	Heap memory usage
process_cpu_usage	CPU usage
```
# 4.3 Example
```
text
rate(http_server_requests_seconds_count{method="GET", uri="/api/v1/customers"}[5m])
Shows the request rate for GET /api/v1/customers over 5 minutes.
```

# 5. How to Use Grafana
### 5.1 Access
```
URL: http://localhost:3000


Login: admin / admin

5.2 Add Prometheus Data Source
Go to Connections → Data Sources → Add data source.

Select Prometheus.

Set URL: http://prometheus:9090

Click Save & Test → ✅ Success.

5.3 Import Pre-Built Dashboard
Go to Dashboards → Import.

Enter Dashboard ID: 4701 (Micrometer / Spring Boot).

Click Load.

Select the Prometheus data source.

Click Import.

This dashboard shows:

HTTP request rate, latency, and error count

JVM memory pools (Heap, Non-Heap)

Garbage collection activity

CPU usage

Thread states
```

# 5.4 Explore Mode (Ad-Hoc Queries)
```
Go to Explore (compass icon).

Select Prometheus data source.

Enter a query and click Run query.

Example: http_server_requests_seconds_count{status="200"}
```

# 6. Real-Time Observation
```
Observed: JVM Heap Memory
Before hitting endpoints: ~1.6 MB

After hitting endpoints: ~2.4 MB

Conclusion: The heap increased due to object allocation during request processing. This proves that the metrics being displayed in Grafana are from customer_api, not a dummy data source.

```
# 7. Quick Reference
```
Tool	URL	Credentials
API	http://localhost:8081	JWT required
Swagger UI	http://localhost:8081/swagger-ui/index.html	-
Actuator Health	http://localhost:8081/actuator/health	Public
Prometheus Metrics	http://localhost:8081/actuator/prometheus	Public
Prometheus UI	http://localhost:9090	-
Grafana	http://localhost:3000	admin/admin
```
# 8. Commands for Next Time
```
Scenario	Command
Start everything	docker compose up -d
Rebuild + start after code change	docker compose up --build -d
Check logs	docker compose logs -f app
Stop everything	docker compose down
Stop + remove volumes (clean slate)	docker compose down -v
```
