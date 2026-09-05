--- Authenticated users identity.
CREATE TABLE users (
    id              UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    email           VARCHAR(254)    NOT NULL,
    password_hash   VARCHAR(100)    NOT NULL,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT now(),

    CONSTRAINT users_email_key UNIQUE (email),

    CONSTRAINT users_email_lowercase CHECK (email = lower(email))
);
