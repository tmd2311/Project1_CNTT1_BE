# ===========================
# Stage 1: Build (Maven)
# ===========================
FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /app

COPY ../../gateway-service/gateway-service /app/gateway-service
# Build ứng dụng chính
WORKDIR /app/gateway-service
RUN mvn clean package -DskipTests

# ===========================
# Stage 2: Run
# ===========================
FROM eclipse-temurin:17-jdk
WORKDIR /app

COPY --from=builder /app/gateway-service/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
