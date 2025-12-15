# ===========================
# Stage 1: Build with Shared Libs
# ===========================
FROM proshop-shared-libs:latest AS deps
WORKDIR /app/file-service

# Copy only pom.xml first (for dependency caching)
COPY file-service/pom.xml ./pom.xml

# Download dependencies (cached layer)
RUN --mount=type=cache,target=/root/.m2 \
    mvn dependency:go-offline -DskipTests || true

# ===========================
# Stage 2: Build Application
# ===========================
FROM deps AS builder

# Copy source code
COPY file-service/src ./src

# Build application
# Không dùng cache mount để Maven có thể tìm thấy shared libs từ base image
RUN mvn clean package spring-boot:repackage -DskipTests

# ===========================
# Stage 3: Runtime
# ===========================
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Create non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring

# Install su-exec for switching users
RUN apk add --no-cache su-exec

# Create uploads directory
RUN mkdir -p /uploads/images && \
    chmod -R 777 /uploads

# Copy entrypoint script (as root to set permissions)
COPY file-service/docker-entrypoint.sh /docker-entrypoint.sh
RUN chmod +x /docker-entrypoint.sh

COPY --from=builder /app/file-service/target/*.jar app.jar

EXPOSE 8084

# Run entrypoint as root to fix permissions, then switch to spring user
ENTRYPOINT ["/docker-entrypoint.sh"]
CMD ["java", "-XX:+UseContainerSupport", "-XX:MaxRAMPercentage=30.0", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]
