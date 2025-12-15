# ProShop Backend Monorepo

Backend monorepo for the ProShop e-commerce platform. It contains multiple Spring Boot microservices, shared libraries, and tooling to run locally or in Docker.

## Services
- **auth-service**: Authentication, OAuth2 login, user management.
- **order-service**: Orders, payments, order detail.
- **product-service**: Products, SKU, categories, brands.
- **sale-service**: Voucher & sale management, voucher validation/apply.
- **review-service**: Product reviews and Q&A.
- **file-service**: File upload/download.
- **gateway-service**: API gateway / routing.
- **flyway-migration-service**: Database migrations.
- **shared libs**: `auth-lib`, `exception-lib` packaged into `proshop-shared-libs` image.

## Tech Stack
- Java 17, Spring Boot 3.x, Spring Cloud OpenFeign, Spring Security
- PostgreSQL, Redis, Consul
- Docker & docker-compose
- Flyway for DB migrations

## Quick Start (Docker)
1) Build shared libs once:
```bash
docker build -t proshop-shared-libs:latest -f Dockerfile.shared-libs .
```
2) Start all services:
```bash
docker-compose up -d
```
3) Access:
- Gateway: `http://localhost:8080`
- Auth-service: `http://localhost:8081`
- Product-service: `http://localhost:8082`
- Order-service: `http://localhost:8083`
- File-service: `http://localhost:8084`
- Review-service: `http://localhost:8086`
- Sale-service: `http://localhost:8087`
- Consul UI: `http://localhost:8500`

## Running a single service locally
Example for auth-service:
```bash
cd auth-service
mvn spring-boot:run
```
You may need to set env vars (see below) and ensure dependent services/DBs are reachable.

## Environment Variables (commonly used)
- `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`
- `SPRING_REDIS_HOST`, `SPRING_REDIS_PORT`
- `SPRING_CLOUD_CONSUL_HOST`, `SPRING_CLOUD_CONSUL_PORT`
- `FILE_UPLOAD_DIR`, `FILE_BASE_URL` (file-service)
- `PRODUCT_SERVICE_URL`, `ORDER_SERVICE_URL`, `FILE_SERVICE_URL` (client calls)
- `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET`, `MAIL_PASSWORD` (auth-service)

## Migrations
Flyway scripts live under `flyway-migration-service/src/main/resources/db/migration/*`. Ensure DB URLs are configured before running services so migrations execute on startup.

## Notes
- Services run as non-root users in containers; volume directories (e.g., `./file-storage` for uploads) need write permission.
- Voucher apply flow updates both voucher usage in sale-service and discount/total in order-service via internal API.

## Build
```bash
mvn clean package -DskipTests
```

## Useful scripts
- `build.sh` – build shared libs and services (wrapper around Maven/Docker)
- `start.sh` / `stop.sh` – start/stop docker-compose stack
- `reset.sh` – clean and rebuild images/containers (use with care)
- Flyway migrations: run via `flyway-migration-service`
  ```bash
  mvn -pl flyway-migration-service spring-boot:run
  # hoặc chạy image đã build của flyway-migration-service
  ```

