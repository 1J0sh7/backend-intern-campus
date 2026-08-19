# Stage 1 : Downloading the dependencies
# Use Java 21
FROM eclipse-temurin:21-jdk



# Set working directory
WORKDIR /app



# Stage 2 : Build the application
# Copy the JAR file
COPY build/libs/*.jar app.jar




# Expose port 8081
EXPOSE 8081




# Stage 3 : RUN the application
# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]