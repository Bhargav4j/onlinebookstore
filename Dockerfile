# Stage 1: Build application
FROM maven:3.8.6-openjdk-8-slim AS builder

# Set working directory
WORKDIR /workspace

# Copy pom.xml first for dependency caching
COPY pom.xml .

# Download dependencies (cached layer)
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src
COPY WebContent ./WebContent

# Build WAR file
RUN mvn clean package -DskipTests -B

# Stage 2: Runtime
FROM eclipse-temurin:8-jre-alpine

# Set working directory
WORKDIR /app

# Create non-root user
RUN addgroup -g 1001 -S appuser && \
    adduser -u 1001 -S appuser -G appuser

# Copy WAR file and webapp-runner from builder
COPY --from=builder /workspace/target/onlinebookstore.war /app/app.war
COPY --from=builder /workspace/target/dependency/webapp-runner.jar /app/webapp-runner.jar

# Set ownership
RUN chown -R appuser:appuser /app

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8080

# Set environment variables
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0" \
    TZ=UTC

# Run application using webapp-runner
CMD java $JAVA_OPTS -jar webapp-runner.jar --port 8080 app.war