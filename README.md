# Memorizy Server

Spring Boot backend for the Memorizy Android application. It provides authentication, remote storage for study sets, cards, learning sessions, and REST endpoints used by the mobile synchronization layer.

## Tech Stack

- Kotlin
- Spring Boot
- Spring Security with JWT
- Spring Data JPA
- PostgreSQL
- Docker Compose for local database setup

## Configuration

Copy `.env.example` to `.env` for local development and replace placeholder values:

```powershell
Copy-Item .env.example .env
```

Required production values:

- `DB_PASSWORD`
- `JWT_SECRET`

`JWT_SECRET` must be a Base64-encoded key suitable for HS256.

Example for generating a local secret:

```powershell
[Convert]::ToBase64String([byte[]](1..32 | ForEach-Object { Get-Random -Maximum 256 }))
```

## Local Run

```powershell
docker compose up -d
.\gradlew.bat bootRun
```

The server listens on `SERVER_PORT`, default `8080`.

## Production Docker Run

On a VPS, install Docker and run:

```bash
cp .env.example .env
nano .env
docker compose -f docker-compose.prod.yaml up -d --build
```

The production Compose file starts PostgreSQL and the Spring Boot app. The app is published on port `80` and connects to PostgreSQL through the internal Docker network.

Check logs:

```bash
docker compose -f docker-compose.prod.yaml logs -f app
```

## Tests

```powershell
.\gradlew.bat test
```
