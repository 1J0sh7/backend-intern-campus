# Codebase Walkthrough — Phase 17

## Project Overview
This document traces 3 endpoints in my Spring Boot backend to demonstrate understanding of the request flow from controller to service to repository to database and back.

---

## Endpoint 1: GET /api/v1/customers/{id}

### Purpose
Retrieve a single customer by their ID.

### Request
GET /api/v1/customers/1
Authorization: Bearer <jwt-token>

text

### Flow

#### 1. Controller Layer
**File:** `CustomerController.java`

```java
@GetMapping("/{id}")
public ResponseEntity<CustomerResponse> getOne(@PathVariable Long id) {
    CustomerResponse customer = customerService.getCustomerById(id);
    if (customer == null) {
        return ResponseEntity.notFound().build();
    }
    return ResponseEntity.ok(customer);
}
```

# What happens:

```
Receives the id from the URL path

Calls CustomerService.getCustomerById()

Returns 200 OK with customer data, or 404 Not Found
```

# 2. Service Layer
File: CustomerService.java
```
java
public CustomerResponse getCustomerById(Long id) {
    Customer customer = customerRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Customer not found"));
    return toResponse(customer);
}
```
# What happens:

```Calls CustomerRepository.findById()

If customer exists, converts to CustomerResponse

If not, throws NotFoundException
```
# 3. Repository Layer
``` 
File: CustomerRepository.java

java
Optional<Customer> findById(Long id);
What happens:

Spring Data JPA generates the query

Query: SELECT * FROM customers WHERE id = ?
```


# 4. Database
``` Table: customers

Column	Type
id	BIGINT
name	VARCHAR
email	VARCHAR
phone	VARCHAR
user_id	BIGINT
Security
Valid JWT token required
```

# User can only view their own profile (unless ADMIN)

```
Response Example
json
{
  "id": 1,
  "name": "John Doe",
  "email": "john@email.com",
  "phone": "1234567890"
} 
```
# Endpoint 2: POST /api/v1/customers
``` 
Purpose
Create a new customer profile.

Request
text
POST /api/v1/customers
Authorization: Bearer <jwt-token>
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@email.com",
  "phone": "1234567890"
} 
```
# Flow
# 1. Controller Layer
```
File: CustomerController.java

java
@PostMapping
public ResponseEntity<CustomerResponse> create(@Valid @RequestBody CustomerRequest request) {
    CustomerResponse created = customerService.createCustomer(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
}
What happens:

Validates request body (@Valid)

Calls CustomerService.createCustomer()

Returns 201 Created with customer data
```

# 2. Service Layer
```
File: CustomerService.java

java
public CustomerResponse createCustomer(CustomerRequest request) {
    User currentUser = getCurrentUser();
    
    // Check if user already has a profile
    if (customerRepository.existsByUserId(currentUser.getId())) {
        throw new ValidationException("You already have a customer profile.");
    }
    
    // Check duplicate email
    if (customerRepository.existsByEmail(request.getEmail())) {
        throw new DuplicateResourceException("Email already exists.");
    }
    
    Customer customer = toEntity(request);
    customer.setUser(currentUser);
    
    Customer saved = customerRepository.save(customer);
    
    // Send welcome email
    emailService.sendWelcomeEmail(saved.getEmail(), saved.getName());
    
    return toResponse(saved);
}
```

# What happens:

```
Gets the current authenticated user

Checks if user already has a profile

Checks for duplicate email

Creates and saves the customer

Sends a welcome email via SendGrid
```

# 3. Repository Layer
```
File: CustomerRepository.java

java
Customer save(Customer customer);
boolean existsByUserId(Long userId);
boolean existsByEmail(String email);
Generated SQL:

sql
INSERT INTO customers (name, email, phone, user_id) VALUES (?, ?, ?, ?)
```
# 4. Database
```
Table: customers

Security
Valid JWT token required

User can only create ONE profile

ADMIN cannot create a profile (only regular users)

Response Example
json
{
  "id": 1,
  "name": "John Doe",
  "email": "john@email.com",
  "phone": "1234567890"
}
Endpoint 3: DELETE /api/v1/customers/{id}
Purpose
Delete a customer by their ID (ADMIN only).

```
# Request
```
text
DELETE /api/v1/customers/1
Authorization: Bearer <jwt-token>
```
# Flow
# 1. Controller Layer
```
File: CustomerController.java

java
@DeleteMapping("/{id}")
public ResponseEntity<Void> delete(@PathVariable Long id) {
    boolean deleted = customerService.deleteCustomer(id);
    if (!deleted) {
        return ResponseEntity.notFound().build();
    }
    return ResponseEntity.noContent().build();
}
```
# What happens:

