CREATE TABLE items (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description TEXT NOT NULL,
    price DECIMAL NOT NULL,
    category VARCHAR(50) NOT NULL,
    image bytea NOT NULL
);