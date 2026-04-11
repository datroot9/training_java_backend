# Stage 1: Build the application securely using a Maven image
FROM maven:3.9.6-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy essentially just the POM first to cache dependencies (improves build speed for future runs)
COPY pom.xml .
# Download dependencies
RUN mvn dependency:go-offline -B

# Now copy the actual source code and build the fat jar
COPY src ./src
# Build the jar, skipping tests to make the image build much faster
RUN mvn clean package -DskipTests

# Stage 2: Create the final lightweight runtime image
FROM eclipse-temurin:17-jre
WORKDIR /app

# Copy the built jar from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the standard Spring Boot port
EXPOSE 8080

# The command that starts the Spring Boot application
ENTRYPOINT ["java", "-jar", "app.jar"]
