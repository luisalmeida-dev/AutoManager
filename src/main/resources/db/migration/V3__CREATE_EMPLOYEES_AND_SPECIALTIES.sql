CREATE SEQUENCE IF NOT EXISTS specialties_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS specialties
(
    id          INTEGER     NOT NULL DEFAULT nextval('specialties_id_seq') PRIMARY KEY,
    workshop_id INTEGER     NOT NULL,
    name        VARCHAR(50) NOT NULL,
    description TEXT,
    active      BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_specialties_workshop FOREIGN KEY (workshop_id) REFERENCES workshops (id),
    CONSTRAINT uk_specialties_workshop_name UNIQUE (workshop_id, name)
);

CREATE INDEX IF NOT EXISTS idx_specialties_workshop_id ON specialties (workshop_id);

CREATE SEQUENCE IF NOT EXISTS employees_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS employees
(
    id          INTEGER      NOT NULL DEFAULT nextval('employees_id_seq') PRIMARY KEY,
    workshop_id INTEGER      NOT NULL,
    name        VARCHAR(100) NOT NULL,
    login       VARCHAR(50)  NOT NULL,
    email       VARCHAR(100),
    password    VARCHAR(255) NOT NULL,
    access_role VARCHAR(30)  NOT NULL,
    active      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_employees_workshop FOREIGN KEY (workshop_id) REFERENCES workshops (id),
    CONSTRAINT uk_employees_workshop_login UNIQUE (workshop_id, login),
    CONSTRAINT ck_employees_access_role CHECK (access_role IN ('MANAGER', 'EMPLOYEE'))
);

CREATE INDEX IF NOT EXISTS idx_employees_workshop_id ON employees (workshop_id);

CREATE SEQUENCE IF NOT EXISTS employee_specialties_id_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE IF NOT EXISTS employee_specialties
(
    id                      INTEGER     NOT NULL DEFAULT nextval('employee_specialties_id_seq') PRIMARY KEY,
    workshop_id             INTEGER     NOT NULL,
    employee_id             INTEGER     NOT NULL,
    specialty_id            INTEGER     NOT NULL,
    assigned_by_employee_id INTEGER,
    created_at              TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_employee_specialties_workshop FOREIGN KEY (workshop_id) REFERENCES workshops (id),
    CONSTRAINT fk_employee_specialties_employee FOREIGN KEY (employee_id) REFERENCES employees (id),
    CONSTRAINT fk_employee_specialties_specialty FOREIGN KEY (specialty_id) REFERENCES specialties (id),
    CONSTRAINT fk_employee_specialties_assigned_by FOREIGN KEY (assigned_by_employee_id) REFERENCES employees (id),
    CONSTRAINT uk_employee_specialties_pair UNIQUE (employee_id, specialty_id)
);

CREATE INDEX IF NOT EXISTS idx_employee_specialties_workshop_id ON employee_specialties (workshop_id);
CREATE INDEX IF NOT EXISTS idx_employee_specialties_employee_id ON employee_specialties (employee_id);
CREATE INDEX IF NOT EXISTS idx_employee_specialties_specialty_id ON employee_specialties (specialty_id);
