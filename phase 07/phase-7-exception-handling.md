# Exception Handling Strategy

## What I Did
- Used try-catch blocks in the controller
- Created `ErrorResponse` DTO for consistent error messages
- Returned proper status codes: 400, 404, 409

## Why I Didn't Use @ControllerAdvice

- `@ControllerAdvice` broke Swagger (500 errors)
- `@ControllerAdvice` broke JSON mapping (POST failed with 400)
- The try-catch approach works and is stable

## Error Responses
- 404: `{"status":404,"message":"Customer with ID X not found","timestamp":"..."}`

- 409: `{"status":409,"message":"Duplicate email: ...","timestamp":"..."}`
- 400: Field-specific validation errors


## Exception Handling Status
#
| Status | Implemented | Notes |
|--------|-------------|-------|
| 400 | ✅ | Validation errors via MethodArgumentNotValidException |
| 401 | ⬜ | Authentication not implemented yet  |
| 403 | ⬜ | Authorization not implemented yet |
| 404 | ✅ | Clean ErrorResponse with message + timestamp |
| 409 | ✅ | Clean ErrorResponse with duplicate email/phone message |
| 500 | ⬜ | Generic server errors (not implemented yet) |

## Stack Trace Protection

Stack traces are not leaked to the client. All error responses use:
- `ErrorResponse` DTO with `status`, `message`, `timestamp`
- Validation errors return only field-specific messages
- No `printStackTrace()` is exposed to the client