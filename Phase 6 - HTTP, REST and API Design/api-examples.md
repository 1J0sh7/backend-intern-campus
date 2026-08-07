# Customer API Examples

## Base URL
http://localhost:8081/api/v1


## Endpoints

### GET /customers
```bash
**Request:**
GET http://localhost:8081/api/v1/customers

```

**Response (200 OK):**

```bash
```json
[
  {
    "id": 1,
    "name": "Caleb Tevera(Editted)",
    "email": "Calebtevera@gmail.com",
    "phone": "0782457059"
  }
]

```
POST /customers
Request:
```bash
json
{
  "name": "John Doe",
  "email": "john@email.com",
  "phone": "1234567890"
}



Response (201 Created):

json
{
  "id": 2,
  "name": "John Doe",
  "email": "john@email.com",
  "phone": "1234567890"
}


```


POST /customers (Duplicate)
Response (409 Conflict):
```bash
json
{
  "error": "Customer with email john@email.com already exists"
}
```

GET /customers/{id}
Response (200 OK):
```bash
json
{
  "id": 1,
  "name": "Caleb Tevera(Editted)",
  "email": "Calebtevera@gmail.com",
  "phone": "0782457059"
}
```

PUT /customers/{id}
Request:

```bash
json
{
  "name": "Updated Name",
  "email": "updated@email.com",
  "phone": "1111111111"
}


Response (200 OK):

json
{
  "id": 1,
  "name": "Updated Name",
  "email": "updated@email.com",
  "phone": "1111111111"
}

```



DELETE /customers/{id}
```bash 
Response: 204 No Content
```


GET /customers/{id} (Not Found)
```bash
Response: 404 Not Found
```


```bash
Status Codes Summary
Code	Meaning	When Used
200	OK	GET, PUT success
201	Created	POST success
204	No Content	DELETE success
400	Bad Request	Validation error
404	Not Found	Customer doesn't exist
409	Conflict	Duplicate email
```