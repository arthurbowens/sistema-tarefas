# Use Maven with OpenJDK 17
FROM maven:3.9.5-openjdk-17-slim

# Set working directory
WORKDIR /app

# Copy pom.xml first for better caching
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY src/ src/

# Build the application
RUN mvn clean package -DskipTests

# Use OpenJDK runtime
FROM eclipse-temurin:17-jre

# Set working directory
WORKDIR /app

# Copy the built jar
COPY --from=0 /app/target/tarefas-1.0-SNAPSHOT.jar app.jar

# Expose port
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]
