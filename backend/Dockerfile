# Stage 1: Build
FROM maven:3.9-eclipse-temurin-17 AS build
WORKDIR /app

# Copy pom.xml and download dependencies
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source code and build
COPY src ./src
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Install wget for health check
RUN apk add --no-cache wget

# Create non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copy built application
COPY --from=build --chown=appuser:appgroup /app/target/quarkus-app/lib/ /app/lib/
COPY --from=build --chown=appuser:appgroup /app/target/quarkus-app/*.jar /app/
COPY --from=build --chown=appuser:appgroup /app/target/quarkus-app/app/ /app/app/
COPY --from=build --chown=appuser:appgroup /app/target/quarkus-app/quarkus/ /app/quarkus/

# Expose port (Cloud Run usa 8080, mas pode ser configurado via PORT)
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:${PORT:-8080}/health || exit 1

# Run the application
ENTRYPOINT ["java", "-jar", "/app/quarkus-run.jar"]
