CREATE TABLE IF NOT EXISTS inventory (
    id serial PRIMARY KEY,
    sku_code text NOT NULL,
    quantity INT NOT NULL
);