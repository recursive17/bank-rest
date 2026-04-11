# bank-rest

небольшой backend для управления банковскими картами.

что умеет:
- регистрация и вход (jwt)
- роли `admin` и `user`
- создание/блокировка/активация/удаление карт
- переводы между своими картами
- просмотр своих карт (поиск + пагинация)
- просмотр баланса
- админское управление пользователями

## стек

- java 17
- spring boot
- spring security + jwt
- spring data jpa
- postgresql
- liquibase
- maven

## быстрый запуск

1. подними базу:
```bash
docker compose up -d
```

2. запусти приложение:
```bash
mvn spring-boot:run
```

по умолчанию:
- api: `http://localhost:8080`
- postgresql: `localhost:5433`

## доступы и auth

при старте автоматически создается админ:
- `username: admin`
- `password: admin`

получить токен можно через:
- `POST /auth/sign-up`
- `POST /auth/sign-in`

дальше токен передаешь в заголовке:
```http
Authorization: Bearer <token>
```

## swagger и openapi

- swagger ui: `http://localhost:8080/swagger-ui.html`
- api docs json: `http://localhost:8080/api-docs`
- openapi файл в репе: `docs/openapi.yaml`

## тесты

запуск всех тестов:
```bash
mvn test
```

если нужно с нуля:
```bash
mvn clean test
```

## полезно знать

- миграции: `src/main/resources/db/migration`
- конфиг: `src/main/resources/application.yml`
