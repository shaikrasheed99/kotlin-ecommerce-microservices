CREATE TABLE IF NOT EXISTS orders (
    id UUID PRIMARY KEY,
    sku_code text NOT NULL,
    price DECIMAL NOT NULL,
    quantity INT NOT NULL
);