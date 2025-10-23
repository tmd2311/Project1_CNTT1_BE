# ===========================
# Stage 1: Build
# ===========================
FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy và cài các thư viện nội bộ trước
COPY exception-lib /app/exception-lib
RUN mvn -f /app/exception-lib/pom.xml clean install -DskipTests

COPY auth-lib /app/auth-lib
RUN mvn -f /app/auth-lib/pom.xml clean install -DskipTests

# Copy mã nguồn auth-service
COPY auth-service /app/auth-service

# Build ứng dụng chính
WORKDIR /app/auth-service
RUN mvn clean package -DskipTests

# ===========================
# Stage 2: Run
# ===========================
FROM eclipse-temurin:17-jdk
WORKDIR /app

COPY --from=builder /app/auth-service/target/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
