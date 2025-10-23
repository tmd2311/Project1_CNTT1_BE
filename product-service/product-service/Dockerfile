# ===========================
# Stage 1: Build
# ===========================
FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy và cài các thư viện nội bộ trước (exception-lib, auth-lib)
COPY ../../exception-lib /app/exception-lib
RUN mvn -f /app/exception-lib/pom.xml clean install -DskipTests

COPY ../../auth-lib /app/auth-lib
RUN mvn -f /app/auth-lib/pom.xml clean install -DskipTests

# Copy mã nguồn product-service
COPY ../../product-service/product-service /app/product-service

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
