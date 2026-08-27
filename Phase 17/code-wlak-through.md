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
