CREATE TABLE IF NOT EXISTS products (
    id bigserial primary key,
    name text,
    description text,
    price double precision
);