``` 
Receives the id from the URL path

Calls CustomerService.deleteCustomer()

Returns 204 No Content if successful, or 404 Not Found

```
# 2. Service Layer
```
File: CustomerService.java

java
public boolean deleteCustomer(Long id) {
    Customer existing = customerRepository.findById(id)
        .orElseThrow(() -> new NotFoundException("Customer not found"));
    
    if (!isAdmin()) {
        throw new AccessDeniedException("Only ADMIN can delete customers");
    }
    
    customerRepository.deleteById(id);
    return true;
}
```
# What happens:

``` 
Checks if customer exists

Checks if current user is ADMIN

If not ADMIN, throws AccessDeniedException

If ADMIN, deletes the customer

```
# 3. Repository Layer
```
File: CustomerRepository.java

java
void deleteById(Long id);
Generated SQL:

sql
DELETE FROM customers WHERE id = ?
Security
Valid JWT token required

Only ADMIN can delete customers

```
#Response
 ```
 204 No Content on success

404 Not Found if customer doesn't exist

403 Forbidden if user is not ADMIN

```
# Summary Table
```
Endpoint	Method	Controller	Service	Repository	Security
/api/v1/customers/{id}	GET	getOne()	getCustomerById()	findById()	JWT + Owner check
/api/v1/customers	POST	create()	createCustomer()	save()	JWT + One profile only
/api/v1/customers/{id}	DELETE	delete()	deleteCustomer()	deleteById()	JWT + ADMIN only
External Integrations
```

### SendGrid → Welcome email sent after customer creation

#### PostgreSQL → Data persistence

### JWT → Authentication

### Spring Security → Authorization

# Key Files
```
Layer	Files
Controller	CustomerController.java, AuthController.java
Service	CustomerService.java, CustomUserDetailsService.java, EmailService.java
Repository	CustomerRepository.java, UserRepository.java
Model	Customer.java, User.java, Role.java
DTO	CustomerRequest.java, CustomerResponse.java, LoginRequest.java
Security	JwtUtil.java, JwtAuthenticationFilter.java, SecurityConfig.java
Exception	GlobalExceptionHandler.java
```
# Conclusion
```
This codebase follows a clean layered architecture:

Controller → HTTP handling

Service → Business logic

Repository → Database access

All endpoints are secured with JWT and role-based access control.
```

### 
#
#
#



## Endpoint 2: POST /api/v1/guests

### Purpose
Creates a new guest record. Admin-only endpoint.

### Entry Point
- **Class:** `GuestController` (`domain/guest/api/GuestController.java`)
- **Mapping:** `@PostMapping` under `@RequestMapping("/api/v1/guests")`
- **Authorization:** class-level `@PreAuthorize("hasRole('ADMINISTRATOR')")` — applies to every method in the controller
- **Validation:** `@Valid @RequestBody GuestCommand command` — bean validation before the service layer runs

### Request
```json
POST http://localhost:8080/api/v1/guests
Authorization: Bearer <token>

{
  "name": "string",
  "surname": "string",
  "phoneNumber": "0782345678",
  "email": "string@gmail.com"
}
```

### Flow: Controller → Service → Repository → Entity → DB

1. **Controller** validates `GuestCommand`, calls `guestService.create(command)`.
2. **Service** (`Guestservice` / `GuestServiceImpl`) maps `GuestCommand` → `Guest` entity via `GuestMapper` (MapStruct-generated).
3. **Repository** (`GuestRepository extends AbstractBaseRepository<Guest>`) persists the entity.
4. **Entity** `Guest` — table `guest.guests` (custom Postgres-style schema, running on H2 in `MODE=PostgreSQL` for local dev). Fields: `name`, `surname`, `phoneNumber`, `email`, plus inherited `BaseEntity` fields (`id`, `uid`, `createdBy`, `createdDate`, `lastModifiedBy`, `lastModifiedDate`, `version`).
5. `uid` is server-generated on creation, not client-supplied.
6. Mapped back to `GuestDto` via `GuestMapper.toDto()`.

