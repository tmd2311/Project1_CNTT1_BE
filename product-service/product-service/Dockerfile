# ===========================
# Stage 1: Build
# ===========================
FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /app

# 👉 Mount cache Maven để reuse thư viện
VOLUME ["/root/.m2"]

# Copy thư viện nội bộ
COPY ./exception-lib /app/exception-lib
COPY ./auth-lib /app/auth-lib

# Cài các thư viện nội bộ vào local repo ( chỉ chạy nếu các lib thay đổi)
RUN mvn -f /app/exception-lib/pom.xml install -DskipTests
RUN mvn -f /app/auth-lib/pom.xml install -DskipTests

# Copy mã nguồn product-service
COPY ./product-service/product-service /app/product-service

# Build ứng dụng chính (có repackage)
WORKDIR /app/product-service
RUN mvn clean package spring-boot:repackage -DskipTests

# ===========================
# Stage 2: Run
# ===========================
FROM eclipse-temurin:17-jdk
WORKDIR /app

COPY --from=builder /app/product-service/target/*.jar app.jar
EXPOSE 8082
ENTRYPOINT ["java", "-jar", "app.jar"]
