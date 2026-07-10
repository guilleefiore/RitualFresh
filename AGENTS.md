# AGENTS.md

## Repo Shape

- Two app roots: `backend/` is Java 21/Spring Boot 3.5/Maven; `frontend/` is React 19/Vite/npm. There is no root package/workspace manifest.
- Backend source is organized by module under `backend/src/main/java/com/ritualfresh/`: `auth`, `profiles`, `admin`, `notifications`, plus cross-cutting `shared` config/security/exception/service code.
- Frontend route wiring lives in `frontend/src/app/router.jsx`; feature code lives under `frontend/src/modules/{auth,admin,profiles}`; shared API/guards live under `frontend/src/shared`.

## Commands

- Backend tests: from `backend/`, run `mvn test`.
- Backend focused test: from `backend/`, run `mvn -Dtest=UserServiceTest test` or replace the class name.
- Backend dev server: start PostgreSQL first with `docker compose up -d postgres`, then from `backend/` run `mvn spring-boot:run`.
- Local dev shortcut: from repo root, run `./scripts/start-dev.sh` to start PostgreSQL/backend with Docker Compose and frontend with Vite; logs go under `.opencode/logs/`.
- Full local stack: from repo root, run `docker compose up --build`.
- Frontend setup/build: from `frontend/`, run `npm install` then `npm run build`; lockfile is `package-lock.json`.
- Frontend dev server: from `frontend/`, run `npm run dev` (Vite serves on `0.0.0.0:5173`).
- Frontend has no configured lint, formatter, or test scripts; do not invent `npm test`/`npm run lint` as verification.

## Runtime And Env

- Docker Compose reads root `.env` automatically; `.env.example` documents DB, SMTP/Mailtrap, and URL variables. Google OAuth uses `RITUALFRESH_GOOGLE_CLIENT_ID` / `RITUALFRESH_GOOGLE_CLIENT_SECRET` from Compose/application config.
- Backend defaults target local PostgreSQL at `jdbc:postgresql://localhost:5432/ritualfresh`; Docker uses service host `postgres`.
- `application.properties` currently enables admin bootstrap by default and seeds `admin@ritualfresh.local` / `AdminPassword123!` unless changed in config.
- Uploaded profile photos are stored at `/app/uploads` in Docker volume `ritualfresh_uploads` and served via `/uploads/**`.
- Frontend API base URL defaults to `http://localhost:8080`; override with `VITE_API_BASE_URL`.

## Auth And API Gotchas

- The app does not use JWT or `HttpSession`; backend persists opaque `UserSession` rows and sends `RITUALFRESH_SESSION` as an `HttpOnly` cookie.
- Frontend API calls must send `credentials: 'include'`; `apiClient.js` centralizes JSON requests, while multipart upload currently uses local `fetch` inside `profileService.js`.
- Backend still accepts `Authorization: Bearer <sessionToken>` for tests/manual debugging, but cookie auth is the primary app path.
- Auth state is currently rehydrated on frontend startup through `GET /api/users/me`; older docs that say hard reload requires logging in again are stale.
- Protected backend routes are enforced in `SecurityConfig`: `/api/admin/**` requires `ADMIN`, `/api/profiles/clientes/**` requires `CLIENT`, `/api/profiles/trabajadores/**` requires `WORKER`, and most other `/api/**` routes require authentication.

## Testing Notes

- Backend unit/security tests use in-memory repositories and `WebMvcTest` fixtures; they do not require the Docker PostgreSQL service.
- Manual end-to-end auth/email flows are documented in `docs/testing/TESTING_STRATEGY.md`; Mailtrap is only needed when validating real email delivery.
- `SecurityIntegrationTest` covers cookie auth, Bearer compatibility, expired/closed sessions, and role-based access; update it when changing `SecurityConfig` or session handling.

## Frontend Conventions

- Visual changes must start from `frontend/docs/UI_GUIDELINES.md`; it is the required UI reference even though some existing CSS still uses older colors/type.
- Keep HTTP calls in module `services` files and route protection in `ProtectedRoute`; do not scatter raw API calls through pages.
- `/profiles` is one protected screen serving both `CLIENT` and `WORKER`; role-specific fields are selected inside the page, not by separate routes.

## Docs To Trust

- When behavior, env/config, commands, auth/security rules, or business rules change, update the relevant docs in the same change.
- Prefer executable config over prose when they disagree. Known current example: frontend auth rehydration is implemented in code despite stale notes in profile/testing docs.
- High-value project docs: root `README.md`, `docs/development/IMPLEMENTATION_GUIDE.md`, `docs/testing/TESTING_STRATEGY.md`, and `frontend/docs/UI_GUIDELINES.md`.
