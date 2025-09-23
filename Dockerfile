# Multi-stage Dockerfile to build and run the backend located in tarefas-backend/

# ===== Build stage =====
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy only the backend pom first for better layer caching
COPY tarefas-backend/pom.xml ./pom.xml

# Pre-fetch dependencies
RUN mvn -q -DskipTests dependency:go-offline

# Copy backend sources
COPY tarefas-backend/src ./src

# Build the application
RUN mvn -q -DskipTests clean package

# ===== Runtime stage =====
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app

# Copy the built jar from the build stage
COPY --from=build /app/target/tarefas-1.0-SNAPSHOT.jar app.jar

# Expose application port
EXPOSE 8080

# Default profile; Render will inject env vars as needed
ENV SPRING_PROFILES_ACTIVE=production

# Run the application
ENTRYPOINT ["java", "-jar", "/app/app.jar"]


