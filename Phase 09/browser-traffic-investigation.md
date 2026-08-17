# Browser Traffic Investigation — Phase 9

## 1. DevTools Setup
- Opened Chrome DevTools → Network tab
- Enabled "Preserve log" and "Disable cache"

## 2. Request Triggered
- Endpoint: `GET /api/v1/customers`
- From: Swagger UI

## 3. Request Details
- **Method:** GET
- **URL:** `http://localhost:8081/api/v1/customers`
- **Status:** 200 OK
- **Content-Type:** application/json

## 4. Response Details
- Returned list of customers with pagination info
- Example:
```json
{
  "content": [],
  "page": 0,
  "size": 10,
  "totalElements": 2
}


```

# 5. cURL Command

```bash
curl -X GET "http://localhost:8081/api/v1/customers?page=0&size=10" 

-H "accept: application/json"

```
# 6. HAR File
```  

Exported as network-log.har

```

# 7. Key Takeaways

```bash

- DevTools helps inspect request/response details

- cURL allows reproducing requests from terminal

- HAR files share debugging logs with others

