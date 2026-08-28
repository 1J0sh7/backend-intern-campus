# AI Learning Log — Phase 18 (AI-001)

## Purpose
Log of how I used AI (Claude) during backend work, what I asked, what I validated myself, and what I accepted or rejected. Carried over from real Phase 17 work since that session was heavily AI-assisted.

---

## 1. Explaining unfamiliar code / project structure

**Prompt:** Asked AI to explain what an unfamiliar downloaded project (Hn-Event-Manager) does, based on its package structure (`tree` output) and a project doc.

**AI output:** Summarized the domains (identity/auth, guest management, wedding content, photos, messaging, file storage, notifications, PDF generation, audit trail) and inferred the tech stack (Spring Boot, JPA/Hibernate, JWT, Postgres schema-per-domain).

**Validation:** Cross-checked against the actual file tree and a project markdown doc (`FAQ_CATEGORIES_IMPLEMENTATION.md`) I had on hand — confirmed the FAQ Category feature matched exactly what AI inferred from the folder names, giving confidence the rest of the summary was reliable too.

**Verdict:** Accepted as-is. Saved significant time versus reading 700+ files manually before knowing where to start.

---

## 2. Debugging a build failure (Docker font install stalling)

**Prompt:** Pasted a stuck `docker build` log (apk installing fonts, 45+ min stall).

**AI output:** Suggested the stall was a slow/flaky connection to the Alpine package mirror, and proposed commenting out the font install lines (not needed to explore the API) to unblock the build.

**Validation:** Ran it myself — build completed in seconds once the font lines were skipped. Confirmed the app still started fine without fonts (only PDF/report generation would be affected, which I wasn't testing).

**Verdict:** Accepted. I made the actual Dockerfile edit myself and kept my own commented-out lines untouched, only adding the new comment/skip lines — didn't let AI rewrite the whole file blindly.

---

## 3. Debugging a 403 Forbidden on POST /api/v1/guests

**Prompt:** Pasted a Postman screenshot showing a 403 with an empty response body, after already confirming my JWT and role looked correct.

**AI output:** Initially proposed several theories (CSRF, wrong role prefix, wrong path). Spotted a double slash in my request URL (`localhost:8080//api/v1/guests`) as the most likely actual cause.

**Validation:** I fixed the URL myself and resent — confirmed `201 Created` with a real guest record back. Verified AI's diagnosis was correct rather than one of the other theories, since the fix that actually worked was the URL, not a security config change.

**Verdict:** Accepted the correct theory, rejected/ignored the CSRF and role-prefix theories once the simpler explanation was confirmed. Good example of not accepting the first plausible-sounding explanation without testing it.

---

## 4. Remote debugging setup (JDWP over Docker)

**Prompt:** Asked how to expose a debug port in the Dockerfile so IntelliJ could attach to the running container instead of running the app locally outside Docker.

**AI output:** Gave the exact `-agentlib:jdwp=...` flag to add to the `ENTRYPOINT`, plus the `-p 5005:5005` run flag and IntelliJ Remote JVM Debug config steps.

**Validation:** Applied it myself, hit a real error (`port is already allocated`), diagnosed and fixed it myself using `docker ps` / `docker stop`, then successfully attached IntelliJ and set breakpoints — confirmed with screenshots showing execution actually pausing.

**Verdict:** Accepted the approach; the port conflict was my own environment issue, not an AI mistake, and I resolved it independently.

---

## 5. Adding Swagger/OpenAPI annotations to existing controllers

**Prompt:** Asked AI to add `@Tag` and `@Operation` annotations to two of my own controllers (`CustomerController`, `AuthController`), explicitly instructing it not to change my existing comments or spacing.

**AI output:** Returned the full files with only the new annotation lines and imports added.

**Validation:** Diffed the output against my originals mentally — confirmed no existing lines, comments, or spacing were altered, only new lines added exactly where expected.

**Verdict:** Accepted. Instruction to preserve my own code style was followed correctly.

---

## Ethics / security notes
- Never pasted real secrets into prompts knowingly — when an `application.yml` I shared turned out to contain a live-looking SendGrid password and SMS API key as default fallback values, AI flagged this back to me as a security concern rather than treating it as normal, and I avoided sharing that file further.
- All code pasted was either my own or a project I have legitimate access to for training purposes.

## Overall takeaway
AI was most useful for (a) fast orientation in a large unfamiliar codebase, (b) narrowing down plausible causes of an error before I tested them myself, and (c) mechanical code additions where I could clearly verify nothing unintended changed. In every case, I ran the actual command, read the actual response, and confirmed the fix myself before considering the task done — AI proposed, I verified.