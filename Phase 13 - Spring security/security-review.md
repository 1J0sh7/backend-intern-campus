# Security Review — Phase 13

## 1. Authentication & Authorization

| Feature | Implementation |
|---------|----------------|
| User model | `User` entity with `username`, `password`, `role` |
| Role model | `Role` enum (`ADMIN`, `USER`) |
| Password encoding | `BCryptPasswordEncoder` (hashing) |
| Login endpoint | `POST /api/auth/login` → returns JWT token |
| Token validation | `JwtAuthenticationFilter` validates every request |
| Role-based access | `.hasRole("ADMIN")` on `/api/v1/customers/**` |

---

## 2. Security Flow

| Scenario | Status Code | Message |
|----------|-------------|---------|
| No token / invalid token | 401 Unauthorized | "Please login to access this resource" |
| Valid token, wrong role | 403 Forbidden | "You do not have permission to access this resource" |
| Valid token, correct role | 200 OK | Data returned |

---

## 3. JWT Configuration

| Property | Value |
|----------|-------|
| Secret | Stored in `application-dev.yml` |
| Expiration | 24 hours (86400000 ms) |
| Algorithm | HS256 |

---

## 4. Secrets & Logging

| Practice | Status |
|----------|--------|
| JWT secret in config (not hardcoded) | ✅ |
| `.env` ignored in `.gitignore` | ✅ |
| Passwords never logged | ✅ |
| Tokens never logged | ✅ |
| Authentication errors log only username | ✅ |

---

## 5. Endpoint Security

| Endpoint | Access |
|----------|--------|
| `/api/auth/login` | Public |
| `/swagger-ui/**`, `/v3/api-docs/**` | Public |
| `/api/v1/customers/**` | ADMIN only |
| Any other request | Authenticated |

---

## 6. Password Storage

| Practice | Status |
|----------|--------|
| Passwords hashed with BCrypt | ✅ |
| No plain-text passwords stored | ✅ |
| Strong hashing algorithm used | ✅ |

---

## 7. Risks & Mitigations

| Risk | Mitigation |
|------|------------|
| JWT token theft | Short expiration (24h), HTTPS in production |
| CSRF | Disabled (stateless API) |
| Session hijacking | Stateless sessions (no session storage) |
| SQL injection | JPA/Hibernate handles it |
| Password leak | BCrypt hashing + no logging |

---

## 8. Tools & Libraries Used

| Tool | Purpose |
|------|---------|
| Spring Security | Authentication & authorization |
| JJWT | JWT generation & validation |
| BCrypt | Password hashing |
| JPA / Hibernate | User storage |
| PostgreSQL | Database |

---

## 9. Summary

Phase 13 is complete. The API is secured with JWT-based authentication and role-based authorization. Error responses are clean and consistent (401 vs 403). No sensitive data is exposed in logs or responses.