# Enterprise Procurement System — Backend Setup

**Owner:** Member 2 (Backend Setup)
**Milestone:** Milestone 1 (Weeks 1–2) — Environment Setup & Procurement Design

## What's included

- Spring Boot 3.3.4 Maven project (Java 17)
- `pom.xml` with: Web, Data JPA, MySQL driver, Validation, Security, JWT (jjwt), Swagger/OpenAPI, Lombok
- `EnterpriseApplication.java` — main entry point
- `application.properties` — MySQL connection (env-var overridable), JPA/Hibernate, Jackson, logging, Swagger, JWT config
- `config/` package:
  - `AppConfig.java` — password encoder bean
  - `CorsConfig.java` — allows the React frontend (Member 4, default `localhost:3000`) to call the API
  - `SecurityConfig.java` — baseline security chain (currently permissive — **must be tightened once AuthController/AuthService are built**)
  - `SwaggerConfig.java` — OpenAPI docs at `/swagger-ui.html`
- `controller/HealthController.java` — `GET /api/health` sanity check that confirms the app boots and the DB connection works
- Full package skeleton matching the agreed folder structure (`entity`, `repository`, `service`, `dto`, `exception`, `security`, `util`, `enums`) so other members can drop files straight in without restructuring

## Before you run it

1. **Install MySQL locally** (or point at a shared dev instance) and make sure it's running on `localhost:3306`.
2. Set credentials as environment variables (don't hardcode real passwords):
   ```
   export DB_USERNAME=root
   export DB_PASSWORD=yourpassword
   ```
   If unset, it falls back to `root` / `root`.
3. The app will auto-create the `procurement_db` schema on first run (`createDatabaseIfNotExist=true`), and `ddl-auto=update` will create tables from JPA entities as Member 3/others add them. **Coordinate with Member 3** — once their SQL scripts are the source of truth, switch `ddl-auto` to `validate` or `none` so Hibernate doesn't fight with hand-written schema/constraints.

## Running the project

Maven wrapper (`mvnw`) isn't included in this handoff — generate it locally with:
```bash
mvn -N wrapper:wrapper
```
(requires Maven installed once, just to bootstrap the wrapper). After that, anyone can run `./mvnw spring-boot:run` without installing Maven themselves.

Or, if you already have Maven installed:
```bash
mvn spring-boot:run
```

## Verifying it works

Once running, hit:
- `http://localhost:8080/api/health` → should return `{"status":"UP","database":"CONNECTED",...}`
- `http://localhost:8080/swagger-ui.html` → API docs UI (empty until controllers are added)

## Handoff notes for the team

- **Member 3 (Database):** entity classes go in `entity/`, repositories in `repository/` — Spring Data JPA repositories just need to extend `JpaRepository<Entity, IdType>`. Let's sync on whether Hibernate (`ddl-auto`) or your SQL scripts own table creation.
- **Member 4 (Frontend):** API will run on `http://localhost:8080`, CORS is currently opened for `http://localhost:3000`. Let me know if your dev server runs elsewhere.
- **Member 1 (Project/API docs):** Swagger/OpenAPI is wired up at `/swagger-ui.html` for auto-generated API docs as controllers get built out.
- **Security:** `SecurityConfig` currently permits all requests so nobody's blocked during early development. This needs to be locked down once `AuthController`/`AuthService` (JWT) are implemented — flagging this now so it doesn't get shipped as-is.
