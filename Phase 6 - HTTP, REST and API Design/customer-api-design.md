# Customer API Design

## Base URL
http://localhost:8081/v3/api-docs


## Endpoints
```bash

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/customers` | Get all customers (with pagination, filtering, sorting) |
| GET | `/customers/{id}` | Get customer by ID |
| POST | `/customers` | Create a new customer |
| PUT | `/customers/{id}` | Update an existing customer |
| DELETE | `/customers/{id}` | Delete a customer |
```


## DTOs

### CustomerRequest (POST/PUT)
#### json
```bash
{
  "name": "John Doe",
  "email": "john@email.com",
  "phone": "1234567890"
}
```
### CustomerResponse (GET)
#### json
```bash
{
  "id": 1,
  "name": "John Doe",
  "email": "john@email.com",
  "phone": "1234567890"
}

```

### Pagination, Filtering, Sorting
```bash
Parameter	Example	Purpose
page	0	Page number (starts at 0)
size	10	Items per page
sort	name,asc	Sort field and direction
name	John	Filter by name
email	john@email.com	Filter by email



Example Request:
GET /api/v1/customers?page=0&size=10&name=John&sort=name,asc
```


### Validation Rules
Field	Rule
name	Required, not blank
email	Required, valid email format
phone	Required, not blank



### Status Codes
```bash
Code	Meaning

200	Success (GET, PUT)

201	Created (POST)

204	No Content (DELETE)

400	Validation error

404	Customer not found

409	Duplicate email/phone
```