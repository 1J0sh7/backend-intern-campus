# Phase 7 - Postman Testing Evidence (Requests Only)

## Base URL
http://localhost:8081/api/v1/customers

text

---

## 1. Create Customer (POST)

**Method:** POST  
**URL:** `http://localhost:8081/api/v1/customers`  
**Headers:** `Content-Type: application/json`

**Body:**
```json
{
  "name": "John Doe",
  "email": "john@email.com",
  "phone": "1234567890"
}

```
# 2. Create Duplicate Email (POST)
# 3Method: POST
URL: http://localhost:8081/api/v1/customers

Headers: Content-Type: application/json
```dash
Body:

json
{
  "name": "Jane Smith",
  "email": "john@email.com",
  "phone": "9876543210"
}
```
# 3. Create Duplicate Phone (POST)
Method: POST

URL: http://localhost:8081/api/v1/customers

Headers: Content-Type: application/json
```bash
Body:

json
{
  "name": "Jane Smith",
  "email": "jane@email.com",
  "phone": "1234567890"
}
```

# 4. Create Invalid Customer (POST)
 Method: POST

URL: http://localhost:8081/api/v1/customers

Headers: Content-Type: application/json

```bash
Body:

json
{
  "name": "",
  "email": "notanemail",
  "phone": ""
}

```

# 5. Get All Customers (GET)
Method: GET
```bash
URL: http://localhost:8081/api/v1/customers
```

#6. Get All Customers with Pagination (GET)
Method: GET

```bash
URL: http://localhost:8081/api/v1/customers?page=0&size=5
```

# 7. Get All Customers with Filtering (GET)
Method: GET
```bash
URL: http://localhost:8081/api/v1/customers?name=John
```

# 8. Get All Customers with Sorting (GET)
Method: GET

```bash
URL: http://localhost:8081/api/v1/customers?sort=name,asc
```


# 9. Get Customer by ID (GET)
Method: GET

```bash
URL: http://localhost:8081/api/v1/customers/1
```


# 10. Get Customer by Invalid ID (GET)
Method: GET

```bash
URL: http://localhost:8081/api/v1/customers/999
```
# 11. Update Customer (PUT)
Method: PUT

URL: http://localhost:8081/api/v1/customers/1

Headers: Content-Type: application/json
```bash
Body:

json
{
  "name": "John Updated",
  "email": "john.updated@email.com",
  "phone": "1111111111"
}
```

# 12. Update with Duplicate Email (PUT)
Method: PUT

URL: http://localhost:8081/api/v1/customers/1
Headers: Content-Type: application/json
```bash
Body:

json
{
  "name": "John",
  "email": "existing@email.com",
  "phone": "123"
}
```


# 13. Update Invalid ID (PUT)
Method: PUT

URL: http://localhost:8081/api/v1/customers/999

Headers: Content-Type: application/json
```bash
Body:

json
{
  "name": "John",
  "email": "test@email.com",
  "phone": "123"
}

```

# 14. Patch Customer (PATCH)
Method: PATCH

URL: http://localhost:8081/api/v1/customers/1

Headers: Content-Type: application/json

```
Body:

json
{
  "phone": "9999999999"
}

```

# 15. Patch Multiple Fields (PATCH)
Method: PATCH

URL: http://localhost:8081/api/v1/customers/1

Headers: Content-Type: application/json
```bash

Body:

json
{
  "name": "John Full Update",
  "phone": "7777777777"
}

```


# 16. Patch Invalid ID (PATCH)
Method: PATCH

URL: http://localhost:8081/api/v1/customers/999

Headers: Content-Type: application/json

```bash
Body:

json
{
  "phone": "123"
}
```

# 17. Delete Customer (DELETE)
Method: DELETE

```bash
URL: http://localhost:8081/api/v1/customers/1
```

# 18. Delete Invalid ID (DELETE)

Method: DELETE
```bash
URL: http://localhost:8081/api/v1/customers/999

```