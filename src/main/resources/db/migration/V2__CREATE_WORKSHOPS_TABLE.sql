CREATE SEQUENCE IF NOT EXISTS workshops_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS workshops
(
    id         INTEGER      NOT NULL DEFAULT nextval('workshops_id_seq') PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    slug       VARCHAR(50)  NOT NULL,
    tax_id     VARCHAR(14),
    phone      VARCHAR(20),
    email      VARCHAR(100),
    active     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uk_workshops_slug UNIQUE (slug)
);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_constraint WHERE conname = 'uk_workshops_tax_id'
    ) THEN
        ALTER TABLE workshops ADD CONSTRAINT uk_workshops_tax_id UNIQUE (tax_id);
    END IF;
END $$;

INSERT INTO workshops (name, slug, active, created_at, updated_at)
VALUES ('Oficina Padrão', 'oficina-padrao', TRUE, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
ON CONFLICT (slug) DO NOTHING;
