# Stage 1: Build the JAR
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app
COPY . .

# Make gradlew executable and build the JAR
RUN chmod +x gradlew
RUN ./gradlew clean build -x test




# Stage 2: Run the JAR
FROM eclipse-temurin:21-jdk


# ininitialising work directory
WORKDIR /app


# Copy the built JAR from the builder stage
COPY --from=builder /app/build/libs/*.jar app.jar



# port mapping
EXPOSE 8081



#running the app
ENTRYPOINT ["java", "-jar", "app.jar"]