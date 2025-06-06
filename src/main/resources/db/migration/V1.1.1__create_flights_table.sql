CREATE TABLE IF NOT EXISTS airports (
    id BIGSERIAL PRIMARY KEY,
    city_id BIGINT NOT NULL REFERENCES cities (id),
    icao VARCHAR(5) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS airplanes (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    code VARCHAR(4) NOT NULL UNIQUE,
    seats INT NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    deleted_at TIMESTAMP WITHOUT TIME ZONE
);

CREATE TABLE IF NOT EXISTS flights (
    id BIGSERIAL PRIMARY KEY,
    airplane_id BIGINT NOT NULL REFERENCES airplanes (id),
    airport_from_id BIGINT NOT NULL REFERENCES airports (id),
    airport_to_id BIGINT NOT NULL REFERENCES airports (id),
    departure TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    arrival TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    price DOUBLE PRECISION NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    deleted_at TIMESTAMP WITHOUT TIME ZONE
);