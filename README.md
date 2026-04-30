# Memorizy Server

Spring Boot-бэкенд для Android-приложения Memorizy. Он обеспечивает аутентификацию, удалённое хранение учебных наборов, карточек и учебных сессий, а также REST-эндпоинты, используемые мобильным клиентом для удаленного хранения.

## Технологический стек

* Kotlin
* Spring Boot
* Spring Security с JWT
* Spring Data JPA
* PostgreSQL
* Docker Compose для локального запуска базы данных

## Конфигурация

Для локальной разработки скопируйте `.env.example` в `.env` и замените значения-заглушки:

```powershell
Copy-Item .env.example .env
```

Обязательные значения для production-среды:

* `DB_PASSWORD`
* `JWT_SECRET`

`JWT_SECRET` должен быть Base64-кодированным ключом, подходящим для HS256.

Пример генерации локального секрета:

```powershell
[Convert]::ToBase64String([byte[]](1..32 | ForEach-Object { Get-Random -Maximum 256 }))
```

## Локальный запуск

```powershell
docker compose up -d
.\gradlew.bat bootRun
```

Сервер слушает порт из `SERVER_PORT`, по умолчанию — `8080`.

## Production-запуск в Docker

На VPS установите Docker и выполните:

```bash
cp .env.example .env
nano .env
docker compose -f docker-compose.prod.yaml up -d --build
```

Production Compose-файл запускает PostgreSQL и Spring Boot-приложение. Приложение публикуется на порту `80` и подключается к PostgreSQL через внутреннюю Docker-сеть.

Проверка логов:

```bash
docker compose -f docker-compose.prod.yaml logs -f app
```

## Тесты

```powershell
.\gradlew.bat test
```
