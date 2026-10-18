Task Manager API

A RESTful task management API built with Java 21, Spring Boot, Spring Security (JWT), Spring Data JPA, MySQL and Flyway.
Users register, log in, and can only see and change their own tasks.

## Features
- Register and login with BCrypt-hashed passwords
- Stateless JWT authentication (HS256, `Authorization: Bearer <token>`)
- Per-user tasks: a user can never read, change or delete another user's task (they get 404)
- CRUD endpoints under `/api/tasks` with correct HTTP status codes (201, 204, 401, 404, 409, 400)
- Request/response DTOs (JPA entities are never exposed, the password hash never leaves the server)
- Bean Validation with field-level error messages
- Global exception handling returning RFC 7807 `ProblemDetail` JSON
- Database schema managed by Flyway migrations (Hibernate only validates)
- Unit tests (JUnit 5 + Mockito) and web-layer tests (`@WebMvcTest` with the real security config)

## Run locally

1. Start MySQL and make sure you can log in.
2. Set the required environment variables:

```bash
export DB_PASSWORD=your_mysql_password
export JWT_SECRET=$(openssl rand -base64 32)   # at least 32 characters
# optional overrides
export DB_URL="jdbc:mysql://localhost:3306/taskmanager?createDatabaseIfNotExist=true"
export DB_USERNAME=root
export JWT_EXPIRATION_MINUTES=60
```

   In IntelliJ: Run > Edit Configurations > Environment variables. Keep `JWT_SECRET` the same between
   restarts, otherwise tokens issued earlier stop working.

3. Start the app:

```bash
./mvnw spring-boot:run
```

4. Run the tests (no database needed):

```bash
./mvnw test
```Errors look like:

```json
{
  "title": "Invalid request",
  "status": 400,
  "detail": "Validation failed",
  "errors": { "password": "password must be 8 to 72 characters" }
}
```
How authentication works
1. `POST /api/auth/register` stores the user with a BCrypt hash of the password.
2. `POST /api/auth/login` checks the password and returns a signed JWT. Its `sub` claim is the user id.
3. For every other request, Spring Security's resource-server filter verifies the token signature and
   expiry. The controller reads the user id from the verified token (never from the request body)
   and the service only queries tasks with that owner id.
   Database migrations
- `V1__create_task_table.sql` creates the `task` table
- `V2__create_users_and_task_owner.sql` creates `users` and links each task to its owner

Never edit a migration that has already run. Add a new `V3__...sql` instead.
