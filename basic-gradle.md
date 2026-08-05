# Basic Gradle Guide

## What is Gradle?

Gradle is a build automation tool. It compiles code, runs tests, packages applications, and manages dependencies.


**Simple definition:** Gradle automates everything between writing code and running it.

---

## Gradle Wrapper

The Gradle Wrapper allows you to run Gradle without installing it.

| File | Purpose |
|------|---------|
| `gradlew.bat` | Runs Gradle on Windows |
| `gradlew` | Runs Gradle on Mac/Linux |
| `gradle-wrapper.properties` | Specifies which Gradle version to use |

### Why Use the Wrapper?

| Reason | Explanation |
|--------|-------------|
| No installation needed | Downloads Gradle automatically |
| Version consistency | Everyone uses the same version |
| Project-specific | Version is controlled by the project |

---

## Gradle Build Lifecycle

| Phase | What Happens |
|-------|--------------|
| **Initialization** | Gradle determines which projects to build |
| **Configuration** | Build files are evaluated |
| **Execution** | Tasks are run in order |

---

## Key Gradle Files

### 1. `settings.gradle`

```groovy
rootProject.name = 'backend-intern-campus'