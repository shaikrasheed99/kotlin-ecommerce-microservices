CREATE TABLE IF NOT EXISTS notifications (
    id UUID PRIMARY KEY,
    sender text NOT NULL,
    recipient text NOT NULL,
    is_sent bool NOT NULL default false,
    order_id UUID NOT NULL,
    sku_code text NOT NULL,
    created_at timestamp NOT NULL default current_timestamp
);