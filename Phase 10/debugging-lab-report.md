## Bug Found During Debugging

### Problem
- Duplicate email was not being rejected
- App returned **500 Internal Server Error** instead of **409 Conflict**

### Root Cause
- The duplicate check in `CustomerService.createCustomer()` was commented out
- Database unique constraint caught it, but no exception handler existed for it

### Fix
- Uncommented the duplicate check
- Re-tested — now returns **409 Conflict**

### Lesson Learned
- Always validate data before saving
- Don't rely solely on database constraints — handle errors early


## **Remote Debug**

# **What it is:** 
Debugging an app running on a different machine or server.

# **How it works:** 
The app opens a debug port (e.g., 5005) and listens for a connection from your IDE.

# **Why it's useful:** 
Helps debug issues that only appear in specific environments like Docker or staging.

# ** Example : **

```bash

java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005 -jar app.jar

```


## **Bug 2: NullPointerException**

### Problem
- Calling `GET /api/v1/customers/{id}` returned **500 Internal Server Error**

### Root Cause
- `customer` variable was forced to `null`
- `toResponse(customer)` tried to call `customer.getId()` → NPE

### Fix
- Replaced with proper repository call:
```java
Customer customer = customerRepository.findById(id)
    .orElseThrow(() -> new NotFoundException("Customer not found"));
```

## Result

- Valid ID → 200 OK

- Invalid ID → 404 Not Found


## Scenarios Debugged

### 1. Controller-to-repository flow
✅ Stepped through in debugger — works correctly

### 2. NullPointerException
✅ Fixed with `orElseThrow()` — returns 404 for invalid ID

### 3. Bad validation
✅ `@Valid` + `@NotBlank`, `@Email` — returns 400 with field errors

### 4. Wrong repository query
✅ Queries are correct — no issues found

### 5. Failed integration call
⏳ Not yet implemented — will be covered in Phase 14