### Response — actual (201 Created)
```json
{
  "success": true,
  "message": "Guest created",
  "data": {
    "id": 1,
    "uid": "01a0480b-8e62-7ec0-b0e6-4d10a2d4c262",
    "createdBy": "verchnate@hobbstech.co.zw",
    "lastModifiedBy": "verchnate@hobbstech.co.zw",
    "lastModifiedDate": 1787923500643,
    "createdDate": 1787923500643,
    "version": 0,
    "name": "string",
    "surname": "string",
    "phoneNumber": "0782345678"
  }
}
```

### Debugging note
Initial testing returned `403 Forbidden` with an empty response body. Root cause: a double slash in the request URL (`http://localhost:8080//api/v1/guests`), not an authentication or permissions problem. Correcting to a single slash resolved it. Lesson: an unexpected 403 can originate from client-side request malformation, not just server-side security config — worth checking the raw request before assuming a backend bug.

### Notable design decisions
- `createdBy`/`lastModifiedBy` auto-populated from the authenticated principal (audit trail baked into `BaseEntity`)
- `uid` is server-generated, decoupled from the auto-increment `id`


#
#
#
## Endpoint 3: GET /api/v1/wedding-details

### Purpose
Retrieves the singleton "wedding details" record (date, time, dress code, etc.) — a single record representing the one wedding this instance is configured for, not a list.

### Entry Point
- **Class:** `WeddingDetailsController` (`domain/wedding/details/api/WeddingDetailsController.java`)
- **Mapping:** `GET /api/v1/wedding-details`
- **Authorization:** requires a valid Bearer token (returned 200, not 403, confirming it's an authenticated-but-accessible endpoint — role requirements likely less strict than Guests, or open to any authenticated user)

### Flow: Controller → Service → Repository
1. **Controller** delegates to `WeddingDetailsService` / `WeddingDetailsServiceImpl` (`domain/wedding/details/service/`)
2. **Service** queries `WeddingDetailsRepository` for the single record
3. **Entity:** `WeddingDetails` (`domain/wedding/details/domain/WeddingDetails.java`) — singleton pattern, likely enforced by always querying for one fixed row rather than allowing multiple

### Response — actual (200 OK)
```json
{
  "success": true,
  "message": "Not yet configured",
  "data": null
}
```

### Notable design decisions
- **Graceful "empty" handling**: rather than a `404 Not Found`, the service returns `200 OK` with `data: null` and an explanatory message. This is a deliberate UX choice — a frontend consuming this can distinguish "not configured yet" from a real error, without needing separate error-handling logic for a 404.
- **Singleton resource pattern**: unlike `Guests` (a list, paginated, CRUD), `WeddingDetails` represents exactly one record per deployment — a common pattern for app-wide settings (matches `Location` and `WhatsApp Contact` in the tag list, also marked "Singleton").
- Confirms this instance's H2 database is genuinely fresh — no data has been seeded for wedding details yet, consistent with `ddl-auto: create-drop` wiping state on every container restart.






#

# Summary of what i did 
---

I downloaded an existing backend project I'd never seen before (Hn-Event-Manager, a wedding event management system) and worked through understanding it end-to-end.

**1. Getting it running**
Found a Dockerfile and tried building it — the build kept stalling on a font installation step (the app generates PDF reports, which needs fonts). Since font rendering wasn't needed to explore the API, I temporarily commented out those lines to unblock the build. Got the container running on port 8080.

**2. Finding the API docs**
Opened Swagger, which auto-generates a page listing every endpoint the app exposes, so I could see the app's capabilities without reading every file first.

**3. Traced 3 real requests through the code**
- **Login** — authenticated with the default admin account the app seeds on first run, got back a JWT token, and traced the exact files handling the request: controller → password check → token generation.
- **Create Guest** — hit a `403 Forbidden` on my first attempt. Debugged it and found the real cause: a typo in my request URL (double slash), not a permissions bug. Fixed it and got a newly created guest back with a generated ID. Traced this one down to the actual database table.
- **Wedding Details** — a simpler GET request. Got a clean response showing the record hasn't been configured yet — this revealed a design pattern where the app returns a friendly "not configured" message instead of a typical 404 error.

**4. Debugged live, not just read code**
Connected IntelliJ's debugger directly into the running Docker container (via a JDWP remote debug port), set breakpoints, and stepped through the code live while sending real requests — watching execution happen line by line rather than guessing from static reading.

**5. Documented everything**
This file captures all three endpoints traced from API call down to database, with real request/response evidence and debug screenshots.
