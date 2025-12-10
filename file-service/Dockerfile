# ===========================
# 🏗️ Stage 1: Build Application
# ===========================
FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy và build exception-lib trước (vì auth-lib phụ thuộc vào nó)
COPY ./exception-lib /app/exception-lib
RUN mvn -f /app/exception-lib/pom.xml clean install -DskipTests

# Copy và build auth-lib sau
COPY ./auth-lib /app/auth-lib
RUN mvn -f /app/auth-lib/pom.xml clean install -DskipTests

# Copy service chính
COPY ./file-service /app/file-service
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
