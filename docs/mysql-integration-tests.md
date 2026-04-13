# MySQL Integration Tests

These integration tests run with a real MySQL database and transactional rollback.

## Test profile

- Profile: `it-mysql`
- Config file: `src/test/resources/application-it-mysql.yml`
- Flyway location for tests: `src/test/resources/db/test-migration`

By default, the profile connects to:

- URL: `jdbc:mysql://localhost:3306/student_management_test?useSSL=false&allowPublicKeyRetrieval=true&createDatabaseIfNotExist=true`
- Username: `root`
- Password: `${DB_PASSWORD}` (fallback: `strongpassword123!`)
- Flyway history table: `flyway_schema_history_it`

You can override with:

- `IT_DB_URL`
- `IT_DB_USERNAME`
- `IT_DB_PASSWORD`

## Run locally

1. Start only MySQL:

   ```bash
   docker compose up -d mysql
   ```

2. Run integration tests:

   ```bash
   mvn -Dtest='*IntegrationTest' test
   ```

## Transaction behavior

- Shared integration base class uses `@Transactional`.
- Each test method is rolled back automatically.
- `AuthDatabaseFailureIntegrationTest` is explicitly non-transactional because it executes DDL (`DROP TABLE`).
