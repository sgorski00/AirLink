# AirLink

## Description

AirLink is a flight reservation REST API.

---

## Getting Started

Make sure you have Docker and Docker Compose installed.

### Available commands (Makefile):

| Command         | Description (EN)                   |
|-----------------|----------------------------------|
| `make up`       | Start all containers in background |
| `make down`     | Stop all containers              |
| `make logs`     | Show logs from all containers   |
| `make build`    | Build Docker images              |
| `make restart`  | Restart containers              |
| `make test`     | Run all tests inside app container |
| `make test-class class=ClassName` | Run specific test class inside app container |
| `make psql`     | Connect to PostgreSQL database shell |
| `make shell`    | Open shell inside app container |
| `make redis`    | Open Redis CLI                  |
| `make flyway-clean`  | Clean database using Flyway   |
| `make flyway-migrate` | Run database migrations Flyway |
| `make flyway-info`    | Show Flyway migration info  |

---

## Notes

- The API is currently under development.
- Use `make up` to start the environment before development.

---

## Requirements

- Docker