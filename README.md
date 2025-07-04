![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white)
[![codecov](https://codecov.io/gh/sgorski00/AirLink/graph/badge.svg?token=8CS976QYZG)](https://codecov.io/gh/sgorski00/AirLink)

# AirLink

## Description

AirLink is a flight reservation REST API written in Java 21 and Spring Boot 3.5.0.

## Live demo

App is available here:  
[https://airlink-zsy6.onrender.com/](https://airlink-zsy6.onrender.com/)

## API Documentation

Swagger UI is available at:
[https://airlink-zsy6.onrender.com/docs](https://airlink-zsy6.onrender.com/docs)

Or locally:
[http://localhost:8080/docs](http://localhost:8080/docs) 

## Technoliges used
- Java 21 + Spring Boot 3.5.0
- PostgreSQL
- Redis
- Flyway
- Docker
- Maven
- JUnit 5 + Mockito

---

## Getting Started

Make sure you have Docker and Docker Compose installed. 
Before installation, ensure that the `.env` file is filled out correctly.

To run application locally simply run `make up`.

### Important commands (Makefile):

| Command                           | Description                                  |
|-----------------------------------|----------------------------------------------|
| `make up`                         | Start all containers in background           |
| `make down`                       | Stop all containers                          |
| `make logs`                       | Show logs from all containers                |
| `make build`                      | Build Docker images                          |
| `make restart`                    | Restart containers                           |
| `make test`                       | Run all tests inside app container           |
| `make test-class class=ClassName` | Run specific test class inside app container |
| `make psql`                       | Connect to PostgreSQL database shell         |
| `make shell`                      | Open shell inside app container              |
| `make redis`                      | Open Redis CLI                               |
| `make flyway-migrate`             | Run database migrations Flyway               |

## Environment Variables

Application requires the following environment variables to run properly (e.g. in `.env.example` file):

```env
# Datasource
SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/AirLink
SPRING_DATASOURCE_USERNAME=root
SPRING_DATASOURCE_PASSWORD=secret

# Mail
SPRING_MAIL_HOST=mailhog
SPRING_MAIL_PORT=1025
SPRING_MAIL_USERNAME=support@airlink.com
SPRING_MAIL_PASSWORD=secret
SPRING_MAIL_STARTTLS_ENABLE=false
SPRING_MAIL_AUTH_ENABLE=false

# Redis
SPRING_REDIS_HOST=redis
SPRING_REDIS_PORT=6379

# JWT
JWT_SECRET_KEY=your-secret-key-in-hs256
JWT_EXPIRATION_TIME=86400000
```

---

## Author
[sgorski00](https://github.com/sgorski00)
