# ===========================
# Stage 1: Build with Shared Libs
# Sử dụng base image đã có exception-lib và auth-lib
# ===========================
FROM proshop-shared-libs:latest AS deps
WORKDIR /app/auth-service

# Copy only pom.xml first (for dependency caching)
COPY auth-service/pom.xml ./pom.xml

# Download dependencies (cached layer)
RUN --mount=type=cache,target=/root/.m2 \
    mvn dependency:go-offline -DskipTests || true

# ===========================
# Stage 2: Build Application
# ===========================
FROM deps AS builder

# Copy source code
COPY auth-service/src ./src

# Build application
# Không dùng cache mount để Maven có thể tìm thấy shared libs từ base image
RUN mvn clean package -DskipTests

# ===========================
# Stage 3: Runtime
# ===========================
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

COPY --from=builder /app/auth-service/target/*.jar app.jar

EXPOSE 8081

# Use exec form for proper signal handling
ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "app.jar"]
