# ===========================
# Stage 1: Dependencies
# ===========================
FROM maven:3.9.9-eclipse-temurin-17 AS deps
WORKDIR /app/gateway-service

# Copy Maven settings
COPY settings.xml /root/.m2/settings.xml

# Copy only pom.xml first (for dependency caching)
COPY gateway-service/gateway-service/pom.xml ./pom.xml

# Download dependencies (cached layer)
RUN --mount=type=cache,target=/root/.m2 \
    mvn dependency:go-offline -DskipTests || true

# ===========================
# Stage 2: Build Application
# ===========================
FROM deps AS builder

# Copy source code
COPY gateway-service/gateway-service/src ./src

# Build application
RUN --mount=type=cache,target=/root/.m2 \
    mvn clean package -DskipTests

# ===========================
# Stage 3: Runtime
# ===========================
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=builder /app/gateway-service/target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=30.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "app.jar"]
