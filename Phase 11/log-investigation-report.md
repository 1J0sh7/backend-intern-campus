# Log Investigation Report — Phase 11

## Purpose
To investigate and document 5 broken scenarios using logs from IntelliJ.

---

## Scenario 1: Duplicate Email

**Action:** Sent POST `/api/v1/customers` with an email that already exists.

**Logs:**

```aiignore
2026-08-18 12:23:14 [http-nio-8081-exec-1] DEBUG [e3892a76-...] c.c.s.CustomerService - Checking for duplicate email: johtn@email.com
2026-08-18 12:23:14 [http-nio-8081-exec-1] WARN [e3892a76-...] c.c.exception.GlobalExceptionHandler - Duplicate resource attempt: Customer with that email already exists
```


**Correlation ID:** `e3892a76-be12-4b49-8b16-b12ca47e3bc8`

**Status:** 409 Conflict

**Root Cause:** Email already exists in the database.

**Fix:** The duplicate check is already in place — it prevents duplicates and logs a warning.

---

## Scenario 2: Customer Not Found

**Action:** Sent GET `/api/v1/customers/999` with an ID that does not exist.

**Logs:**

```aiignore

2026-08-18 12:23:14 [http-nio-8081-exec-1] INFO [e3892a76-...] c.c.c.CustomerController - Searching for customer with ID: 999
2026-08-18 12:23:14 [http-nio-8081-exec-1] WARN [e3892a76-...] c.c.exception.GlobalExceptionHandler - Resource not found: Customer with ID 999 not found
```


**Status:** 404 Not Found

**Root Cause:** Customer ID does not exist.

**Fix:** The service already handles this by throwing `NotFoundException` and returning 404.

---

## Scenario 3: Invalid Email Format

**Action:** Sent POST `/api/v1/customers` with invalid email format (e.g., `notanemail`).

**Logs:**

```aiignore
2026-08-18 12:23:14 [http-nio-8081-exec-1] ERROR [e3892a76-...] c.c.exception.GlobalExceptionHandler - Validation error: Email must be valid
```


**Status:** 400 Bad Request

**Root Cause:** Email does not match `@Email` validation.

**Fix:** Validation is already in place — the error is caught and logged.

---

## Scenario 4: Empty Name

**Action:** Sent POST `/api/v1/customers` with an empty `name` field.

**Logs:**

```aiignore
2026-08-18 12:23:14 [http-nio-8081-exec-1] ERROR [e3892a76-...] c.c.exception.GlobalExceptionHandler - Validation error: Name is required
```


**Status:** 400 Bad Request

**Root Cause:** Name field is empty.

**Fix:** `@NotBlank` validation is already in place.

---

## Scenario 5: Missing Phone Number

**Action:** Sent POST `/api/v1/customers` without a `phone` field.

**Logs:**

```aiignore
2026-08-18 12:23:14 [http-nio-8081-exec-1] ERROR [e3892a76-...] c.c.exception.GlobalExceptionHandler - Validation error: Phone is required
```


**Status:** 400 Bad Request

**Root Cause:** Phone field is missing.

**Fix:** `@NotBlank` validation is already in place.

---

## Summary of Findings

| Scenario | Error | Status | Root Cause | Fix Already in Place? |
|----------|-------|--------|------------|------------------------|
| 1 | Duplicate email | 409 | Email exists | ✅ Yes |
| 2 | Customer not found | 404 | Invalid ID | ✅ Yes |
| 3 | Invalid email format | 400 | Invalid format | ✅ Yes |
| 4 | Empty name | 400 | Empty field | ✅ Yes |
| 5 | Missing phone | 400 | Missing field | ✅ Yes |

---

## Tools Used
- IntelliJ console for log reading
- Correlation ID to trace requests (`e3892a76-...`)
- Timestamps to track execution flow
- Log levels (DEBUG, WARN, ERROR) to identify severity

---
 scenarios were successfully investigated using logs. The errors were identified, root causes found, and fixes are already in place. Logging with correlation ID allows full request tracing.