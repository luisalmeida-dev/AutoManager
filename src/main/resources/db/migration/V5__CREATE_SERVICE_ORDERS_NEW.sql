-- ============================================================
-- Fase 4 — Recria service_orders no modelo multi-tenant (com
--          snapshots) e adiciona service_order_items e
--          service_order_events (histórico imutável).
-- ============================================================

-- 1) Derruba service_orders legado (banco vazio em dev)
DROP TABLE IF EXISTS service_orders CASCADE;

-- 2) service_orders (novo) --------------------------------------------------
CREATE SEQUENCE IF NOT EXISTS service_orders_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS service_orders
(
    id                          INTEGER        NOT NULL DEFAULT nextval('service_orders_id_seq') PRIMARY KEY,
    workshop_id                 INTEGER        NOT NULL,
    vehicle_id                  INTEGER,
    customer_id                 INTEGER,
    opened_by_employee_id       INTEGER,
    evaluated_by_employee_id    INTEGER,
    status                      VARCHAR(30)    NOT NULL,
    evaluation_notes            TEXT,
    evaluated_at                TIMESTAMPTZ,
    snapshot_customer_name      VARCHAR(100)   NOT NULL,
    snapshot_customer_phone     VARCHAR(20),
    snapshot_customer_email     VARCHAR(100),
    snapshot_customer_cpf       VARCHAR(11),
    snapshot_vehicle_plate      VARCHAR(10)    NOT NULL,
    snapshot_vehicle_make_model VARCHAR(100)   NOT NULL,
    snapshot_vehicle_year       INTEGER,
    snapshot_vehicle_color      VARCHAR(30),
    estimate                    DECIMAL(10, 2),
    final_value                 DECIMAL(10, 2),
    created_at                  TIMESTAMPTZ    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at                  TIMESTAMPTZ    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    closed_at                   TIMESTAMPTZ,
    CONSTRAINT fk_service_orders_workshop FOREIGN KEY (workshop_id) REFERENCES workshops (id),
    CONSTRAINT fk_service_orders_vehicle FOREIGN KEY (vehicle_id) REFERENCES vehicles (id),
    CONSTRAINT fk_service_orders_customer FOREIGN KEY (customer_id) REFERENCES customers (id),
    CONSTRAINT fk_service_orders_opened_by FOREIGN KEY (opened_by_employee_id) REFERENCES employees (id),
    CONSTRAINT fk_service_orders_evaluated_by FOREIGN KEY (evaluated_by_employee_id) REFERENCES employees (id),
    CONSTRAINT ck_service_orders_status CHECK (status IN
                                                ('EVALUATION', 'IN_PROGRESS', 'ON_HOLD', 'COMPLETED', 'CANCELLED'))
);

CREATE INDEX IF NOT EXISTS idx_service_orders_workshop_id ON service_orders (workshop_id);
CREATE INDEX IF NOT EXISTS idx_service_orders_snapshot_plate ON service_orders (workshop_id, snapshot_vehicle_plate);
CREATE INDEX IF NOT EXISTS idx_service_orders_vehicle_id ON service_orders (vehicle_id);
CREATE INDEX IF NOT EXISTS idx_service_orders_customer_id ON service_orders (customer_id);
CREATE INDEX IF NOT EXISTS idx_service_orders_status ON service_orders (workshop_id, status);

-- 3) service_order_items -----------------------------------------------------
CREATE SEQUENCE IF NOT EXISTS service_order_items_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS service_order_items
(
    id                       INTEGER        NOT NULL DEFAULT nextval('service_order_items_id_seq') PRIMARY KEY,
    service_order_id         INTEGER        NOT NULL,
    catalog_service_id       INTEGER,
    description              TEXT           NOT NULL,
    specialty_id             INTEGER,
    assigned_employee_id     INTEGER,
    proposed_by_employee_id  INTEGER,
    created_by_employee_id   INTEGER,
    approved_by_employee_id  INTEGER,
    sequence_order           INTEGER,
    status                   VARCHAR(30)    NOT NULL,
    unit_price               DECIMAL(10, 2),
    rejection_reason         TEXT,
    created_at               TIMESTAMPTZ    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at               TIMESTAMPTZ    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    approved_at              TIMESTAMPTZ,
    started_at               TIMESTAMPTZ,
    completed_at             TIMESTAMPTZ,
    CONSTRAINT fk_service_order_items_order FOREIGN KEY (service_order_id) REFERENCES service_orders (id),
    CONSTRAINT fk_service_order_items_catalog FOREIGN KEY (catalog_service_id) REFERENCES catalog_services (id),
    CONSTRAINT fk_service_order_items_specialty FOREIGN KEY (specialty_id) REFERENCES specialties (id),
    CONSTRAINT fk_service_order_items_assigned FOREIGN KEY (assigned_employee_id) REFERENCES employees (id),
    CONSTRAINT fk_service_order_items_proposed_by FOREIGN KEY (proposed_by_employee_id) REFERENCES employees (id),
    CONSTRAINT fk_service_order_items_created_by FOREIGN KEY (created_by_employee_id) REFERENCES employees (id),
    CONSTRAINT fk_service_order_items_approved_by FOREIGN KEY (approved_by_employee_id) REFERENCES employees (id),
    CONSTRAINT ck_service_order_items_status CHECK (status IN
                                                      ('PENDING_APPROVAL', 'REJECTED', 'PENDING', 'IN_PROGRESS',
                                                       'COMPLETED', 'CANCELLED'))
);

CREATE INDEX IF NOT EXISTS idx_service_order_items_order_id ON service_order_items (service_order_id);
CREATE INDEX IF NOT EXISTS idx_service_order_items_assigned_employee ON service_order_items (assigned_employee_id);
CREATE INDEX IF NOT EXISTS idx_service_order_items_specialty_id ON service_order_items (specialty_id);
CREATE INDEX IF NOT EXISTS idx_service_order_items_status ON service_order_items (status);

-- 4) service_order_events (log imutável) ------------------------------------
CREATE SEQUENCE IF NOT EXISTS service_order_events_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS service_order_events
(
    id                          INTEGER      NOT NULL DEFAULT nextval('service_order_events_id_seq') PRIMARY KEY,
    workshop_id                 INTEGER      NOT NULL,
    service_order_id            INTEGER      NOT NULL,
    service_order_item_id       INTEGER,
    event_type                  VARCHAR(30)  NOT NULL,
    performed_by_employee_id    INTEGER      NOT NULL,
    occurred_at                 TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    notes                       TEXT,
    previous_status             VARCHAR(30),
    new_status                  VARCHAR(30),
    payload                     JSONB,
    CONSTRAINT fk_service_order_events_workshop FOREIGN KEY (workshop_id) REFERENCES workshops (id),
    CONSTRAINT fk_service_order_events_order FOREIGN KEY (service_order_id) REFERENCES service_orders (id),
    CONSTRAINT fk_service_order_events_item FOREIGN KEY (service_order_item_id) REFERENCES service_order_items (id),
    CONSTRAINT fk_service_order_events_performed_by FOREIGN KEY (performed_by_employee_id) REFERENCES employees (id)
);

CREATE INDEX IF NOT EXISTS idx_service_order_events_workshop_id ON service_order_events (workshop_id);
CREATE INDEX IF NOT EXISTS idx_service_order_events_order_id ON service_order_events (service_order_id);
CREATE INDEX IF NOT EXISTS idx_service_order_events_occurred_at ON service_order_events (occurred_at);
CREATE INDEX IF NOT EXISTS idx_service_order_events_type ON service_order_events (workshop_id, event_type);
