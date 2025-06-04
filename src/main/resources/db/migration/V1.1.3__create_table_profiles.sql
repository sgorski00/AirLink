CREATE TABLE IF NOT EXISTS profiles (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(50),
    last_name VARCHAR(50),
    phone_number VARCHAR(15),
    country VARCHAR(50),
    zip VARCHAR(20),
    city VARCHAR(50),
    street VARCHAR(100)
);

ALTER TABLE users ADD COLUMN profile_id BIGINT REFERENCES profiles(id);