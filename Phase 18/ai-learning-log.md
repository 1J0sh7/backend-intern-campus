# AI Learning Log — Phase 18

## 1. Prompt: Explain JWT Authentication Flow

**My Prompt:**
"Explain how JWT authentication works in Spring Boot, including the filter chain."

**AI Output:**
[AI explanation of JWT filter, SecurityContext, token validation]

**Validation:**
- Cross-checked with Spring Security documentation
- Verified against my existing `JwtAuthenticationFilter.java`
- Matched the implementation

**Decision:** ✅ Accepted — explanation aligned with my code

---

## 2. Prompt: Generate Test Cases

**My Prompt:**
"Generate JUnit test cases for CustomerService.createCustomer()"

**AI Output:**
[AI generated test methods]

**Validation:**
- Compared with my existing `CustomerServiceTest.java`
- Some tests were redundant
- Added one new edge case: null email

**Decision:** ✅ Partially accepted — added one new test, ignored duplicates

---

## 3. Prompt: Refactor EmailService

**My Prompt:**
"How can I refactor EmailService to use a template?"

**AI Output:**
[AI suggested using SendGrid dynamic templates]

**Validation:**
- Checked SendGrid documentation
- Verified template ID approach works

**Decision:** Deferred — ✅implemented  it