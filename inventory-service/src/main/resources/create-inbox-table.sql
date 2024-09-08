CREATE TABLE IF NOT EXISTS inbox (
    id UUID PRIMARY KEY,
    event_id UUID NOT NULL,
    event_type text NOT NULL,
    topic text NOT NULL
);