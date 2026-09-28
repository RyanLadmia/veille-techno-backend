# API Kanban (Spring Boot)

REST API for a **Kanban Board** application: users, lists, and cards.

Stack: **Spring Boot 4 / Java 25**, Spring Security + JWT Bearer, JPA/Hibernate, PostgreSQL.

## Interactive documentation

Once the server is running:

| Resource | URL |
|---|---|
| Swagger UI | [http://localhost:3310/api](http://localhost:3310/api) |
| Adminer (Postgres UI) | [http://localhost:8081](http://localhost:8081) (server `db`, user/db `kanban`) |

In Swagger: **Authorize** → paste the `accessToken` returned by `POST /api/auth/login` (the UI adds the `Bearer` prefix).

## Prerequisites

- Docker + Docker Compose
- (Optional) JDK 25 + Maven to run tests outside Docker

## Getting started

From the repository root:

```bash
# 1. Create a .env file (see variables below)
cp .env.example .env   # or create .env manually

# 2. Start Postgres + API + Adminer
docker compose up --build
```

The API listens on the port set by `APP_PORT` (e.g. `3310`).

The `app` container mounts the source code: Java changes reload the app via Spring DevTools (hot-reload), without a full rebuild each time.

Stop:

```bash
docker compose down
```

### Tests

```bash
cd api-kanban
./mvnw test
```

Integration tests use in-memory H2 (Postgres is not required).

## Environment variables

`.env` file at the repository root (read by Docker Compose):

| Variable | Required | Example | Description |
|---|---|---|---|
| `APP_PORT` | Yes | `3310` | HTTP port for the API (host and container) |
| `DB_PASSWORD` | Yes | `kanban_dev` | Postgres password (`POSTGRES_PASSWORD` + Spring datasource) |
| `JWT_SECRET` | No* | long secret | HMAC secret used to sign JWTs |
| `JWT_EXPIRATION_MS` | No | `3600000` | Token lifetime in ms (default: 1 hour) |
| `ADMINER_PORT` | No | `8081` | Adminer port on the host |

\*Without `JWT_SECRET`, Docker Compose falls back to a development default. **Change it outside local use.**

Variables injected into the `app` container (derived / fixed):

| Spring / app variable | Source |
|---|---|
| `SERVER_PORT` | `APP_PORT` |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://db:5432/kanban` |
| `SPRING_DATASOURCE_USERNAME` | `kanban` |
| `SPRING_DATASOURCE_PASSWORD` | `DB_PASSWORD` |
| `JWT_SECRET` / `JWT_EXPIRATION_MS` | `.env` |

Minimal `.env` example:

```env
APP_PORT=3310
DB_PASSWORD=kanban_dev
JWT_SECRET=mysecret-verylong-noone-shouldknows
JWT_EXPIRATION_MS=3600000
ADMINER_PORT=8081
```

## Authentication

1. `POST /api/auth/register` — sign up (`email`, `password`, `name`) → **201** + user **without** `password`
2. `POST /api/auth/login` — → **200** + `{ "accessToken": "<jwt>" }`
3. Protected routes: `Authorization: Bearer <jwt>` header

The JWT includes at least `sub` (user id) and `exp`. The token is returned **in the JSON body** (no httpOnly cookie).

## Implementation choices

### 403 vs 404 on resource ownership

If a list or card **exists** but belongs to another user, the API returns **403 Forbidden** (explicit message), **not** an opaque 404.

**404** is reserved for resources that truly do not exist (unknown id).

This follows the specification: we prefer a clear permission denial over hiding that the resource exists.

### Deleting a list and its cards

`DELETE /api/lists/{id}` **cascades** and deletes all cards in the list (`CascadeType.ALL` + `orphanRemoval` on `KanbanList.cards`).

Rejected alternative: refuse deletion when the list is not empty (409) — one client call is enough.

### Scoping of `GET /api/lists`

Returns only lists owned by the authenticated user. No third-party list is exposed → no 403 on this route.

### Roles

- `user` (default on registration) and `admin`
- Changing a user's `role` via `PATCH /api/users/{id}`: **admin** only (otherwise **403**)
- A user may update their own profile; a non-admin cannot update another user's profile (**403**)

### Password

Never exposed in any API response (`register`, `users/me`, `PATCH /users/{id}`, etc.). Stored hashed with BCrypt.

## Repository layout

```
.
├── api-kanban/          # Spring Boot application
├── docker-compose.yml   # Postgres + Adminer + app (dev)
├── openapi.yaml         # OpenAPI contract (source)
└── README.md
```

The copy served by Swagger is `api-kanban/src/main/resources/static/openapi.yaml` (keep it in sync with the root file).
