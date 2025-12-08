# ===========================
# 🏗️ Stage 1: Build Application
# ===========================
FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /app

# 👉 Mount cache Maven để reuse thư viện
VOLUME ["/root/.m2"]

# Copy thư viện nội bộ
COPY ../../exception-lib /app/exception-lib
COPY ../../auth-lib /app/auth-lib

# Cài các thư viện nội bộ vào local repo ( chỉ chạy nếu các lib thay đổi)
RUN mvn -f /app/exception-lib/pom.xml install -DskipTests
RUN mvn -f /app/auth-lib/pom.xml install -DskipTests

# Copy service chính
COPY ../../file-service /app/file-service
WORKDIR /app/file-service
RUN mvn clean package spring-boot:repackage -DskipTests

# ===========================
# 🚀 Stage 2: Run Application
# ===========================
FROM eclipse-temurin:17-jdk-alpine
WORKDIR /app
COPY --from=builder /app/file-service/target/*.jar app.jar
EXPOSE 8084
ENV SPRING_PROFILES_ACTIVE=docker
ENTRYPOINT ["java", "-jar", "app.jar"]
