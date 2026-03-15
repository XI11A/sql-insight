# SQL Insight Example Application

This is a sample Spring Boot application demonstrating the features of SQL Insight.

## Features Demonstrated

1. **Dashboard**: Accessible at `http://localhost:8080/sql-insight`.
2. **Annotations**:
   - `GET /api/products`: Uses `@QueryTrace`
   - `GET /api/products/{id}`: Uses `@QueryLabel`
   - `GET /api/products/slow`: Uses `@SlowQueryAlert`
   - `POST /api/products`: Uses `@DisableQueryTracking`
   - `GET /api/products/n-plus-one`: Demonstrates N+1 pattern detection.

## How to Run

1. Navigate to the `examples` directory or run from the root:
   ```bash
   mvn spring-boot:run -pl examples
   ```
2. Interact with the endpoints using `curl` or a web browser.
3. Open the dashboard at `http://localhost:8080/sql-insight`.

## Configuration

Settings can be found in `src/main/resources/application.yml`.
By default, debug logging for `io.sqlinsight` is enabled to show query capture in the console.
