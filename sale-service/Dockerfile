# ===========================
# Stage 1: Build
# ===========================
FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy Maven settings (with mirror for faster download)
COPY ./settings.xml /root/.m2/settings.xml

# Copy và build exception-lib trước (vì auth-lib phụ thuộc vào nó)
COPY ./exception-lib /app/exception-lib
RUN mvn -f /app/exception-lib/pom.xml clean install -DskipTests

# Copy và build auth-lib sau
COPY ./auth-lib /app/auth-lib
RUN mvn -f /app/auth-lib/pom.xml clean install -DskipTests

# Copy mã nguồn sale-service
COPY ./sale-service /app/sale-service

# Build ứng dụng chính
WORKDIR /app/sale-service
RUN mvn clean package -DskipTests

# ===========================
# Stage 2: Run
# ===========================
FROM eclipse-temurin:17-jdk
WORKDIR /app

COPY --from=builder /app/sale-service/target/*.jar app.jar
EXPOSE 8087
ENTRYPOINT ["java", "-jar", "app.jar"]
