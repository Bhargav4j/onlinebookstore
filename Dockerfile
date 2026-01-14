=== Dockerfile ===
# Multi-stage build for Java Servlet WAR application
# Build stage
FROM maven:3.9.4-eclipse-temurin-8 AS builder

WORKDIR /workspace

# Copy pom.xml first for dependency caching
COPY pom.xml .

# Download dependencies (cache layer)
RUN mvn dependency:go-offline -B

# Copy source code and WebContent
COPY src ./src
COPY WebContent ./WebContent

# Build the WAR file and copy webapp-runner
RUN mvn clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:8-jdk

WORKDIR /app

# Create non-root user for security
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Copy WAR file and webapp-runner from builder
COPY --from=builder /workspace/target/onlinebookstore.war /app/onlinebookstore.war
COPY --from=builder /workspace/target/dependency/webapp-runner.jar /app/webapp-runner.jar

# Set ownership
RUN chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

# Expose application port
EXPOSE 8080

# Environment variables for JVM tuning
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"
ENV TZ=UTC

# Run webapp-runner with the WAR file
CMD java $JAVA_OPTS -jar webapp-runner.jar --port 8080 onlinebookstore.war
