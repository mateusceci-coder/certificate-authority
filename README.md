## Fake-Bry — Docker setup

Run the full stack (Postgres + Spring Boot API + Nginx + Frontend) with Docker. Below are separate commands for development (hot-reload frontend) and production (optimized static build served by Nginx).

### Prerequisites

- Docker Desktop (or Docker Engine) and Docker Compose v2
- Open ports: 80 (Nginx), 8080 (API — dev only), 5173 (Frontend — dev only), 5432 (Postgres — dev only)

### 1) Environment variables

Create a `.env` file in the repository root (next to `compose.yaml`):

```env
POSTGRES_USER=app
POSTGRES_PASSWORD=app
POSTGRES_DB=app
```

Compose will automatically load these. Adjust values as needed.

### 2) Development

This uses both `compose.yaml` and `compose.override.yaml` (auto-merged by Docker Compose). It runs:

- Postgres with published port 5432
- Spring Boot API with published port 8080
- Vite dev server for the frontend on port 5173
- Nginx using a dev config that proxies `/` to the dev frontend and `/api/` to the backend

Start (build if needed):

```bash
docker compose up -d --build
```

Open:

- App: http://localhost
- Frontend (direct, dev server): http://localhost:5173
- API (direct, dev only): http://localhost:8080

Stop:

```bash
docker compose down
```

Start fresh (remove volumes, including database data):

```bash
docker compose down -v
```

### 3) Production

Production uses only `compose.yaml`. Nginx serves the built frontend and proxies `/api/` to the backend. Postgres and Spring Boot do not expose ports to the host by default.

Build and run:

```bash
docker compose -f compose.yaml up -d --build
```

Open:

- App: http://localhost

Stop:

```bash
docker compose -f compose.yaml down
```

Rebuild without cache:

```bash
docker compose -f compose.yaml build --no-cache
```

### Useful commands

- Follow logs for a service:
  ```bash
  docker compose logs -f springboot
  docker compose logs -f nginx
  docker compose logs -f postgres
  docker compose logs -f front    # dev only
  ```
- Rebuild a single service:

  ```bash
  # dev
  docker compose build springboot
  docker compose build nginx

  # prod (base file only)
  docker compose -f compose.yaml build springboot
  docker compose -f compose.yaml build nginx
  ```

### Services overview

- `postgres`: database with data stored in the `postgres_data` volume
- `certificates`: generates and shares certs via the `certs` volume
- `springboot`: Java API on port 8080 (exposed in dev); healthcheck at `/api/ca-info`
- `nginx`:
  - Dev: reverse-proxy to Vite dev server and backend
  - Prod: serves built static files and proxies `/api/` to backend
- `front` (dev only): Vite dev server with hot reload

### Endpoints (through Nginx)

- Frontend: `/`
- API: `/api/`

If port 80 is occupied, change the `ports` mapping for `nginx` in the relevant Compose file.
