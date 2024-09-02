CREATE TABLE IF NOT EXISTS outbox (
    event_id UUID PRIMARY KEY,
    event_type text NOT NULL,
    event_payload text NOT NULL,
    topic text NOT NULL
);