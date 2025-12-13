# ===========================
# Stage 1: Build with Shared Libs
# ===========================
FROM proshop-shared-libs:latest AS deps
WORKDIR /app/sale-service

# Copy only pom.xml first (for dependency caching)
COPY sale-service/pom.xml ./pom.xml

# Download dependencies (cached layer)
RUN --mount=type=cache,target=/root/.m2 \
    mvn dependency:go-offline -DskipTests || true

# ===========================
# Stage 2: Build Application
# ===========================
FROM deps AS builder

# Copy source code
COPY sale-service/src ./src

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

COPY --from=builder /app/sale-service/target/*.jar app.jar

EXPOSE 8087

ENTRYPOINT ["java", \
    "-XX:+UseContainerSupport", \
    "-XX:MaxRAMPercentage=75.0", \
    "-Djava.security.egd=file:/dev/./urandom", \
    "-jar", "app.jar"]
