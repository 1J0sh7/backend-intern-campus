## Log Reading (Item 5)

### From IntelliJ
- Logs are visible in the console when running `./gradlew bootRun`
- Correlation ID is present in all logs
- Screenshot saved as `intellij-logs.png`

### From Docker
- Docker will be set up in Phase 12
- Log reading from Docker will be documented then


#
## Item 6 — Protect Data

### What I will Do
- Never log passwords, tokens, or personal data
- Only log business context (email, ID, phone)
- No sensitive data is exposed in logs

### OWASP Rules Followed
- ✅ No passwords in logs
- ✅ No tokens in logs
- ✅ No PII (personal data) in logs
- ✅ Logs are safe to share for debugging

### Example of Safe Log

``` bash
Customer created with ID: 1, email: john@email.com

```