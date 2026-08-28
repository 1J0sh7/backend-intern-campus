## Endpoint 1: POST /api/opn/auth/login

### Purpose
Authenticates a system admin using email and password, returning a signed JWT for subsequent authorized requests.

### Entry Point
- **Class:** `AuthController`
- **Path:** `domain/identity/api/AuthController.java`
- **Mapping:** `@PostMapping("/api/opn/auth/login")`
- **Note:** the `opn` segment in the path marks this as one of the few public (open/unauthenticated) endpoints in the API.

### Request
- **DTO:** `LoginRequest` (`domain/identity/dto/request/LoginRequest.java`)
- **Fields:** `email`, `password`
- **Example body:**
```json
{
  "email": "verchnate@hobbstech.co.zw",
  "password": "%$Pass123"
}
```

### Flow: Controller → Service → Repository

1. **Controller** receives the `LoginRequest`, passes it to the identity/auth service layer.
2. **Credential lookup:** the service loads the matching `User` entity via `UserRepository` (`domain/identity/user/domain/`), keyed by email.
3. **Password verification:** the stored hash is compared against the submitted password using `BCrypt` (`shared/encryption/BCrypt.java`) — plaintext passwords are never stored.
4. **Session creation:** a `LoginSession` record is created via `LoginSessionServiceImpl` → `LoginSessionRepository` (`domain/identity/login/`), producing the `sessionId` embedded in the token. This lets the system track/revoke active sessions later (see `LoginSessionController` and `VIEW_LOGIN_SESSIONS` permission).
5. **Permission resolution:** the user's assigned `Role`(s) are resolved (`domain/identity/role/`) and flattened into a single permission list — avoiding a per-request roles lookup on protected endpoints later.
6. **Token generation:** `JwtTokenProvider` (`shared/security/JwtTokenProvider.java`) signs a JWT (HS256) containing:
    - `sub` — user's email
    - `principalUid` — user's unique ID
    - `principalType` — `"ADMIN"` (this app supports two principal types — admin and guest, guests authenticate separately via OTP)
    - `permissions` — full flattened permission list
    - `sessionId` — links token to the `LoginSession` record
    - `passwordChangeRequired` — flag checked on protected routes by `PasswordChangeRequiredInterceptor`
    - `iat` / `exp` — issued-at and expiry (standard JWT claims)

### Response
- **DTO:** `LoginResponse` (`domain/identity/dto/response/LoginResponse.java`)
- **Wrapped in:** `ApiResponse` (`shared/response/ApiResponse.java`) — the standard `{success, message, data}` envelope used app-wide
- **Example:**
```json
{
  "success": true,
  "message": "Success",
  "data": {
    "token": "<JWT>",
    "type": "Bearer"
  }
}
```

### Notable design decisions
- Permissions are baked directly into the JWT payload rather than looked up per-request — trades token size for fewer DB round-trips on protected endpoints.
- Sessions are persisted server-side (`LoginSession`) even though auth is JWT-based, giving the system the ability to audit or invalidate sessions despite JWTs normally being stateless.
- `passwordChangeRequired` is enforced via an interceptor, not baked into the login response body — meaning any protected call after login can still block the user until they change a forced-reset password.