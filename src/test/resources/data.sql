INSERT INTO roles (name) VALUES ('USER'), ('ADMIN');

INSERT INTO profiles (first_name, last_name, phone_number) VALUES ('John', 'Doe', '1234567890');
INSERT INTO profiles (first_name) VALUES (null), (null);

INSERT INTO users (email, password, role_id, profile_id) VALUES ('test@user.com', '$argon2id$v=19$m=65536,t=2,p=1$bHlUBiu87fRfQjt6Cd1hig$vdkbLhjaMvNYM4QjFVeFGA', 1, 1);
INSERT INTO users (email, password, role_id, profile_id) VALUES ('test@user2.com', '$argon2id$v=19$m=65536,t=2,p=1$bHlUBiu87fRfQjt6Cd1hig$vdkbLhjaMvNYM4QjFVeFGA', 1, 2);
INSERT INTO users (email, password, role_id, profile_id) VALUES ('test@admin.com', '$argon2id$v=19$m=65536,t=2,p=1$bHlUBiu87fRfQjt6Cd1hig$vdkbLhjaMvNYM4QjFVeFGA', 2, 3);

INSERT INTO airplanes (name, code, seats, serial_number) VALUES ( 'Boeing 777', 'W627', 120, 'SN-123456789' );

INSERT INTO countries (name, code) VALUES ('United States of America', 'US'), ('Brazil', 'BR');
INSERT INTO cities (name, country_id) VALUES ('City A', 1), ('City B', 2);
INSERT INTO airports (city_id, icao) VALUES (1, 'AAAA'), (2, 'BBBB');

INSERT INTO flights (airplane_id, airport_from_id, airport_to_id, departure, arrival, price) VALUES ( 1, 1, 2, '2020-01-01 00:00:00', '2020-01-01 03:00:00', 100.00);
INSERT INTO flights (airplane_id, airport_from_id, airport_to_id, departure, arrival, price) VALUES ( 1, 1, 2, '2120-01-01 00:00:00', '2120-01-01 03:00:00', 100.00);
INSERT INTO flights (airplane_id, airport_from_id, airport_to_id, departure, arrival, price, deleted_at) VALUES ( 1, 1, 2, '2120-01-01 04:00:00', '2120-01-01 07:00:00', 100.00, '2025-01-01 00:00:00');