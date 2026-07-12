-- ============================================================
-- Fase 3 — Cria catalog_services e vehicles; evolui customers
--          para o modelo multi-tenant.
-- ============================================================

-- catalog_services --------------------------------------------------------
CREATE SEQUENCE IF NOT EXISTS catalog_services_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS catalog_services
(
    id            INTEGER      NOT NULL DEFAULT nextval('catalog_services_id_seq') PRIMARY KEY,
    workshop_id   INTEGER      NOT NULL,
    specialty_id  INTEGER,
    name          VARCHAR(100) NOT NULL,
    description   TEXT,
    default_price DECIMAL(10, 2),
    active        BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_catalog_services_workshop FOREIGN KEY (workshop_id) REFERENCES workshops (id),
    CONSTRAINT fk_catalog_services_specialty FOREIGN KEY (specialty_id) REFERENCES specialties (id),
    CONSTRAINT uk_catalog_services_workshop_name UNIQUE (workshop_id, name)
);

CREATE INDEX IF NOT EXISTS idx_catalog_services_workshop_id ON catalog_services (workshop_id);
CREATE INDEX IF NOT EXISTS idx_catalog_services_specialty_id ON catalog_services (specialty_id);

-- vehicles ----------------------------------------------------------------
CREATE SEQUENCE IF NOT EXISTS vehicles_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS vehicles
(
    id               INTEGER      NOT NULL DEFAULT nextval('vehicles_id_seq') PRIMARY KEY,
    workshop_id      INTEGER      NOT NULL,
    customer_id      INTEGER      NOT NULL,
    plate            VARCHAR(10)  NOT NULL,
    make_model       VARCHAR(100) NOT NULL,
    manufacture_year INTEGER,
    color            VARCHAR(30),
    active           BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at       TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_vehicles_workshop FOREIGN KEY (workshop_id) REFERENCES workshops (id),
    CONSTRAINT fk_vehicles_customer FOREIGN KEY (customer_id) REFERENCES customers (id),
    CONSTRAINT uk_vehicles_workshop_plate UNIQUE (workshop_id, plate)
);

CREATE INDEX IF NOT EXISTS idx_vehicles_workshop_id ON vehicles (workshop_id);
CREATE INDEX IF NOT EXISTS idx_vehicles_customer_id ON vehicles (customer_id);

-- customers — evolução ao modelo multi-tenant -----------------------------

-- 1) Colunas novas (nullable/default para acomodar linhas legadas)
ALTER TABLE customers ADD COLUMN IF NOT EXISTS workshop_id INTEGER;
ALTER TABLE customers ADD COLUMN IF NOT EXISTS active BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE customers ADD COLUMN IF NOT EXISTS created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE customers ADD COLUMN IF NOT EXISTS updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP;

-- 2) Preenche workshop_id em linhas existentes (aponta pra Oficina Padrão da V2)
UPDATE customers SET workshop_id = 1 WHERE workshop_id IS NULL;

-- 3) Endurece workshop_id
ALTER TABLE customers ALTER COLUMN workshop_id SET NOT NULL;

-- 4) Remove coluna legada "role" (tipo de cliente saiu do modelo)
ALTER TABLE customers DROP COLUMN IF EXISTS role;

-- 5) Amplia VARCHARs e afrouxa nullabilidade conforme docs de arquitetura
ALTER TABLE customers ALTER COLUMN name TYPE VARCHAR(100);
ALTER TABLE customers ALTER COLUMN email TYPE VARCHAR(100);
ALTER TABLE customers ALTER COLUMN phone TYPE VARCHAR(20);
ALTER TABLE customers ALTER COLUMN address TYPE VARCHAR(200);

ALTER TABLE customers ALTER COLUMN cpf DROP NOT NULL;
ALTER TABLE customers ALTER COLUMN email DROP NOT NULL;
ALTER TABLE customers ALTER COLUMN phone DROP NOT NULL;
ALTER TABLE customers ALTER COLUMN address DROP NOT NULL;

-- 6) Substitui UNIQUE (cpf) global por UNIQUE parcial (workshop_id, cpf) onde cpf não nulo
DO $$
BEGIN
    IF EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'uk_customers_cpf') THEN
        ALTER TABLE customers DROP CONSTRAINT uk_customers_cpf;
    END IF;
END $$;

CREATE UNIQUE INDEX IF NOT EXISTS uk_customers_workshop_cpf
    ON customers (workshop_id, cpf) WHERE cpf IS NOT NULL;

-- 7) FK para workshops (idempotente)
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_constraint WHERE conname = 'fk_customers_workshop') THEN
        ALTER TABLE customers ADD CONSTRAINT fk_customers_workshop
            FOREIGN KEY (workshop_id) REFERENCES workshops (id);
    END IF;
END $$;

CREATE INDEX IF NOT EXISTS idx_customers_workshop_id ON customers (workshop_id);
