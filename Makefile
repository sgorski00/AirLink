.PHONY: logs

up:
	docker compose up -d

down:
	docker compose down

logs:
	docker compose logs -f

build:
	docker compose build

restart:
	docker compose restart

test:
	docker exec -it app mvn test

test-class:
	docker exec -it app mvn test -Dtest=${class}

psql:
	docker exec -it db psql -U root -d AirLink

shell:
	docker exec -it app sh

redis:
	docker exec -it redis redis-cli

flyway-clean:
	docker exec -it app mvn flyway:clean

flyway-migrate:
	docker exec -it app mvn flyway:migrate

flyway-info:
	docker exec -it app mvn flyway:info
