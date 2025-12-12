# ===========================
# Stage 1: Build
# ===========================
FROM maven:3.9.9-eclipse-temurin-17 AS builder
WORKDIR /app

# Copy Maven settings (with mirror for faster download)
COPY ./settings.xml /root/.m2/settings.xml

# Copy và build exception-lib trước (vì auth-lib phụ thuộc vào nó)
COPY ./exception-lib /app/exception-lib
RUN --mount=type=cache,target=/root/.m2 \
    mvn -f /app/exception-lib/pom.xml clean install -DskipTests

# Copy và build auth-lib sau
COPY ./auth-lib /app/auth-lib
RUN --mount=type=cache,target=/root/.m2 \
    mvn -f /app/auth-lib/pom.xml clean install -DskipTests

# Copy mã nguồn review-service
COPY ./review-service /app/review-service

# Build ứng dụng chính (có repackage)
WORKDIR /app/review-service
RUN --mount=type=cache,target=/root/.m2 \
    mvn clean package spring-boot:repackage -DskipTests

# ===========================
# Stage 2: Run
# ===========================
FROM eclipse-temurin:17-jdk
WORKDIR /app

COPY --from=builder /app/review-service/target/*.jar app.jar
EXPOSE 8086
ENTRYPOINT ["java", "-jar", "app.jar"]
