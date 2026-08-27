# Observability Lab — Phase 16

## 1. Health Check
**Endpoint:** `/actuator/health`
**Response:** `{"status":"UP"}`

## 2. Metrics
**Endpoint:** `/actuator/metrics`
**Metrics available:** memory, CPU, request count, etc.

## 3. Container Monitoring
**Command:** `docker stats`
**Result:** CPU and memory usage shown

## 4. Request Tracing
**Tool:** Correlation ID from Phase 11
**Example:** `344c5293-3afa-4cfa-9b12-8141e1ff6232`
**Action:** Each request has a unique ID in logs

## 5. Logs vs Metrics vs Traces
- **Logs:** What happened (events)
- **Metrics:** How much / how fast (numbers)
- **Traces:** Where time went (path)