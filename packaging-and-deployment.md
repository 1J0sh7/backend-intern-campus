# Packaging and Deployment Guide

## Build the Application

```bash
./gradlew clean build

```
# Run the JAR
```bash
java -jar build/libs/backend-intern-campus-1.0.0.jar
```
## Run with Different Profiles
# Dev Profile
``` bash
java -jar build/libs/backend-intern-campus-1.0.0.jar --spring.profiles.active=dev
```
## Prod Profile
``` bash
java -jar build/libs/backend-intern-campus-1.0.0.jar --spring.profiles.active=prod
```
## Configuration Files
``` File	Purpose
application.yml	Main config
application-dev.yml	-- Dev environment
application-prod.yml	-- Production environment



Dependencies

Library	Purpose
JUnit Jupiter	- Testing
Commons Lang3	- String utilities